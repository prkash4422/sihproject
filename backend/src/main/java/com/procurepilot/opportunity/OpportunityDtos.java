package com.procurepilot.opportunity;

import com.procurepilot.tender.TenderDtos;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class OpportunityDtos {

    public static class OpportunityDto {
        private Long id;
        private Long startupId;
        private Long tenderId;
        private TenderDtos.TenderSummaryDto tender;
        private OpportunityStage stage;
        private String priority;
        private Integer readinessPercent;
        private LocalDate targetSubmissionDate;
        private String notes;
        private Instant updatedAt;
        private List<TaskDto> tasks;

        public OpportunityDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getStartupId() { return startupId; }
        public void setStartupId(Long startupId) { this.startupId = startupId; }

        public Long getTenderId() { return tenderId; }
        public void setTenderId(Long tenderId) { this.tenderId = tenderId; }

        public TenderDtos.TenderSummaryDto getTender() { return tender; }
        public void setTender(TenderDtos.TenderSummaryDto tender) { this.tender = tender; }

        public OpportunityStage getStage() { return stage; }
        public void setStage(OpportunityStage stage) { this.stage = stage; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public Integer getReadinessPercent() { return readinessPercent; }
        public void setReadinessPercent(Integer readinessPercent) { this.readinessPercent = readinessPercent; }

        public LocalDate getTargetSubmissionDate() { return targetSubmissionDate; }
        public void setTargetSubmissionDate(LocalDate targetSubmissionDate) { this.targetSubmissionDate = targetSubmissionDate; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

        public List<TaskDto> getTasks() { return tasks; }
        public void setTasks(List<TaskDto> tasks) { this.tasks = tasks; }
    }

    public static class TaskDto {
        private Long id;
        private Long opportunityId;
        private String title;
        private String description;
        private LocalDate dueDate;
        private Boolean completed;
        private Instant completedAt;

        public TaskDto() {}

        public TaskDto(OpportunityTask task) {
            this.id = task.getId();
            this.opportunityId = task.getOpportunity().getId();
            this.title = task.getTitle();
            this.description = task.getDescription();
            this.dueDate = task.getDueDate();
            this.completed = task.getCompleted();
            this.completedAt = task.getCompletedAt();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getOpportunityId() { return opportunityId; }
        public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }

        public Instant getCompletedAt() { return completedAt; }
        public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    }

    public static class CreateOpportunityRequest {
        private Long tenderId;
        private OpportunityStage stage = OpportunityStage.INTERESTED;
        private String priority = "HIGH";
        private String notes;

        public CreateOpportunityRequest() {}

        public Long getTenderId() { return tenderId; }
        public void setTenderId(Long tenderId) { this.tenderId = tenderId; }

        public OpportunityStage getStage() { return stage; }
        public void setStage(OpportunityStage stage) { this.stage = stage; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class UpdateStageRequest {
        private OpportunityStage stage;

        public UpdateStageRequest() {}

        public OpportunityStage getStage() { return stage; }
        public void setStage(OpportunityStage stage) { this.stage = stage; }
    }
}
