package com.procurepilot.tender;

import com.procurepilot.common.ResourceNotFoundException;
import com.procurepilot.matching.MatchingEngine;
import com.procurepilot.matching.StartupTenderMatch;
import com.procurepilot.matching.StartupTenderMatchRepository;
import com.procurepilot.requirement.RequirementDto;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.requirement.TenderRequirementRepository;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TenderService {

    private final TenderRepository tenderRepository;
    private final TenderSectionRepository sectionRepository;
    private final TenderRequirementRepository requirementRepository;
    private final StartupRepository startupRepository;
    private final StartupTenderMatchRepository matchRepository;
    private final MatchingEngine matchingEngine;

    public TenderService(TenderRepository tenderRepository,
                         TenderSectionRepository sectionRepository,
                         TenderRequirementRepository requirementRepository,
                         StartupRepository startupRepository,
                         StartupTenderMatchRepository matchRepository,
                         MatchingEngine matchingEngine) {
        this.tenderRepository = tenderRepository;
        this.sectionRepository = sectionRepository;
        this.requirementRepository = requirementRepository;
        this.startupRepository = startupRepository;
        this.matchRepository = matchRepository;
        this.matchingEngine = matchingEngine;
    }

    @Transactional(readOnly = true)
    public List<TenderDtos.TenderSummaryDto> listTenders(String query, String category, String department, Long userId) {
        List<Tender> tenders = tenderRepository.searchTenders(query, category, department);
        Optional<Startup> startupOpt = (userId != null ? startupRepository.findByUserId(userId) : Optional.<Startup>empty())
                .or(() -> startupRepository.findAll().stream().findFirst());

        return tenders.stream().map(t -> {
            TenderDtos.TenderSummaryDto dto = new TenderDtos.TenderSummaryDto();
            dto.setId(t.getId());
            dto.setTenderRefNo(t.getTenderRefNo());
            dto.setTitle(t.getTitle());
            dto.setDepartment(t.getDepartment());
            dto.setAuthority(t.getAuthority());
            dto.setCategory(t.getCategory());
            dto.setEstimatedValueInr(t.getEstimatedValueInr());
            dto.setEmdInr(t.getEmdInr());
            dto.setPublishedDate(t.getPublishedDate());
            dto.setClosingDate(t.getClosingDate());
            dto.setSourcePortal(t.getSourcePortal());
            dto.setSourceUrl(t.getSourceUrl());
            dto.setStatus(t.getStatus());

            if (t.getClosingDate() != null) {
                long days = Duration.between(Instant.now(), t.getClosingDate()).toDays();
                dto.setDaysRemaining((int) Math.max(0, days));
            }

            if (startupOpt.isPresent()) {
                Optional<StartupTenderMatch> matchOpt = matchRepository.findByStartupIdAndTenderId(startupOpt.get().getId(), t.getId());
                if (matchOpt.isPresent()) {
                    int score = matchOpt.get().getOverallScore();
                    dto.setMatchScore(score);
                    dto.setEligibilityStatus(score >= 75 ? "PASS" : (score >= 50 ? "NEEDS_REVIEW" : "FAIL"));
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TenderDtos.TenderDetailDto getTenderDetails(Long tenderId, Long userId) {
        Tender tender = tenderRepository.findById(tenderId)
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found with id: " + tenderId));

        TenderDtos.TenderDetailDto dto = new TenderDtos.TenderDetailDto();
        dto.setId(tender.getId());
        dto.setTenderRefNo(tender.getTenderRefNo());
        dto.setTitle(tender.getTitle());
        dto.setDepartment(tender.getDepartment());
        dto.setAuthority(tender.getAuthority());
        dto.setCategory(tender.getCategory());
        dto.setEstimatedValueInr(tender.getEstimatedValueInr());
        dto.setEmdInr(tender.getEmdInr());
        dto.setPublishedDate(tender.getPublishedDate());
        dto.setClosingDate(tender.getClosingDate());
        dto.setSourcePortal(tender.getSourcePortal());
        dto.setSourceUrl(tender.getSourceUrl());
        dto.setStatus(tender.getStatus());
        dto.setDocumentUrl(tender.getDocumentUrl());

        List<TenderSection> sections = sectionRepository.findByTenderId(tenderId);
        dto.setSections(sections.stream().map(TenderDtos.SectionDto::new).collect(Collectors.toList()));

        List<TenderRequirement> requirements = requirementRepository.findByTenderId(tenderId);
        dto.setRequirements(requirements.stream().map(RequirementDto::new).collect(Collectors.toList()));

        Optional<Startup> startupOpt = (userId != null ? startupRepository.findByUserId(userId) : Optional.<Startup>empty())
                .or(() -> startupRepository.findAll().stream().findFirst());

        if (startupOpt.isPresent()) {
            matchRepository.findByStartupIdAndTenderId(startupOpt.get().getId(), tenderId).ifPresent(match -> {
                int score = match.getOverallScore();
                dto.setMatchScore(score);
                dto.setEligibilityStatus(score >= 75 ? "PASS" : (score >= 50 ? "NEEDS_REVIEW" : "FAIL"));
            });
        }

        return dto;
    }

    @Transactional
    public StartupTenderMatch analyseTender(Long tenderId, Long userId) {
        Tender tender = tenderRepository.findById(tenderId)
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found: " + tenderId));

        Startup startup = (userId != null ? startupRepository.findByUserId(userId) : Optional.<Startup>empty())
                .orElseGet(() -> startupRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No startup profile available to evaluate")));

        List<TenderRequirement> requirements = requirementRepository.findByTenderId(tenderId);

        // Delete existing match for clean re-evaluation and flush
        Optional<StartupTenderMatch> existingOpt = matchRepository.findByStartupIdAndTenderId(startup.getId(), tenderId);
        if (existingOpt.isPresent()) {
            matchRepository.delete(existingOpt.get());
            matchRepository.flush();
        }

        StartupTenderMatch match = matchingEngine.calculateMatch(startup, tender, requirements);
        return matchRepository.save(match);
    }
}
