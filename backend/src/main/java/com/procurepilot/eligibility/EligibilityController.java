package com.procurepilot.eligibility;

import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import com.procurepilot.common.ResourceNotFoundException;
import com.procurepilot.matching.StartupTenderMatch;
import com.procurepilot.matching.StartupTenderMatchRepository;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupRepository;
import com.procurepilot.tender.TenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EligibilityController {

    private final StartupRepository startupRepository;
    private final StartupTenderMatchRepository matchRepository;
    private final EligibilityRuleRepository ruleRepository;
    private final TenderService tenderService;

    public EligibilityController(StartupRepository startupRepository,
                                 StartupTenderMatchRepository matchRepository,
                                 EligibilityRuleRepository ruleRepository,
                                 TenderService tenderService) {
        this.startupRepository = startupRepository;
        this.matchRepository = matchRepository;
        this.ruleRepository = ruleRepository;
        this.tenderService = tenderService;
    }

    @GetMapping("/tenders/{id}/eligibility")
    public ResponseEntity<ApiResponse<EligibilityDtos.EvaluationSummaryDto>> getTenderEligibility(
            @PathVariable("id") Long tenderId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        Startup startup = startupRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> startupRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found.")));

        StartupTenderMatch match = matchRepository.findByStartupIdAndTenderId(startup.getId(), tenderId)
                .orElseGet(() -> tenderService.analyseTender(tenderId, currentUser.getId()));

        EligibilityDtos.EvaluationSummaryDto summary = new EligibilityDtos.EvaluationSummaryDto();
        summary.setTenderId(tenderId);
        summary.setStartupId(startup.getId());
        summary.setOverallMatchScore(match.getOverallScore());
        summary.setOverallStatus(match.getOverallScore() >= 80 ? "PASS" : (match.getOverallScore() >= 50 ? "NEEDS_REVIEW" : "FAIL"));

        List<EligibilityDtos.EvaluationItemDto> items = match.getEvaluations().stream()
                .map(EligibilityDtos.EvaluationItemDto::new)
                .collect(Collectors.toList());
        summary.setEvaluations(items);

        List<EligibilityDtos.RuleCitationDto> rules = match.getEvaluations().stream()
                .filter(e -> e.getAppliedRelaxationRule() != null)
                .map(e -> new EligibilityDtos.RuleCitationDto(e.getAppliedRelaxationRule()))
                .distinct()
                .collect(Collectors.toList());
        summary.setAppliedRelaxations(rules);

        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/admin/rules")
    public ResponseEntity<ApiResponse<List<EligibilityRule>>> listRules() {
        return ResponseEntity.ok(ApiResponse.ok(ruleRepository.findAll()));
    }

    @PostMapping("/admin/rules")
    public ResponseEntity<ApiResponse<EligibilityRule>> saveRule(@RequestBody EligibilityRule rule) {
        EligibilityRule saved = ruleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.ok("Rule saved successfully", saved));
    }
}
