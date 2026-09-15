package com.procurepilot.opportunity;

import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OpportunityDtos.OpportunityDto>>> listOpportunities(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        List<OpportunityDtos.OpportunityDto> opps = opportunityService.listOpportunities(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(opps));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OpportunityDtos.OpportunityDto>> createOpportunity(
            @RequestBody OpportunityDtos.CreateOpportunityRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        OpportunityDtos.OpportunityDto opp = opportunityService.createOpportunity(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity tracked successfully", opp));
    }

    @PatchMapping("/{id}/stage")
    public ResponseEntity<ApiResponse<OpportunityDtos.OpportunityDto>> updateStage(
            @PathVariable("id") Long id,
            @RequestBody OpportunityDtos.UpdateStageRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        OpportunityDtos.OpportunityDto opp = opportunityService.updateStage(id, request.getStage(), currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Stage updated successfully", opp));
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<ApiResponse<OpportunityDtos.TaskDto>> addTask(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        String title = (String) payload.get("title");
        String description = (String) payload.get("description");
        LocalDate dueDate = payload.get("dueDate") != null ? LocalDate.parse((String) payload.get("dueDate")) : null;

        OpportunityDtos.TaskDto task = opportunityService.addTask(id, title, description, dueDate, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Task added successfully", task));
    }

    @PatchMapping("/tasks/{taskId}/toggle")
    public ResponseEntity<ApiResponse<OpportunityDtos.TaskDto>> toggleTask(
            @PathVariable("taskId") Long taskId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        OpportunityDtos.TaskDto task = opportunityService.toggleTask(taskId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Task toggled successfully", task));
    }
}
