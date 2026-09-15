package com.procurepilot.startup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "startup_certifications")
public class StartupCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @Column(nullable = false)
    private String certType; // ISO 9001:2015, ISO 27001, CMMI Level 3, BIS, DPIIT, UDYAM

    private String certNumber;
    private String issuingBody;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String documentUrl;

    public StartupCertification() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Startup getStartup() { return startup; }
    public void setStartup(Startup startup) { this.startup = startup; }

    public String getCertType() { return certType; }
    public void setCertType(String certType) { this.certType = certType; }

    public String getCertNumber() { return certNumber; }
    public void setCertNumber(String certNumber) { this.certNumber = certNumber; }

    public String getIssuingBody() { return issuingBody; }
    public void setIssuingBody(String issuingBody) { this.issuingBody = issuingBody; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
}
