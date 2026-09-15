package com.procurepilot.requirement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.procurepilot.tender.Tender;
import jakarta.persistence.*;

@Entity
@Table(name = "tender_requirements")
public class TenderRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tender_id", nullable = false)
    private Tender tender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String requirementText;

    private String normalizedValue;
    private String unit;

    @Enumerated(EnumType.STRING)
    private RequirementOperator operator = RequirementOperator.GTE;

    private Boolean mandatory = true;
    private Boolean preferred = false;
    private Integer sourcePage;
    private String sourceSection;

    @Column(columnDefinition = "TEXT")
    private String sourceSnippet;

    private String confidence = "HIGH"; // HIGH, MEDIUM, LOW
    private String reviewStatus = "APPROVED"; // APPROVED, NEEDS_REVIEW, REJECTED

    public TenderRequirement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tender getTender() { return tender; }
    public void setTender(Tender tender) { this.tender = tender; }

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
