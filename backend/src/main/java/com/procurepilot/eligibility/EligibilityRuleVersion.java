package com.procurepilot.eligibility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "eligibility_rule_versions")
public class EligibilityRuleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private EligibilityRule rule;

    @Column(nullable = false)
    private String versionTag;

    @Column(nullable = false)
    private String relaxationType; // EXEMPT_TURNOVER, EXEMPT_EXPERIENCE, EXEMPT_EMD, LOCAL_PREFERENCE

    @Column(columnDefinition = "TEXT")
    private String criteriaConditionsJson;

    private String sourceClause;

    public EligibilityRuleVersion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EligibilityRule getRule() { return rule; }
    public void setRule(EligibilityRule rule) { this.rule = rule; }

    public String getVersionTag() { return versionTag; }
    public void setVersionTag(String versionTag) { this.versionTag = versionTag; }

    public String getRelaxationType() { return relaxationType; }
    public void setRelaxationType(String relaxationType) { this.relaxationType = relaxationType; }

    public String getCriteriaConditionsJson() { return criteriaConditionsJson; }
    public void setCriteriaConditionsJson(String criteriaConditionsJson) { this.criteriaConditionsJson = criteriaConditionsJson; }

    public String getSourceClause() { return sourceClause; }
    public void setSourceClause(String sourceClause) { this.sourceClause = sourceClause; }
}
