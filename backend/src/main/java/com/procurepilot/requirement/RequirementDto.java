package com.procurepilot.requirement;

public class RequirementDto {
    private Long id;
    private Long tenderId;
    private RequirementType type;
    private String requirementText;
    private String normalizedValue;
    private String unit;
    private RequirementOperator operator;
    private Boolean mandatory;
    private Boolean preferred;
    private Integer sourcePage;
    private String sourceSection;
    private String sourceSnippet;
    private String confidence;
    private String reviewStatus;

    public RequirementDto() {}

    public RequirementDto(TenderRequirement req) {
        this.id = req.getId();
        this.tenderId = req.getTender() != null ? req.getTender().getId() : null;
        this.type = req.getType();
        this.requirementText = req.getRequirementText();
        this.normalizedValue = req.getNormalizedValue();
        this.unit = req.getUnit();
        this.operator = req.getOperator();
        this.mandatory = req.getMandatory();
        this.preferred = req.getPreferred();
        this.sourcePage = req.getSourcePage();
        this.sourceSection = req.getSourceSection();
        this.sourceSnippet = req.getSourceSnippet();
        this.confidence = req.getConfidence();
        this.reviewStatus = req.getReviewStatus();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenderId() { return tenderId; }
    public void setTenderId(Long tenderId) { this.tenderId = tenderId; }

    public RequirementType getType() { return type; }
    public void setType(RequirementType type) { this.type = type; }

    public String getRequirementText() { return requirementText; }
    public void setRequirementText(String requirementText) { this.requirementText = requirementText; }

    public String getNormalizedValue() { return normalizedValue; }
    public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public RequirementOperator getOperator() { return operator; }
    public void setOperator(RequirementOperator operator) { this.operator = operator; }

    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }

    public Boolean getPreferred() { return preferred; }
    public void setPreferred(Boolean preferred) { this.preferred = preferred; }

    public Integer getSourcePage() { return sourcePage; }
    public void setSourcePage(Integer sourcePage) { this.sourcePage = sourcePage; }

    public String getSourceSection() { return sourceSection; }
    public void setSourceSection(String sourceSection) { this.sourceSection = sourceSection; }

    public String getSourceSnippet() { return sourceSnippet; }
    public void setSourceSnippet(String sourceSnippet) { this.sourceSnippet = sourceSnippet; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
}
