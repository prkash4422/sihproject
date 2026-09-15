package com.procurepilot.startup;

import com.procurepilot.auth.User;
import com.procurepilot.auth.UserRepository;
import com.procurepilot.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StartupService {

    private final StartupRepository startupRepository;
    private final StartupCapabilityRepository capabilityRepository;
    private final StartupCertificationRepository certificationRepository;
    private final StartupDocumentRepository documentRepository;
    private final UserRepository userRepository;

    public StartupService(StartupRepository startupRepository,
                          StartupCapabilityRepository capabilityRepository,
                          StartupCertificationRepository certificationRepository,
                          StartupDocumentRepository documentRepository,
                          UserRepository userRepository) {
        this.startupRepository = startupRepository;
        this.capabilityRepository = capabilityRepository;
        this.certificationRepository = certificationRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public StartupDtos.ProfileDto getProfileByUserId(Long userId) {
        Startup startup = startupRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found for user id: " + userId));

        return toDto(startup);
    }

    @Transactional(readOnly = true)
    public Startup getStartupEntityByUserId(Long userId) {
        return startupRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found for user id: " + userId));
    }

    @Transactional
    public StartupDtos.ProfileDto updateProfile(Long userId, StartupDtos.ProfileDto dto) {
        Startup startup = startupRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    Startup newStartup = new Startup();
                    newStartup.setUser(user);
                    return newStartup;
                });

        startup.setCompanyName(dto.getCompanyName());
        startup.setLegalName(dto.getLegalName());
        startup.setDpiitRecognized(dto.getDpiitRecognized());
        startup.setDpiitNumber(dto.getDpiitNumber());
        startup.setUdyamNumber(dto.getUdyamNumber());
        startup.setIncorporationDate(dto.getIncorporationDate());
        startup.setAnnualTurnoverInr(dto.getAnnualTurnoverInr());
        startup.setNetWorthInr(dto.getNetWorthInr());
        startup.setPrimarySector(dto.getPrimarySector());
        startup.setState(dto.getState());
        startup.setCity(dto.getCity());
        startup.setWebsite(dto.getWebsite());
        startup.setUpdatedAt(Instant.now());

        // Update capabilities
        if (dto.getCapabilities() != null) {
            startup.getCapabilities().clear();
            for (StartupDtos.CapabilityDto capDto : dto.getCapabilities()) {
                StartupCapability cap = new StartupCapability();
                cap.setStartup(startup);
                cap.setCategory(capDto.getCategory());
                cap.setName(capDto.getName());
                cap.setProficiencyLevel(capDto.getProficiencyLevel());
                cap.setDescription(capDto.getDescription());
                startup.getCapabilities().add(cap);
            }
        }

        // Update certifications
        if (dto.getCertifications() != null) {
            startup.getCertifications().clear();
            for (StartupDtos.CertificationDto certDto : dto.getCertifications()) {
                StartupCertification cert = new StartupCertification();
                cert.setStartup(startup);
                cert.setCertType(certDto.getCertType());
                cert.setCertNumber(certDto.getCertNumber());
                cert.setIssuingBody(certDto.getIssuingBody());
                cert.setIssueDate(certDto.getIssueDate());
                cert.setExpiryDate(certDto.getExpiryDate());
                cert.setDocumentUrl(certDto.getDocumentUrl());
                startup.getCertifications().add(cert);
            }
        }

        // Generate capability fingerprint string for fast indexing
        String fingerprint = generateFingerprint(startup);
        startup.setCapabilityFingerprint(fingerprint);

        Startup saved = startupRepository.save(startup);
        return toDto(saved);
    }

    private String generateFingerprint(Startup startup) {
        StringBuilder sb = new StringBuilder();
        if (startup.getPrimarySector() != null) {
            sb.append(startup.getPrimarySector().toLowerCase().replaceAll("[^a-z0-9]", "-")).append(";");
        }
        for (StartupCapability cap : startup.getCapabilities()) {
            sb.append(cap.getName().toLowerCase().replaceAll("[^a-z0-9]", "-")).append(";");
        }
        for (StartupCertification cert : startup.getCertifications()) {
            sb.append(cert.getCertType().toLowerCase().replaceAll("[^a-z0-9]", "-")).append(";");
        }
        return sb.toString();
    }

    @Transactional
    public StartupDtos.DocumentDto addDocument(Long userId, String docType, String fileName, String filePath, Long fileSize, String mimeType) {
        Startup startup = getStartupEntityByUserId(userId);

        StartupDocument doc = new StartupDocument();
        doc.setStartup(startup);
        doc.setDocType(docType);
        doc.setFileName(fileName);
        doc.setFilePath(filePath);
        doc.setFileSize(fileSize);
        doc.setMimeType(mimeType);
        doc.setUploadedAt(Instant.now());

        StartupDocument saved = documentRepository.save(doc);
        return new StartupDtos.DocumentDto(saved.getId(), saved.getDocType(), saved.getFileName(), saved.getFilePath(), saved.getFileSize(), saved.getMimeType(), saved.getUploadedAt());
    }

    @Transactional(readOnly = true)
    public List<StartupDtos.DocumentDto> listDocuments(Long userId) {
        Startup startup = getStartupEntityByUserId(userId);
        return documentRepository.findByStartupId(startup.getId()).stream()
                .map(d -> new StartupDtos.DocumentDto(d.getId(), d.getDocType(), d.getFileName(), d.getFilePath(), d.getFileSize(), d.getMimeType(), d.getUploadedAt()))
                .collect(Collectors.toList());
    }

    private StartupDtos.ProfileDto toDto(Startup startup) {
        StartupDtos.ProfileDto dto = new StartupDtos.ProfileDto();
        dto.setId(startup.getId());
        dto.setUserId(startup.getUser().getId());
        dto.setCompanyName(startup.getCompanyName());
        dto.setLegalName(startup.getLegalName());
        dto.setDpiitRecognized(startup.getDpiitRecognized());
        dto.setDpiitNumber(startup.getDpiitNumber());
        dto.setUdyamNumber(startup.getUdyamNumber());
        dto.setIncorporationDate(startup.getIncorporationDate());
        dto.setAnnualTurnoverInr(startup.getAnnualTurnoverInr());
        dto.setNetWorthInr(startup.getNetWorthInr());
        dto.setPrimarySector(startup.getPrimarySector());
        dto.setState(startup.getState());
        dto.setCity(startup.getCity());
        dto.setWebsite(startup.getWebsite());
        dto.setCapabilityFingerprint(startup.getCapabilityFingerprint());

        if (startup.getCapabilities() != null) {
            dto.setCapabilities(startup.getCapabilities().stream()
                    .map(c -> new StartupDtos.CapabilityDto(c.getId(), c.getCategory(), c.getName(), c.getProficiencyLevel(), c.getDescription()))
                    .collect(Collectors.toList()));
        }

        if (startup.getCertifications() != null) {
            dto.setCertifications(startup.getCertifications().stream()
                    .map(c -> new StartupDtos.CertificationDto(c.getId(), c.getCertType(), c.getCertNumber(), c.getIssuingBody(), c.getIssueDate(), c.getExpiryDate(), c.getDocumentUrl()))
                    .collect(Collectors.toList()));
        }

        if (startup.getDocuments() != null) {
            dto.setDocuments(startup.getDocuments().stream()
                    .map(d -> new StartupDtos.DocumentDto(d.getId(), d.getDocType(), d.getFileName(), d.getFilePath(), d.getFileSize(), d.getMimeType(), d.getUploadedAt()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
