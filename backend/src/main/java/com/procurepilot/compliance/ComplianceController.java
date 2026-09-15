package com.procurepilot.compliance;

import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @GetMapping("/tender/{tenderId}")
    public ResponseEntity<ApiResponse<List<ComplianceDtos.ComplianceItemDto>>> getChecklist(
            @PathVariable("tenderId") Long tenderId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        List<ComplianceDtos.ComplianceItemDto> checklist = complianceService.getChecklist(tenderId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(checklist));
    }

    @PostMapping("/items/{itemId}/evidence")
    public ResponseEntity<ApiResponse<ComplianceDtos.ComplianceItemDto>> linkEvidence(
            @PathVariable("itemId") Long itemId,
            @RequestBody Map<String, Long> payload,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        Long documentId = payload.get("documentId");
        ComplianceDtos.ComplianceItemDto item = complianceService.linkEvidence(itemId, documentId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Evidence linked successfully", item));
    }

    @PatchMapping("/items/{itemId}/status")
    public ResponseEntity<ApiResponse<ComplianceDtos.ComplianceItemDto>> updateStatus(
            @PathVariable("itemId") Long itemId,
            @RequestBody ComplianceDtos.StatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        ComplianceDtos.ComplianceItemDto item = complianceService.updateStatus(itemId, request.getStatus(), request.getNotes(), currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Status updated", item));
    }
}
