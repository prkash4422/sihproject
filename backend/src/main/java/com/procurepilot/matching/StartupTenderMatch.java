package com.procurepilot.matching;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurepilot.eligibility.EligibilityEvaluation;
import com.procurepilot.startup.Startup;
import com.procurepilot.tender.Tender;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "startup_tender_matches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"startup_id", "tender_id"})
})
public class StartupTenderMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tender_id", nullable = false)
    private Tender tender;

    @Transient
    @JsonProperty("startupId")
    public Long getStartupId() {
        return startup != null ? startup.getId() : null;
    }

    @Transient
    @JsonProperty("tenderId")
    public Long getTenderId() {
        return tender != null ? tender.getId() : null;
    }

    @Column(nullable = false)
    private Integer overallScore;

    @Column(nullable = false)
    private Integer technicalScore;

    @Column(nullable = false)
    private Integer sectorScore;

    @Column(nullable = false)
    private Integer eligibilityScore;

    @Column(nullable = false)
    private Integer readinessScore;

    @Column(nullable = false)
    private Integer fitScore;

    @Column(columnDefinition = "TEXT")
    private String explanationJson;

    private Instant evaluatedAt = Instant.now();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EligibilityEvaluation> evaluations = new ArrayList<>();

    public StartupTenderMatch() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Startup getStartup() { return startup; }
    public void setStartup(Startup startup) { this.startup = startup; }

    public Tender getTender() { return tender; }
    public void setTender(Tender tender) { this.tender = tender; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public Integer getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(Integer technicalScore) { this.technicalScore = technicalScore; }

    public Integer getSectorScore() { return sectorScore; }
    public void setSectorScore(Integer sectorScore) { this.sectorScore = sectorScore; }

    public Integer getEligibilityScore() { return eligibilityScore; }
    public void setEligibilityScore(Integer eligibilityScore) { this.eligibilityScore = eligibilityScore; }

    public Integer getReadinessScore() { return readinessScore; }
    public void setReadinessScore(Integer readinessScore) { this.readinessScore = readinessScore; }

    public Integer getFitScore() { return fitScore; }
    public void setFitScore(Integer fitScore) { this.fitScore = fitScore; }

    public String getExplanationJson() { return explanationJson; }
    public void setExplanationJson(String explanationJson) { this.explanationJson = explanationJson; }

    public Instant getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(Instant evaluatedAt) { this.evaluatedAt = evaluatedAt; }

    public List<EligibilityEvaluation> getEvaluations() { return evaluations; }
    public void setEvaluations(List<EligibilityEvaluation> evaluations) { this.evaluations = evaluations; }
}
