package com.procurepilot.compliance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.procurepilot.startup.Startup;
import com.procurepilot.tender.Tender;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compliance_items")
public class ComplianceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tender_id", nullable = false)
    private Tender tender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @Column(nullable = false)
    private String title;

    private String category;
    private Boolean mandatory = true;

    @Enumerated(EnumType.STRING)
    private ComplianceStatus status = ComplianceStatus.NOT_STARTED;

    private Integer tenderSourcePage;
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "complianceItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplianceEvidence> evidenceList = new ArrayList<>();

    public ComplianceItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tender getTender() { return tender; }
    public void setTender(Tender tender) { this.tender = tender; }

    public Startup getStartup() { return startup; }
    public void setStartup(Startup startup) { this.startup = startup; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }

    public ComplianceStatus getStatus() { return status; }
    public void setStatus(ComplianceStatus status) { this.status = status; }

    public Integer getTenderSourcePage() { return tenderSourcePage; }
    public void setTenderSourcePage(Integer tenderSourcePage) { this.tenderSourcePage = tenderSourcePage; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<ComplianceEvidence> getEvidenceList() { return evidenceList; }
    public void setEvidenceList(List<ComplianceEvidence> evidenceList) { this.evidenceList = evidenceList; }
}
