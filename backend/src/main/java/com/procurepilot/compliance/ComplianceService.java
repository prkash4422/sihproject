package com.procurepilot.compliance;

import com.procurepilot.common.ResourceNotFoundException;
import com.procurepilot.opportunity.Opportunity;
import com.procurepilot.opportunity.OpportunityRepository;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.requirement.TenderRequirementRepository;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupDocument;
import com.procurepilot.startup.StartupDocumentRepository;
import com.procurepilot.startup.StartupRepository;
import com.procurepilot.tender.Tender;
import com.procurepilot.tender.TenderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComplianceService {

    private final ComplianceItemRepository itemRepository;
    private final ComplianceEvidenceRepository evidenceRepository;
    private final StartupRepository startupRepository;
    private final StartupDocumentRepository documentRepository;
    private final TenderRepository tenderRepository;
    private final TenderRequirementRepository requirementRepository;
    private final OpportunityRepository opportunityRepository;

    public ComplianceService(ComplianceItemRepository itemRepository,
                             ComplianceEvidenceRepository evidenceRepository,
                             StartupRepository startupRepository,
                             StartupDocumentRepository documentRepository,
                             TenderRepository tenderRepository,
                             TenderRequirementRepository requirementRepository,
                             OpportunityRepository opportunityRepository) {
        this.itemRepository = itemRepository;
        this.evidenceRepository = evidenceRepository;
        this.startupRepository = startupRepository;
        this.documentRepository = documentRepository;
        this.tenderRepository = tenderRepository;
        this.requirementRepository = requirementRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional
    public List<ComplianceDtos.ComplianceItemDto> getChecklist(Long tenderId, Long userId) {
        Startup startup = (userId != null ? startupRepository.findByUserId(userId) : java.util.Optional.<Startup>empty())
                .orElseGet(() -> startupRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found.")));

        List<ComplianceItem> items = itemRepository.findByTenderIdAndStartupId(tenderId, startup.getId());

        // Auto-generate dynamic checklist if first time viewing tender
        if (items.isEmpty()) {
            Tender tender = tenderRepository.findById(tenderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tender not found: " + tenderId));
            List<TenderRequirement> requirements = requirementRepository.findByTenderId(tenderId);

            items = new ArrayList<>();
            for (TenderRequirement req : requirements) {
                ComplianceItem item = new ComplianceItem();
                item.setTender(tender);
                item.setStartup(startup);
                item.setTitle(req.getType() + ": " + (req.getNormalizedValue() != null ? req.getNormalizedValue() : req.getRequirementText()));
                item.setCategory(req.getType().name());
                item.setMandatory(req.getMandatory());
                item.setTenderSourcePage(req.getSourcePage());
                item.setStatus(ComplianceStatus.IN_PROGRESS);
                item.setDueDate(LocalDate.now().plusDays(7));
                items.add(itemRepository.save(item));
            }
        }

        return items.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ComplianceDtos.ComplianceItemDto linkEvidence(Long itemId, Long documentId, Long userId) {
        Startup startup = (userId != null ? startupRepository.findByUserId(userId) : java.util.Optional.<Startup>empty())
                .orElseGet(() -> startupRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found.")));

        ComplianceItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance item not found: " + itemId));

        if (!item.getStartup().getId().equals(startup.getId())) {
            throw new ResourceNotFoundException("Unauthorized access to compliance item");
        }

        StartupDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        ComplianceEvidence evidence = new ComplianceEvidence();
        evidence.setComplianceItem(item);
        evidence.setDocument(doc);
        evidence.setVerificationStatus("VERIFIED");
        evidence.setVerifiedBy("User Self-Certification");
        evidence.setVerifiedAt(Instant.now());

        evidenceRepository.save(evidence);

        item.setStatus(ComplianceStatus.READY);
        ComplianceItem savedItem = itemRepository.save(item);

        syncOpportunityReadiness(startup.getId(), item.getTender().getId());

        return toDto(savedItem);
    }

    @Transactional
    public ComplianceDtos.ComplianceItemDto updateStatus(Long itemId, ComplianceStatus status, String notes, Long userId) {
        Startup startup = (userId != null ? startupRepository.findByUserId(userId) : java.util.Optional.<Startup>empty())
                .orElseGet(() -> startupRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found.")));

        ComplianceItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance item not found: " + itemId));

        if (!item.getStartup().getId().equals(startup.getId())) {
            throw new ResourceNotFoundException("Unauthorized access to compliance item");
        }

        if (status != null) item.setStatus(status);
        if (notes != null) item.setNotes(notes);

        ComplianceItem saved = itemRepository.save(item);

        syncOpportunityReadiness(startup.getId(), item.getTender().getId());

        return toDto(saved);
    }

    private void syncOpportunityReadiness(Long startupId, Long tenderId) {
        List<ComplianceItem> items = itemRepository.findByTenderIdAndStartupId(tenderId, startupId);
        if (items.isEmpty()) return;

        long readyCount = items.stream().filter(i -> i.getStatus() == ComplianceStatus.READY || i.getStatus() == ComplianceStatus.VERIFIED).count();
        int percent = (int) Math.round(((double) readyCount / items.size()) * 100);

        Optional<Opportunity> oppOpt = opportunityRepository.findByStartupIdAndTenderId(startupId, tenderId);
        if (oppOpt.isPresent()) {
            Opportunity opp = oppOpt.get();
            opp.setReadinessPercent(percent);
            opp.setUpdatedAt(Instant.now());
            opportunityRepository.save(opp);
        }
    }

    private ComplianceDtos.ComplianceItemDto toDto(ComplianceItem item) {
        ComplianceDtos.ComplianceItemDto dto = new ComplianceDtos.ComplianceItemDto();
        dto.setId(item.getId());
        dto.setTenderId(item.getTender().getId());
        dto.setStartupId(item.getStartup().getId());
        dto.setTitle(item.getTitle());
        dto.setCategory(item.getCategory());
        dto.setMandatory(item.getMandatory());
        dto.setStatus(item.getStatus());
        dto.setTenderSourcePage(item.getTenderSourcePage());
        dto.setDueDate(item.getDueDate());
        dto.setNotes(item.getNotes());

        List<ComplianceEvidence> evList = evidenceRepository.findByComplianceItemId(item.getId());
        List<ComplianceDtos.EvidenceDto> evDtos = evList.stream().map(e -> new ComplianceDtos.EvidenceDto(
                e.getId(),
                e.getDocument() != null ? e.getDocument().getId() : null,
                e.getDocument() != null ? e.getDocument().getFileName() : "Attached Document",
                e.getDocument() != null ? e.getDocument().getDocType() : "EVIDENCE",
                e.getDocument() != null ? e.getDocument().getFilePath() : null,
                e.getVerificationStatus(),
                e.getVerifiedBy()
        )).collect(Collectors.toList());

        dto.setEvidence(evDtos);
        return dto;
    }
}
