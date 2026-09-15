package com.procurepilot.startup;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class StartupDtos {

    public static class ProfileDto {
        private Long id;
        private Long userId;
        private String companyName;
        private String legalName;
        private Boolean dpiitRecognized;
        private String dpiitNumber;
        private String udyamNumber;
        private LocalDate incorporationDate;
        private BigDecimal annualTurnoverInr;
        private BigDecimal netWorthInr;
        private String primarySector;
        private String state;
        private String city;
        private String website;
        private String capabilityFingerprint;
        private List<CapabilityDto> capabilities;
        private List<CertificationDto> certifications;
        private List<DocumentDto> documents;

        public ProfileDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getLegalName() { return legalName; }
        public void setLegalName(String legalName) { this.legalName = legalName; }

        public Boolean getDpiitRecognized() { return dpiitRecognized; }
        public void setDpiitRecognized(Boolean dpiitRecognized) { this.dpiitRecognized = dpiitRecognized; }

        public String getDpiitNumber() { return dpiitNumber; }
        public void setDpiitNumber(String dpiitNumber) { this.dpiitNumber = dpiitNumber; }

        public String getUdyamNumber() { return udyamNumber; }
        public void setUdyamNumber(String udyamNumber) { this.udyamNumber = udyamNumber; }

        public LocalDate getIncorporationDate() { return incorporationDate; }
        public void setIncorporationDate(LocalDate incorporationDate) { this.incorporationDate = incorporationDate; }

        public BigDecimal getAnnualTurnoverInr() { return annualTurnoverInr; }
        public void setAnnualTurnoverInr(BigDecimal annualTurnoverInr) { this.annualTurnoverInr = annualTurnoverInr; }

        public BigDecimal getNetWorthInr() { return netWorthInr; }
        public void setNetWorthInr(BigDecimal netWorthInr) { this.netWorthInr = netWorthInr; }

        public String getPrimarySector() { return primarySector; }
        public void setPrimarySector(String primarySector) { this.primarySector = primarySector; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }

        public String getCapabilityFingerprint() { return capabilityFingerprint; }
        public void setCapabilityFingerprint(String capabilityFingerprint) { this.capabilityFingerprint = capabilityFingerprint; }

        public List<CapabilityDto> getCapabilities() { return capabilities; }
        public void setCapabilities(List<CapabilityDto> capabilities) { this.capabilities = capabilities; }

        public List<CertificationDto> getCertifications() { return certifications; }
        public void setCertifications(List<CertificationDto> certifications) { this.certifications = certifications; }

        public List<DocumentDto> getDocuments() { return documents; }
        public void setDocuments(List<DocumentDto> documents) { this.documents = documents; }
    }

    public static class CapabilityDto {
        private Long id;
        private String category;
        private String name;
        private String proficiencyLevel;
        private String description;

        public CapabilityDto() {}

        public CapabilityDto(Long id, String category, String name, String proficiencyLevel, String description) {
            this.id = id;
            this.category = category;
            this.name = name;
            this.proficiencyLevel = proficiencyLevel;
            this.description = description;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProficiencyLevel() { return proficiencyLevel; }
        public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class CertificationDto {
        private Long id;
        private String certType;
        private String certNumber;
        private String issuingBody;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String documentUrl;

        public CertificationDto() {}

        public CertificationDto(Long id, String certType, String certNumber, String issuingBody, LocalDate issueDate, LocalDate expiryDate, String documentUrl) {
            this.id = id;
            this.certType = certType;
            this.certNumber = certNumber;
            this.issuingBody = issuingBody;
            this.issueDate = issueDate;
            this.expiryDate = expiryDate;
            this.documentUrl = documentUrl;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
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

    public static class DocumentDto {
        private Long id;
        private String docType;
        private String fileName;
        private String filePath;
        private Long fileSize;
        private String mimeType;
        private Instant uploadedAt;

        public DocumentDto() {}

        public DocumentDto(Long id, String docType, String fileName, String filePath, Long fileSize, String mimeType, Instant uploadedAt) {
            this.id = id;
            this.docType = docType;
            this.fileName = fileName;
            this.filePath = filePath;
            this.fileSize = fileSize;
            this.mimeType = mimeType;
            this.uploadedAt = uploadedAt;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDocType() { return docType; }
        public void setDocType(String docType) { this.docType = docType; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
        public Instant getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    }
}
