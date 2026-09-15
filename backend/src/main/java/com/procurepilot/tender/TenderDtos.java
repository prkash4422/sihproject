package com.procurepilot.tender;

import com.procurepilot.requirement.RequirementDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class TenderDtos {

    public static class TenderSummaryDto {
        private Long id;
        private String tenderRefNo;
        private String title;
        private String department;
        private String authority;
        private String category;
        private BigDecimal estimatedValueInr;
        private BigDecimal emdInr;
        private Instant publishedDate;
        private Instant closingDate;
        private String sourcePortal;
        private String sourceUrl;
        private String status;
        private Integer matchScore;
        private String eligibilityStatus; // PASS, FAIL, NEEDS_REVIEW, UNCHECKED
        private Integer daysRemaining;

        public TenderSummaryDto() {}

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

        public Integer getMatchScore() { return matchScore; }
        public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

        public String getEligibilityStatus() { return eligibilityStatus; }
        public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

        public Integer getDaysRemaining() { return daysRemaining; }
        public void setDaysRemaining(Integer daysRemaining) { this.daysRemaining = daysRemaining; }
    }

    public static class TenderDetailDto {
        private Long id;
        private String tenderRefNo;
        private String title;
        private String department;
        private String authority;
        private String category;
        private BigDecimal estimatedValueInr;
        private BigDecimal emdInr;
        private Instant publishedDate;
        private Instant closingDate;
        private String sourcePortal;
        private String sourceUrl;
        private String status;
        private String documentUrl;
        private List<SectionDto> sections;
        private List<RequirementDto> requirements;
        private Integer matchScore;
        private String eligibilityStatus;

        public TenderDetailDto() {}

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

        public List<SectionDto> getSections() { return sections; }
        public void setSections(List<SectionDto> sections) { this.sections = sections; }

        public List<RequirementDto> getRequirements() { return requirements; }
        public void setRequirements(List<RequirementDto> requirements) { this.requirements = requirements; }

        public Integer getMatchScore() { return matchScore; }
        public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

        public String getEligibilityStatus() { return eligibilityStatus; }
        public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }
    }

    public static class SectionDto {
        private Long id;
        private String sectionCode;
        private String title;
        private Integer pageStart;
        private Integer pageEnd;
        private String rawText;

        public SectionDto() {}

        public SectionDto(TenderSection section) {
            this.id = section.getId();
            this.sectionCode = section.getSectionCode();
            this.title = section.getTitle();
            this.pageStart = section.getPageStart();
            this.pageEnd = section.getPageEnd();
            this.rawText = section.getRawText();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSectionCode() { return sectionCode; }
        public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getPageStart() { return pageStart; }
        public void setPageStart(Integer pageStart) { this.pageStart = pageStart; }
        public Integer getPageEnd() { return pageEnd; }
        public void setPageEnd(Integer pageEnd) { this.pageEnd = pageEnd; }
        public String getRawText() { return rawText; }
        public void setRawText(String rawText) { this.rawText = rawText; }
    }
}
