package com.procurepilot.compliance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.procurepilot.startup.StartupDocument;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "compliance_evidence")
public class ComplianceEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliance_item_id", nullable = false)
    private ComplianceItem complianceItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "startup_document_id")
    private StartupDocument document;

    private String verificationStatus = "PENDING"; // PENDING, VERIFIED, REJECTED
    private String verifiedBy;
    private Instant verifiedAt;

    public ComplianceEvidence() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ComplianceItem getComplianceItem() { return complianceItem; }
    public void setComplianceItem(ComplianceItem complianceItem) { this.complianceItem = complianceItem; }

    public StartupDocument getDocument() { return document; }
    public void setDocument(StartupDocument document) { this.document = document; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public Instant getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; }
}
