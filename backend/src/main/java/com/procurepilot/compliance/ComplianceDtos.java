package com.procurepilot.compliance;

import java.time.LocalDate;
import java.util.List;

public class ComplianceDtos {

    public static class ComplianceItemDto {
        private Long id;
        private Long tenderId;
        private Long startupId;
        private String title;
        private String category;
        private Boolean mandatory;
        private ComplianceStatus status;
        private Integer tenderSourcePage;
        private LocalDate dueDate;
        private String notes;
        private List<EvidenceDto> evidence;

        public ComplianceItemDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getTenderId() { return tenderId; }
        public void setTenderId(Long tenderId) { this.tenderId = tenderId; }

        public Long getStartupId() { return startupId; }
        public void setStartupId(Long startupId) { this.startupId = startupId; }

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

        public List<EvidenceDto> getEvidence() { return evidence; }
        public void setEvidence(List<EvidenceDto> evidence) { this.evidence = evidence; }
    }

    public static class EvidenceDto {
        private Long id;
        private Long documentId;
        private String fileName;
        private String docType;
        private String filePath;
        private String verificationStatus;
        private String verifiedBy;

        public EvidenceDto() {}

        public EvidenceDto(Long id, Long documentId, String fileName, String docType, String filePath, String verificationStatus, String verifiedBy) {
            this.id = id;
            this.documentId = documentId;
            this.fileName = fileName;
            this.docType = docType;
            this.filePath = filePath;
            this.verificationStatus = verificationStatus;
            this.verifiedBy = verifiedBy;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getDocumentId() { return documentId; }
        public void setDocumentId(Long documentId) { this.documentId = documentId; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getDocType() { return docType; }
        public void setDocType(String docType) { this.docType = docType; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public String getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
        public String getVerifiedBy() { return verifiedBy; }
        public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    }

    public static class StatusUpdateRequest {
        private ComplianceStatus status;
        private String notes;

        public StatusUpdateRequest() {}

        public ComplianceStatus getStatus() { return status; }
        public void setStatus(ComplianceStatus status) { this.status = status; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
