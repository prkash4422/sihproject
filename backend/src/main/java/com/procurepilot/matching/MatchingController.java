package com.procurepilot.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import com.procurepilot.common.ResourceNotFoundException;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupRepository;
import com.procurepilot.tender.TenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tenders")
public class MatchingController {

    private final StartupRepository startupRepository;
    private final StartupTenderMatchRepository matchRepository;
    private final TenderService tenderService;
    private final ObjectMapper objectMapper;

    public MatchingController(StartupRepository startupRepository,
                              StartupTenderMatchRepository matchRepository,
                              TenderService tenderService) {
        this.startupRepository = startupRepository;
        this.matchRepository = matchRepository;
        this.tenderService = tenderService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/{id}/match")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMatchDetails(
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

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("tenderId", tenderId);
        result.put("startupId", startup.getId());
        result.put("overallScore", match.getOverallScore());
        result.put("technicalScore", match.getTechnicalScore());
        result.put("sectorScore", match.getSectorScore());
        result.put("eligibilityScore", match.getEligibilityScore());
        result.put("readinessScore", match.getReadinessScore());
        result.put("fitScore", match.getFitScore());

        try {
            if (match.getExplanationJson() != null) {
                Map<String, Object> explanation = objectMapper.readValue(match.getExplanationJson(), Map.class);
                result.put("explanation", explanation);
            }
        } catch (Exception e) {
            result.put("explanation", Map.of("summary", "Match evaluation calculated."));
        }

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
