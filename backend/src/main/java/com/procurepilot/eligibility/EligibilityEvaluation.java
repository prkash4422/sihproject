package com.procurepilot.eligibility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.procurepilot.matching.StartupTenderMatch;
import com.procurepilot.requirement.TenderRequirement;
import jakarta.persistence.*;

@Entity
@Table(name = "eligibility_evaluations")
public class EligibilityEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private StartupTenderMatch match;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requirement_id")
    private TenderRequirement requirement;

    private String startupValue;
    private String tenderValue;
    private String operator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityResult result;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applied_relaxation_rule_id")
    private EligibilityRule appliedRelaxationRule;

    private Integer sourcePage;
    private String sourceSection;

    public EligibilityEvaluation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StartupTenderMatch getMatch() { return match; }
    public void setMatch(StartupTenderMatch match) { this.match = match; }

    public TenderRequirement getRequirement() { return requirement; }
    public void setRequirement(TenderRequirement requirement) { this.requirement = requirement; }

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

    public EligibilityRule getAppliedRelaxationRule() { return appliedRelaxationRule; }
    public void setAppliedRelaxationRule(EligibilityRule appliedRelaxationRule) { this.appliedRelaxationRule = appliedRelaxationRule; }

    public Integer getSourcePage() { return sourcePage; }
    public void setSourcePage(Integer sourcePage) { this.sourcePage = sourcePage; }

    public String getSourceSection() { return sourceSection; }
    public void setSourceSection(String sourceSection) { this.sourceSection = sourceSection; }
}
