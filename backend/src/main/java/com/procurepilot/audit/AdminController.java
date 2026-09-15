package com.procurepilot.audit;

import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import com.procurepilot.common.ResourceNotFoundException;
import com.procurepilot.requirement.RequirementDto;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.requirement.TenderRequirementRepository;
import com.procurepilot.startup.StartupRepository;
import com.procurepilot.tender.TenderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ROLE_PROCUREMENT_ADMIN', 'ROLE_SYSTEM_ADMIN')")
public class AdminController {

    private final TenderRequirementRepository requirementRepository;
    private final AuditService auditService;
    private final TenderRepository tenderRepository;
    private final StartupRepository startupRepository;

    public AdminController(TenderRequirementRepository requirementRepository,
                           AuditService auditService,
                           TenderRepository tenderRepository,
                           StartupRepository startupRepository) {
        this.requirementRepository = requirementRepository;
        this.auditService = auditService;
        this.tenderRepository = tenderRepository;
        this.startupRepository = startupRepository;
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<RequirementDto>>> getReviewQueue() {
        List<TenderRequirement> list = requirementRepository.findAll().stream()
                .filter(r -> "NEEDS_REVIEW".equalsIgnoreCase(r.getReviewStatus()) || "LOW".equalsIgnoreCase(r.getConfidence()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(list.stream().map(RequirementDto::new).collect(Collectors.toList())));
    }

    @PatchMapping("/requirements/{id}")
    public ResponseEntity<ApiResponse<RequirementDto>> updateRequirement(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        TenderRequirement req = requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found: " + id));

        if (payload.containsKey("reviewStatus")) req.setReviewStatus((String) payload.get("reviewStatus"));
        if (payload.containsKey("normalizedValue")) req.setNormalizedValue((String) payload.get("normalizedValue"));
        if (payload.containsKey("mandatory")) req.setMandatory((Boolean) payload.get("mandatory"));

        TenderRequirement saved = requirementRepository.save(req);

        auditService.log(
                currentUser != null ? currentUser.getId() : null,
                currentUser != null ? currentUser.getUsername() : "admin",
                "REQUIREMENT_UPDATED",
                "TenderRequirement",
                id.toString(),
                "127.0.0.1",
                "Updated status to " + req.getReviewStatus()
        );

        return ResponseEntity.ok(ApiResponse.ok("Requirement updated", new RequirementDto(saved)));
    }

    @GetMapping({"/audit", "/audit-logs"})
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getRecentLogs()));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStartups", startupRepository.count());
        stats.put("totalTenders", tenderRepository.count());
        stats.put("pendingReviews", requirementRepository.findAll().stream().filter(r -> "NEEDS_REVIEW".equalsIgnoreCase(r.getReviewStatus())).count());
        stats.put("totalRequirements", requirementRepository.count());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
