package com.procurepilot.opportunity;

import com.procurepilot.common.BadRequestException;
import com.procurepilot.common.ResourceNotFoundException;
import com.procurepilot.compliance.ComplianceItem;
import com.procurepilot.compliance.ComplianceItemRepository;
import com.procurepilot.compliance.ComplianceStatus;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupRepository;
import com.procurepilot.tender.Tender;
import com.procurepilot.tender.TenderDtos;
import com.procurepilot.tender.TenderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityTaskRepository taskRepository;
    private final StartupRepository startupRepository;
    private final TenderRepository tenderRepository;
    private final ComplianceItemRepository complianceItemRepository;

    public OpportunityService(OpportunityRepository opportunityRepository,
                              OpportunityTaskRepository taskRepository,
                              StartupRepository startupRepository,
                              TenderRepository tenderRepository,
                              ComplianceItemRepository complianceItemRepository) {
        this.opportunityRepository = opportunityRepository;
        this.taskRepository = taskRepository;
        this.startupRepository = startupRepository;
        this.tenderRepository = tenderRepository;
        this.complianceItemRepository = complianceItemRepository;
    }

    @Transactional
    public List<OpportunityDtos.OpportunityDto> listOpportunities(Long userId) {
        Startup startup = startupRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found."));

        List<Opportunity> opps = opportunityRepository.findByStartupIdOrderByUpdatedAtDesc(startup.getId());
        for (Opportunity opp : opps) {
            recalculateReadiness(opp);
            opportunityRepository.save(opp);
        }
        return opps.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public OpportunityDtos.OpportunityDto createOpportunity(Long userId, OpportunityDtos.CreateOpportunityRequest request) {
        Startup startup = startupRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found."));

        Tender tender = tenderRepository.findById(request.getTenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found: " + request.getTenderId()));

        Opportunity opp = opportunityRepository.findByStartupIdAndTenderId(startup.getId(), tender.getId())
                .orElseGet(() -> {
                    Opportunity newOpp = new Opportunity();
                    newOpp.setStartup(startup);
                    newOpp.setTender(tender);
                    return newOpp;
                });

        if (request.getStage() != null) opp.setStage(request.getStage());
        if (request.getPriority() != null) opp.setPriority(request.getPriority());
        if (request.getNotes() != null) opp.setNotes(request.getNotes());
        opp.setUpdatedAt(Instant.now());

        // Calculate readiness based on compliance items
        recalculateReadiness(opp);

        Opportunity saved = opportunityRepository.save(opp);
        return toDto(saved);
    }

    @Transactional
    public OpportunityDtos.OpportunityDto updateStage(Long oppId, OpportunityStage stage, Long userId) {
        Startup startup = startupRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found."));

        Opportunity opp = opportunityRepository.findById(oppId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + oppId));

        if (!opp.getStartup().getId().equals(startup.getId())) {
            throw new BadRequestException("Unauthorized access to opportunity");
        }

        opp.setStage(stage);
        opp.setUpdatedAt(Instant.now());
        recalculateReadiness(opp);

        Opportunity saved = opportunityRepository.save(opp);
        return toDto(saved);
    }

    @Transactional
    public OpportunityDtos.TaskDto addTask(Long oppId, String title, String description, LocalDate dueDate, Long userId) {
        Startup startup = startupRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found."));

        Opportunity opp = opportunityRepository.findById(oppId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + oppId));

        if (!opp.getStartup().getId().equals(startup.getId())) {
            throw new BadRequestException("Unauthorized access to opportunity");
        }

        OpportunityTask task = new OpportunityTask();
        task.setOpportunity(opp);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusDays(5));
        task.setCompleted(false);

        OpportunityTask saved = taskRepository.save(task);
        return new OpportunityDtos.TaskDto(saved);
    }

    @Transactional
    public OpportunityDtos.TaskDto toggleTask(Long taskId, Long userId) {
        Startup startup = startupRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found."));

        OpportunityTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        if (!task.getOpportunity().getStartup().getId().equals(startup.getId())) {
            throw new BadRequestException("Unauthorized access to task");
        }

        task.setCompleted(!Boolean.TRUE.equals(task.getCompleted()));
        task.setCompletedAt(Boolean.TRUE.equals(task.getCompleted()) ? Instant.now() : null);

        OpportunityTask saved = taskRepository.save(task);
        return new OpportunityDtos.TaskDto(saved);
    }

    private void recalculateReadiness(Opportunity opp) {
        List<ComplianceItem> items = complianceItemRepository.findByTenderIdAndStartupId(opp.getTender().getId(), opp.getStartup().getId());
        if (items.isEmpty()) {
            opp.setReadinessPercent(40); // default baseline
            return;
        }

        long readyCount = items.stream().filter(i -> i.getStatus() == ComplianceStatus.READY || i.getStatus() == ComplianceStatus.VERIFIED).count();
        int percent = (int) Math.round(((double) readyCount / items.size()) * 100);
        opp.setReadinessPercent(percent);
    }

    private OpportunityDtos.OpportunityDto toDto(Opportunity opp) {
        OpportunityDtos.OpportunityDto dto = new OpportunityDtos.OpportunityDto();
        dto.setId(opp.getId());
        dto.setStartupId(opp.getStartup().getId());
        dto.setTenderId(opp.getTender().getId());
        dto.setStage(opp.getStage());
        dto.setPriority(opp.getPriority());
        dto.setReadinessPercent(opp.getReadinessPercent());
        dto.setTargetSubmissionDate(opp.getTargetSubmissionDate());
        dto.setNotes(opp.getNotes());
        dto.setUpdatedAt(opp.getUpdatedAt());

        Tender t = opp.getTender();
        TenderDtos.TenderSummaryDto tDto = new TenderDtos.TenderSummaryDto();
        tDto.setId(t.getId());
        tDto.setTenderRefNo(t.getTenderRefNo());
        tDto.setTitle(t.getTitle());
        tDto.setDepartment(t.getDepartment());
        tDto.setAuthority(t.getAuthority());
        tDto.setCategory(t.getCategory());
        tDto.setEstimatedValueInr(t.getEstimatedValueInr());
        tDto.setEmdInr(t.getEmdInr());
        tDto.setClosingDate(t.getClosingDate());
        tDto.setSourcePortal(t.getSourcePortal());
        tDto.setStatus(t.getStatus());

        if (t.getClosingDate() != null) {
            long days = Duration.between(Instant.now(), t.getClosingDate()).toDays();
            tDto.setDaysRemaining((int) Math.max(0, days));
        }

        dto.setTender(tDto);

        List<OpportunityTask> tasks = taskRepository.findByOpportunityId(opp.getId());
        dto.setTasks(tasks.stream().map(OpportunityDtos.TaskDto::new).collect(Collectors.toList()));

        return dto;
    }
}
