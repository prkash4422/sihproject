package com.procurepilot.tender;

import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import com.procurepilot.matching.StartupTenderMatch;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenders")
public class TenderController {

    private final TenderService tenderService;

    public TenderController(TenderService tenderService) {
        this.tenderService = tenderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenderDtos.TenderSummaryDto>>> listTenders(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "department", required = false) String department,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        List<TenderDtos.TenderSummaryDto> tenders = tenderService.listTenders(query, category, department, userId);
        return ResponseEntity.ok(ApiResponse.ok(tenders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenderDtos.TenderDetailDto>> getTender(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        TenderDtos.TenderDetailDto detail = tenderService.getTenderDetails(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    @PostMapping("/{id}/analyse")
    public ResponseEntity<ApiResponse<StartupTenderMatch>> analyseTender(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required to analyse tenders."));
        }

        StartupTenderMatch match = tenderService.analyseTender(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Tender analysis completed successfully", match));
    }
}
