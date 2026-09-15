package com.procurepilot.startup;

import com.procurepilot.auth.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "startups")
public class Startup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    private String legalName;
    private Boolean dpiitRecognized = false;
    private String dpiitNumber;
    private String udyamNumber;
    private LocalDate incorporationDate;
    private BigDecimal annualTurnoverInr = BigDecimal.ZERO;
    private BigDecimal netWorthInr = BigDecimal.ZERO;
    private String primarySector;
    private String state;
    private String city;
    private String website;

    @Column(columnDefinition = "TEXT")
    private String capabilityFingerprint;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "startup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StartupCapability> capabilities = new ArrayList<>();

    @OneToMany(mappedBy = "startup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StartupCertification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "startup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StartupDocument> documents = new ArrayList<>();

    public Startup() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<StartupCapability> getCapabilities() { return capabilities; }
    public void setCapabilities(List<StartupCapability> capabilities) { this.capabilities = capabilities; }

    public List<StartupCertification> getCertifications() { return certifications; }
    public void setCertifications(List<StartupCertification> certifications) { this.certifications = certifications; }

    public List<StartupDocument> getDocuments() { return documents; }
    public void setDocuments(List<StartupDocument> documents) { this.documents = documents; }
}
