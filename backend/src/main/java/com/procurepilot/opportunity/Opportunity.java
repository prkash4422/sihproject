package com.procurepilot.opportunity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.procurepilot.startup.Startup;
import com.procurepilot.tender.Tender;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "opportunities", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"startup_id", "tender_id"})
})
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tender_id", nullable = false)
    private Tender tender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpportunityStage stage = OpportunityStage.INTERESTED;

    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT
    private Integer readinessPercent = 0;
    private LocalDate targetSubmissionDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityTask> tasks = new ArrayList<>();

    public Opportunity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Startup getStartup() { return startup; }
    public void setStartup(Startup startup) { this.startup = startup; }

    public Tender getTender() { return tender; }
    public void setTender(Tender tender) { this.tender = tender; }

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

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<OpportunityTask> getTasks() { return tasks; }
    public void setTasks(List<OpportunityTask> tasks) { this.tasks = tasks; }
}
