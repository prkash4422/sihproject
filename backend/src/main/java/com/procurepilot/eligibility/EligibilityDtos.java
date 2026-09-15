package com.procurepilot.eligibility;

import java.time.LocalDate;
import java.util.List;

public class EligibilityDtos {

    public static class EvaluationSummaryDto {
        private Long tenderId;
        private Long startupId;
        private Integer overallMatchScore;
        private String overallStatus; // PASS, FAIL, NEEDS_REVIEW
        private List<EvaluationItemDto> evaluations;
        private List<RuleCitationDto> appliedRelaxations;

        public EvaluationSummaryDto() {}

        public Long getTenderId() { return tenderId; }
        public void setTenderId(Long tenderId) { this.tenderId = tenderId; }

        public Long getStartupId() { return startupId; }
        public void setStartupId(Long startupId) { this.startupId = startupId; }

        public Integer getOverallMatchScore() { return overallMatchScore; }
        public void setOverallMatchScore(Integer overallMatchScore) { this.overallMatchScore = overallMatchScore; }

        public String getOverallStatus() { return overallStatus; }
        public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

        public List<EvaluationItemDto> getEvaluations() { return evaluations; }
        public void setEvaluations(List<EvaluationItemDto> evaluations) { this.evaluations = evaluations; }

        public List<RuleCitationDto> getAppliedRelaxations() { return appliedRelaxations; }
        public void setAppliedRelaxations(List<RuleCitationDto> appliedRelaxations) { this.appliedRelaxations = appliedRelaxations; }
    }

    public static class EvaluationItemDto {
        private Long id;
        private String requirementType;
        private String requirementText;
        private String startupValue;
        private String tenderValue;
        private String operator;
        private EligibilityResult result;
        private String reason;
        private Integer sourcePage;
        private String sourceSection;
        private String appliedRuleCode;

        public EvaluationItemDto() {}

        public EvaluationItemDto(EligibilityEvaluation eval) {
            this.id = eval.getId();
            this.requirementType = eval.getRequirement() != null ? eval.getRequirement().getType().name() : "GENERAL";
            this.requirementText = eval.getRequirement() != null ? eval.getRequirement().getRequirementText() : "";
            this.startupValue = eval.getStartupValue();
            this.tenderValue = eval.getTenderValue();
            this.operator = eval.getOperator();
            this.result = eval.getResult();
            this.reason = eval.getReason();
            this.sourcePage = eval.getSourcePage();
            this.sourceSection = eval.getSourceSection();
            this.appliedRuleCode = eval.getAppliedRelaxationRule() != null ? eval.getAppliedRelaxationRule().getRuleCode() : null;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getRequirementType() { return requirementType; }
        public void setRequirementType(String requirementType) { this.requirementType = requirementType; }

        public String getRequirementText() { return requirementText; }
        public void setRequirementText(String requirementText) { this.requirementText = requirementText; }

        public String getStartupValue() { return startupValue; }
        public void setStartupValue(String startupValue) { this.startupValue = startupValue; }

        public String getTenderValue() { return tenderValue; }
        public void setTenderValue(String tenderValue) { this.tenderValue = tenderValue; }

        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }

        public EligibilityResult getResult() { return result; }
        public void setResult(EligibilityResult result) { this.result = result; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public Integer getSourcePage() { return sourcePage; }
        public void setSourcePage(Integer sourcePage) { this.sourcePage = sourcePage; }

        public String getSourceSection() { return sourceSection; }
        public void setSourceSection(String sourceSection) { this.sourceSection = sourceSection; }

        public String getAppliedRuleCode() { return appliedRuleCode; }
        public void setAppliedRuleCode(String appliedRuleCode) { this.appliedRuleCode = appliedRuleCode; }
    }

    public static class RuleCitationDto {
        private String ruleCode;
        private String name;
        private String authority;
        private String description;
        private String sourceUrl;
        private LocalDate effectiveDate;
        private String version;

        public RuleCitationDto() {}

        public RuleCitationDto(EligibilityRule rule) {
            this.ruleCode = rule.getRuleCode();
            this.name = rule.getName();
            this.authority = rule.getAuthority();
            this.description = rule.getDescription();
            this.sourceUrl = rule.getSourceUrl();
            this.effectiveDate = rule.getEffectiveDate();
            this.version = rule.getVersion();
        }

        public String getRuleCode() { return ruleCode; }
        public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAuthority() { return authority; }
        public void setAuthority(String authority) { this.authority = authority; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getSourceUrl() { return sourceUrl; }
        public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

        public LocalDate getEffectiveDate() { return effectiveDate; }
        public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }
}
