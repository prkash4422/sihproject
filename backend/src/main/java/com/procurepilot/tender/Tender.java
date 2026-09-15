package com.procurepilot.tender;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.procurepilot.requirement.TenderRequirement;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenders")
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tenderRefNo;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false)
    private String department;

    private String authority;
    private String category;
    private BigDecimal estimatedValueInr;
    private BigDecimal emdInr;
    private Instant publishedDate;

    @Column(nullable = false)
    private Instant closingDate;

    private String sourcePortal = "GeM";
    private String sourceUrl;
    private String status = "PUBLISHED";
    private String documentUrl;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "tender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TenderSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "tender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TenderRequirement> requirements = new ArrayList<>();

    public Tender() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenderRefNo() { return tenderRefNo; }
    public void setTenderRefNo(String tenderRefNo) { this.tenderRefNo = tenderRefNo; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getAuthority() { return authority; }
    public void setAuthority(String authority) { this.authority = authority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getEstimatedValueInr() { return estimatedValueInr; }
    public void setEstimatedValueInr(BigDecimal estimatedValueInr) { this.estimatedValueInr = estimatedValueInr; }

    public BigDecimal getEmdInr() { return emdInr; }
    public void setEmdInr(BigDecimal emdInr) { this.emdInr = emdInr; }

    public Instant getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Instant publishedDate) { this.publishedDate = publishedDate; }

    public Instant getClosingDate() { return closingDate; }
    public void setClosingDate(Instant closingDate) { this.closingDate = closingDate; }

    public String getSourcePortal() { return sourcePortal; }
    public void setSourcePortal(String sourcePortal) { this.sourcePortal = sourcePortal; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<TenderSection> getSections() { return sections; }
    public void setSections(List<TenderSection> sections) { this.sections = sections; }

    public List<TenderRequirement> getRequirements() { return requirements; }
    public void setRequirements(List<TenderRequirement> requirements) { this.requirements = requirements; }
}
