package com.procurepilot.common;

import com.procurepilot.ai.DocumentChunk;
import com.procurepilot.ai.DocumentChunkRepository;
import com.procurepilot.auth.Role;
import com.procurepilot.auth.RoleRepository;
import com.procurepilot.auth.User;
import com.procurepilot.auth.UserRepository;
import com.procurepilot.compliance.ComplianceItem;
import com.procurepilot.compliance.ComplianceItemRepository;
import com.procurepilot.compliance.ComplianceStatus;
import com.procurepilot.eligibility.EligibilityRule;
import com.procurepilot.eligibility.EligibilityRuleRepository;
import com.procurepilot.matching.MatchingEngine;
import com.procurepilot.matching.StartupTenderMatch;
import com.procurepilot.matching.StartupTenderMatchRepository;
import com.procurepilot.opportunity.Opportunity;
import com.procurepilot.opportunity.OpportunityRepository;
import com.procurepilot.opportunity.OpportunityStage;
import com.procurepilot.opportunity.OpportunityTask;
import com.procurepilot.opportunity.OpportunityTaskRepository;
import com.procurepilot.requirement.RequirementOperator;
import com.procurepilot.requirement.RequirementType;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.requirement.TenderRequirementRepository;
import com.procurepilot.startup.*;
import com.procurepilot.tender.Tender;
import com.procurepilot.tender.TenderRepository;
import com.procurepilot.tender.TenderSection;
import com.procurepilot.tender.TenderSectionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StartupRepository startupRepository;
    private final StartupCapabilityRepository capabilityRepository;
    private final StartupCertificationRepository certificationRepository;
    private final StartupDocumentRepository documentRepository;
    private final EligibilityRuleRepository ruleRepository;
    private final TenderRepository tenderRepository;
    private final TenderSectionRepository sectionRepository;
    private final TenderRequirementRepository requirementRepository;
    private final DocumentChunkRepository chunkRepository;
    private final ComplianceItemRepository complianceItemRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityTaskRepository opportunityTaskRepository;
    private final StartupTenderMatchRepository matchRepository;
    private final MatchingEngine matchingEngine;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           StartupRepository startupRepository,
                           StartupCapabilityRepository capabilityRepository,
                           StartupCertificationRepository certificationRepository,
                           StartupDocumentRepository documentRepository,
                           EligibilityRuleRepository ruleRepository,
                           TenderRepository tenderRepository,
                           TenderSectionRepository sectionRepository,
                           TenderRequirementRepository requirementRepository,
                           DocumentChunkRepository chunkRepository,
                           ComplianceItemRepository complianceItemRepository,
                           OpportunityRepository opportunityRepository,
                           OpportunityTaskRepository opportunityTaskRepository,
                           StartupTenderMatchRepository matchRepository,
                           MatchingEngine matchingEngine,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.startupRepository = startupRepository;
        this.capabilityRepository = capabilityRepository;
        this.certificationRepository = certificationRepository;
        this.documentRepository = documentRepository;
        this.ruleRepository = ruleRepository;
        this.tenderRepository = tenderRepository;
        this.sectionRepository = sectionRepository;
        this.requirementRepository = requirementRepository;
        this.chunkRepository = chunkRepository;
        this.complianceItemRepository = complianceItemRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityTaskRepository = opportunityTaskRepository;
        this.matchRepository = matchRepository;
        this.matchingEngine = matchingEngine;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // 1. Roles
        Role roleStartup = roleRepository.findByName("ROLE_STARTUP").orElseGet(() -> roleRepository.save(new Role("ROLE_STARTUP")));
        Role roleAdmin = roleRepository.findByName("ROLE_PROCUREMENT_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_PROCUREMENT_ADMIN")));
        Role roleSysAdmin = roleRepository.findByName("ROLE_SYSTEM_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_SYSTEM_ADMIN")));

        // 2. Users
        User founder = userRepository.findByEmail("founder@aerodef.in").orElseGet(() -> {
            User u = new User();
            u.setEmail("founder@aerodef.in");
            u.setFullName("Arjun Sharma");
            u.setPhone("+91 9876543210");
            u.setPasswordHash(passwordEncoder.encode("Password123!"));
            u.setRoles(Collections.singleton(roleStartup));
            return userRepository.save(u);
        });

        User admin = userRepository.findByEmail("admin@procurepilot.gov.in").orElseGet(() -> {
            User u = new User();
            u.setEmail("admin@procurepilot.gov.in");
            u.setFullName("Dr. Ramesh Kumar (Director Procurement)");
            u.setPhone("+91 9811223344");
            u.setPasswordHash(passwordEncoder.encode("AdminSecret123!"));
            u.setRoles(Collections.singleton(roleAdmin));
            return userRepository.save(u);
        });

        // 3. Startup Profile
        Startup startup = startupRepository.findByUserId(founder.getId()).orElseGet(() -> {
            Startup s = new Startup();
            s.setUser(founder);
            s.setCompanyName("AeroDef AI Technologies Pvt Ltd");
            s.setLegalName("AeroDef Artificial Intelligence Technologies Private Limited");
            s.setDpiitRecognized(true);
            s.setDpiitNumber("DIPP98741");
            s.setUdyamNumber("UDYAM-DL-01-0023412");
            s.setIncorporationDate(LocalDate.of(2023, 3, 15));
            s.setAnnualTurnoverInr(new BigDecimal("18000000.00")); // 1.8 Cr
            s.setNetWorthInr(new BigDecimal("25000000.00"));
            s.setPrimarySector("Defense, AI & Computer Vision");
            s.setState("Delhi");
            s.setCity("New Delhi");
            s.setWebsite("https://aerodef.in");
            s.setCapabilityFingerprint("ai-surveillance;edge-computing;drone-telemetry;object-detection;iso-9001;embedded-linux");
            return startupRepository.save(s);
        });

        // 4. Capabilities
        if (capabilityRepository.findByStartupId(startup.getId()).isEmpty()) {
            saveCapability(startup, "Artificial Intelligence", "Edge AI Video Analytics & Object Detection", "EXPERT", "Low-latency YOLO & TensorRT models on NVIDIA Jetson embedded hardware");
            saveCapability(startup, "Unmanned Systems", "Autonomous Drone Navigation & Telemetry", "ADVANCED", "MAVLink & ROS2 based automated patrol flight controllers");
            saveCapability(startup, "Software & Cloud", "Real-time Command & Control Dashboard", "EXPERT", "High-throughput WebRTC video streaming and geospatial event tracking");
        }

        // 5. Certifications
        if (certificationRepository.findByStartupId(startup.getId()).isEmpty()) {
            saveCert(startup, "ISO 9001:2015", "ISO-QMS-2023-9912", "Bureau Veritas India", LocalDate.of(2023, 6, 1), LocalDate.of(2026, 5, 31));
            saveCert(startup, "DPIIT Recognition Certificate", "DIPP98741", "DPIIT", LocalDate.of(2023, 4, 10), LocalDate.of(2033, 4, 9));
        }

        // 6. Documents
        if (documentRepository.findByStartupId(startup.getId()).isEmpty()) {
            saveDoc(startup, "DPIIT_CERTIFICATE", "DPIIT_Recognition_Certificate.pdf", "/uploads/DPIIT_Recognition_Certificate.pdf");
            saveDoc(startup, "GST_CERTIFICATE", "GST_Registration_07AABCA1234F1Z5.pdf", "/uploads/GST_Registration.pdf");
            saveDoc(startup, "PAN_CARD", "Company_PAN_AABCA1234F.pdf", "/uploads/Company_PAN.pdf");
            saveDoc(startup, "ISO_9001", "ISO_9001_Quality_Management.pdf", "/uploads/ISO_9001.pdf");
        }

        // 7. Master Rules
        saveRule("GFR-161-IV", "Exemption from Prior Turnover and Prior Experience for Startups", "Ministry of Finance / DoE", "Rule 161(iv) of General Financial Rules (GFR) 2017 allows relaxation of prior turnover and prior experience criteria for recognized Startups, subject to meeting technical specifications and quality standards.", "https://doe.gov.in/procurement-policy", LocalDate.of(2017, 3, 8));
        saveRule("DPIIT-OM-2016", "Relaxation of Norms for Public Procurement for Startups", "DPIIT", "Office Memorandum directing all Central Ministries/Departments to relax prior turnover & experience norms.", "https://www.startupindia.gov.in", LocalDate.of(2016, 3, 10));
        saveRule("PPP-MII-2017", "Public Procurement (Preference to Make in India) Order", "DPIIT", "Class-I local supplier preference for local value addition >= 50%.", "https://dpiit.gov.in/public-procurement-order", LocalDate.of(2017, 6, 15));
        saveRule("MSE-EMD-EXEMPTION", "EMD and Tender Fee Exemption for MSEs & Startups", "Ministry of MSME / GeM", "100% exemption from Earnest Money Deposit (EMD) for DPIIT recognized startups.", "https://gem.gov.in/help", LocalDate.of(2018, 1, 1));

        // 8. Tenders
        Tender t1 = saveTender(
                "GEM/2026/B/892301",
                "Procurement of AI-Powered Edge Drone Surveillance & Intelligent Video Analytics System for Perimeter Security",
                "Ministry of Defence / Smart Cities Mission",
                "Directorate General of Border Intelligence & Urban Surveillance",
                "Defence & Surveillance AI",
                new BigDecimal("15000000.00"), // 1.5 Cr
                new BigDecimal("300000.00"),
                Instant.now().minus(10, ChronoUnit.DAYS),
                Instant.now().plus(25, ChronoUnit.DAYS),
                "GeM (Government e-Marketplace)",
                "https://gem.gov.in/tenders/gem-2026-b-892301"
        );

        Tender t2 = saveTender(
                "AIIMS/PROC/2026/IOT-77",
                "Supply, Installation & Maintenance of Smart Hospital IoT Patient Telemetry & Continuous Vitals Monitoring Network",
                "All India Institute of Medical Sciences (AIIMS) New Delhi",
                "Department of Biomedical Engineering & Hospital Informatics",
                "Healthcare & Medical IoT",
                new BigDecimal("45000000.00"), // 4.5 Cr
                new BigDecimal("900000.00"),
                Instant.now().minus(5, ChronoUnit.DAYS),
                Instant.now().plus(12, ChronoUnit.DAYS),
                "CPPP (Central Public Procurement Portal)",
                "https://eprocure.gov.in/epublish/app?tenderId=AIIMS-IOT-77"
        );

        Tender t3 = saveTender(
                "NHAI/TECH/2026/CIVIL-402",
                "Engineering, Procurement & Construction (EPC) of 4-Lane Highway Bypass and Grade Separators on NH-48 Corridor",
                "National Highways Authority of India (NHAI)",
                "Ministry of Road Transport and Highways",
                "Heavy Civil Infrastructure",
                new BigDecimal("750000000.00"), // 75 Cr
                new BigDecimal("15000000.00"),
                Instant.now().minus(15, ChronoUnit.DAYS),
                Instant.now().plus(35, ChronoUnit.DAYS),
                "CPPP (eProcurement)",
                "https://eprocure.gov.in/nhai/civil-402"
        );

        Tender t4 = saveTender(
                "ISRO/SAT/2026/099",
                "Development & Deployment of Autonomous Satellite Ground Station Telemetry AI Controller",
                "Indian Space Research Organisation (ISRO)",
                "Telemetry, Tracking and Command Network (ISTRAC)",
                "Space & Embedded Telemetry",
                new BigDecimal("28000000.00"), // 2.8 Cr
                new BigDecimal("560000.00"),
                Instant.now().minus(8, ChronoUnit.DAYS),
                Instant.now().plus(18, ChronoUnit.DAYS),
                "GeM (Government e-Marketplace)",
                "https://gem.gov.in/tenders/isro-sat-2026-099"
        );

        Tender t5 = saveTender(
                "BEL/DEF/2025/EO-11",
                "Supply of Ruggedized Edge Video Analytics Embedded Units for Armoured Combat Vehicles",
                "Bharat Electronics Limited (BEL)",
                "Military Radar & Optronics Division",
                "Defence Electronics & Rugged AI",
                new BigDecimal("35000000.00"), // 3.5 Cr
                new BigDecimal("700000.00"),
                Instant.now().minus(30, ChronoUnit.DAYS),
                Instant.now().minus(2, ChronoUnit.DAYS),
                "CPPP (eProcurement)",
                "https://eprocure.gov.in/bel/eo-11"
        );

        // 9. Requirements for Tender 1 (Defence AI Drone)
        if (requirementRepository.findByTenderId(t1.getId()).isEmpty()) {
            saveReq(t1, RequirementType.TURNOVER, "The bidder must have an average annual turnover of at least ₹1.00 Crore over the last three financial years. (DPIIT recognized startups exempted as per GFR 161(iv))", "10000000", "INR", RequirementOperator.GTE, true, false, 4, "Clause 3.1 - Financial Eligibility", "Average annual turnover shall not be less than INR 1.00 Cr in preceding three financial years.");
            saveReq(t1, RequirementType.EXPERIENCE, "Bidder should have minimum 2 years of proven experience in deploying Computer Vision, AI Object Detection or Unmanned Aerial Systems.", "2", "YEARS", RequirementOperator.GTE, true, false, 5, "Clause 3.2 - Technical Experience", "The bidder should possess minimum 2 years experience in Computer Vision or UAV surveillance systems.");
            saveReq(t1, RequirementType.CERTIFICATION, "Bidder must possess a valid ISO 9001:2015 Quality Management System Certification at the time of bidding.", "ISO 9001:2015", "TEXT", RequirementOperator.EQUALS, true, false, 7, "Clause 4.1 - Quality Certifications", "Copy of valid ISO 9001:2015 certification from an accredited body is mandatory.");
            saveReq(t1, RequirementType.TECHNICAL, "The edge AI unit must support real-time low-latency object detection (>25 FPS at 1080p) and automated geo-referenced target classification.", "25 FPS @ 1080p", "TEXT", RequirementOperator.CONTAINS, true, false, 12, "Clause 6.3 - Technical Specifications", "Edge inference system shall achieve frame rates >= 25 FPS with multi-class vehicle and personnel recognition.");
            saveReq(t1, RequirementType.LEGAL, "The bidder must be an Indian entity registered under Companies Act or LLP Act, with valid GSTIN and PAN.", "GSTIN & PAN", "TEXT", RequirementOperator.EQUALS, true, false, 3, "Clause 2.1 - Statutory Registrations", "Valid GST registration certificate and permanent account number (PAN) must be furnished.");
        }

        // Requirements for Tender 2 (AIIMS Healthcare IoT)
        if (requirementRepository.findByTenderId(t2.getId()).isEmpty()) {
            saveReq(t2, RequirementType.TURNOVER, "Average annual turnover of ₹2.50 Crore during last three fiscal years. (DPIIT startups eligible for relaxation under GFR 161(iv))", "25000000", "INR", RequirementOperator.GTE, true, false, 3, "Clause 3.1 - Financial Capability", "The bidder must have an average annual turnover of at least ₹2.50 Cr.");
            saveReq(t2, RequirementType.EXPERIENCE, "Minimum 3 years demonstrable experience in Medical IoT, Patient Telemetry, or Biometric sensor systems.", "3", "YEARS", RequirementOperator.GTE, true, false, 4, "Clause 3.2 - Healthcare IoT Experience", "Bidder should have completed at least 2 hospital IoT or telemetry deployments.");
            saveReq(t2, RequirementType.CERTIFICATION, "Valid ISO 13485 (Medical Devices QMS) or ISO 9001:2015 Certification is required.", "ISO 9001:2015", "TEXT", RequirementOperator.EQUALS, true, false, 6, "Clause 4.1 - Quality Standards", "ISO 13485 or ISO 9001 certification mandatory.");
            saveReq(t2, RequirementType.TECHNICAL, "Continuous vitals telemetry gateway supporting HL7/FHIR protocols and edge alert latency < 500ms.", "HL7 / FHIR Gateway", "TEXT", RequirementOperator.CONTAINS, true, false, 9, "Clause 5.2 - System Architecture", "Gateway must stream patient vitals with sub-500ms latency to Central Nurse Station.");
            saveReq(t2, RequirementType.LEGAL, "Registered Indian company with active CDSCO / biomedical equipment compliance certificate.", "CDSCO / Statutory", "TEXT", RequirementOperator.EQUALS, true, false, 2, "Clause 2.1 - Statutory Registrations", "GSTIN, PAN and medical equipment distribution clearance required.");
        }

        // Requirements for Tender 3 (NHAI Civil Highway EPC)
        if (requirementRepository.findByTenderId(t3.getId()).isEmpty()) {
            saveReq(t3, RequirementType.TURNOVER, "Minimum average annual construction turnover of ₹50.00 Crore over the last 5 financial years.", "500000000", "INR", RequirementOperator.GTE, true, false, 5, "Clause 3.1 - Highway EPC Financial Turn", "Turnover must exceed ₹50 Cr from civil infrastructure works.");
            saveReq(t3, RequirementType.EXPERIENCE, "Minimum 5 years prime contractor experience in 4/6 Lane National Highway EPC or Expressways.", "5", "YEARS", RequirementOperator.GTE, true, false, 6, "Clause 3.2 - EPC Experience", "Bidder must have completed at least 25 km of 4-lane highway EPC works.");
            saveReq(t3, RequirementType.CERTIFICATION, "MoRTH Grade-1 Registered Highway Contractor license or CPWD Class-A license.", "MoRTH Grade-1", "TEXT", RequirementOperator.EQUALS, true, false, 8, "Clause 4.1 - Contractor Grade", "MoRTH Grade-1 contractor certification mandatory.");
            saveReq(t3, RequirementType.TECHNICAL, "Ownership or verified lease of 400 TPH Asphalt Hot Mix Plant and GPS Sensor Pavers.", "400 TPH Asphalt Plant", "TEXT", RequirementOperator.CONTAINS, true, false, 14, "Clause 7.1 - Plant & Machinery", "Automated hot mix batch plant and asphalt pavers mandatory.");
            saveReq(t3, RequirementType.LEGAL, "Positive Net Worth of at least ₹15.00 Crore certified by Chartered Accountant.", "150000000", "INR", RequirementOperator.GTE, true, false, 5, "Clause 3.3 - Net Worth", "Net worth certificate with UDIN from registered CA.");
        }

        // Requirements for Tender 4 (ISRO Satellite AI Controller)
        if (requirementRepository.findByTenderId(t4.getId()).isEmpty()) {
            saveReq(t4, RequirementType.TURNOVER, "Average annual turnover of ₹1.50 Crore during last three fiscal years. (Relaxed for DPIIT Startups under GFR 161(iv))", "15000000", "INR", RequirementOperator.GTE, true, false, 3, "Clause 2.1 - Financial Turnover", "Average annual turnover not less than 1.5 Cr.");
            saveReq(t4, RequirementType.EXPERIENCE, "Minimum 2 years experience in Embedded Systems, Telemetry processing or High-Reliability Software.", "2", "YEARS", RequirementOperator.GTE, true, false, 4, "Clause 2.2 - Technical Experience", "Experience in real-time embedded control systems.");
            saveReq(t4, RequirementType.CERTIFICATION, "ISO 9001:2015 Quality Management System Certification.", "ISO 9001:2015", "TEXT", RequirementOperator.EQUALS, true, false, 5, "Clause 3.1 - Quality Standards", "Valid ISO 9001 certificate.");
            saveReq(t4, RequirementType.TECHNICAL, "Edge processing support for high-throughput space telemetry demodulation and packet decoding.", "Telemetry / Edge AI", "TEXT", RequirementOperator.CONTAINS, true, false, 8, "Clause 4.2 - Technical Specifications", "Real-time telemetry decoding with zero frame drop.");
            saveReq(t4, RequirementType.LEGAL, "Indian Entity with DPIIT Recognition and valid GST/PAN.", "GSTIN & PAN", "TEXT", RequirementOperator.EQUALS, true, false, 2, "Clause 1.2 - Statutory Eligibility", "Valid Indian company registration.");
        }

        // Requirements for Tender 5 (BEL Defence EO/IR Processing)
        if (requirementRepository.findByTenderId(t5.getId()).isEmpty()) {
            saveReq(t5, RequirementType.TURNOVER, "Average annual turnover of ₹2.00 Crore. (DPIIT Startups exempted under GFR 161(iv))", "20000000", "INR", RequirementOperator.GTE, true, false, 3, "Clause 3.1 - Financial Norms", "Turnover requirement of ₹2.00 Cr.");
            saveReq(t5, RequirementType.EXPERIENCE, "Minimum 2 years experience in Defense Grade Embedded AI and Rugged Computing.", "2", "YEARS", RequirementOperator.GTE, true, false, 4, "Clause 3.2 - Defense Experience", "Track record in ruggedized embedded systems.");
            saveReq(t5, RequirementType.CERTIFICATION, "ISO 9001:2015 Certification.", "ISO 9001:2015", "TEXT", RequirementOperator.EQUALS, true, false, 6, "Clause 4.1 - Quality Certifications", "ISO 9001 QMS certificate.");
            saveReq(t5, RequirementType.TECHNICAL, "MIL-STD-810G rugged edge AI computer with NVIDIA Jetson or equivalent SoC.", "MIL-STD Edge AI", "TEXT", RequirementOperator.CONTAINS, true, false, 10, "Clause 5.1 - Hardware Specifications", "MIL-STD compliant embedded processing box.");
            saveReq(t5, RequirementType.LEGAL, "Indigenous Make in India Class-I supplier with >= 50% local content.", "Class-I Local Supplier", "TEXT", RequirementOperator.EQUALS, true, false, 2, "Clause 1.5 - Make in India", "Self-declaration of local value addition.");
        }

        // 10. Chunks for Grounded RAG
        if (chunkRepository.findByTenderIdOrderByChunkIndexAsc(t1.getId()).isEmpty()) {
            saveChunk(t1, 4, "Clause 3.1 - Financial Eligibility", 1, "Clause 3.1: Minimum Financial Eligibility. The bidder should have an average annual turnover of at least ₹1.00 Crore over the last three financial years. Relaxation Note: In accordance with Rule 161(iv) of General Financial Rules (GFR) 2017 and DPIIT notification No. 5(4)/2017-BE-I, recognized startups shall be exempted from the condition of prior turnover, provided they meet quality and technical specifications.");
            saveChunk(t1, 5, "Clause 3.2 - Technical Experience", 2, "Clause 3.2: Prior Experience Criteria. The bidder should possess a minimum of 2 years experience in the deployment of Computer Vision, Edge AI, or Unmanned Aerial Systems. For DPIIT recognized startups, prior experience criteria may be relaxed if the bidder demonstrates certified technical capability and satisfactory prototype test results as evaluated by the technical evaluation committee.");
            saveChunk(t1, 7, "Clause 4.1 - Quality Certifications", 3, "Clause 4.1: Quality Standards & Statutory Compliance. The bidder must possess a valid ISO 9001:2015 Quality Management System Certification. Startups claiming exemption from technical experience must hold ISO 9001 or equivalent CMMI Level 3 certification to establish quality management protocols.");
            saveChunk(t1, 8, "Clause 4.5 - Startup Benefits & EMD Exemption", 4, "Clause 4.5: Benefits for Startups & MSEs. As per Government of India public procurement policies, DPIIT recognized startups are 100% exempt from submitting Earnest Money Deposit (EMD) of ₹3,00,000. Startups must upload their valid DPIIT Certificate of Recognition on the portal at the time of online bid submission.");
            saveChunk(t1, 14, "Clause 7.2 - Local Content & Delivery Schedule", 5, "Clause 7.2: Make in India & Delivery. The procurement falls under Class-I Local Supplier category with minimum 50% local value addition. Complete supply and commissioning shall be completed within 90 days from the date of Award of Contract (AOC).");
        }

        if (chunkRepository.findByTenderIdOrderByChunkIndexAsc(t2.getId()).isEmpty()) {
            saveChunk(t2, 3, "Clause 3.1 - Financial Capability", 1, "Clause 3.1: Minimum Financial Eligibility. The bidder should have an average annual turnover of at least ₹2.50 Crore over the last three fiscal years. DPIIT recognized startups qualify for turnover relaxation under GFR 161(iv).");
            saveChunk(t2, 4, "Clause 3.2 - Healthcare Experience", 2, "Clause 3.2: Domain Experience. Minimum 3 years experience in IoT sensor gateways, telemetry, or hospital patient management networks.");
            saveChunk(t2, 6, "Clause 4.1 - Quality Standards", 3, "Clause 4.1: Quality & Medical Device Standards. Bidder must hold valid ISO 13485 or ISO 9001:2015 certification for electronic devices.");
            saveChunk(t2, 8, "Clause 4.6 - EMD Exemption", 4, "Clause 4.6: Startups holding valid DPIIT recognition are 100% exempt from payment of ₹9,00,000 EMD.");
            saveChunk(t2, 9, "Clause 5.2 - System Architecture", 5, "Clause 5.2: Gateway Telemetry. System must support standard HL7/FHIR communication with hospital central server.");
        }

        if (chunkRepository.findByTenderIdOrderByChunkIndexAsc(t3.getId()).isEmpty()) {
            saveChunk(t3, 5, "Clause 3.1 - Financial Criteria", 1, "Clause 3.1: Heavy Civil Works Turnover. Average annual construction turnover shall not be less than ₹50.00 Cr in preceding five financial years. Non-relaxable due to scale.");
            saveChunk(t3, 6, "Clause 3.2 - Highway EPC Experience", 2, "Clause 3.2: EPC Track Record. Bidder must have executed minimum 25 km of 4/6 lane National Highway projects.");
            saveChunk(t3, 8, "Clause 4.1 - Contractor License", 3, "Clause 4.1: Statutory Grade. MoRTH Grade-1 contractor license or CPWD Class-A registration is mandatory.");
            saveChunk(t3, 11, "Clause 5.2 - EMD Requirement", 4, "Clause 5.2: Earnest Money Deposit. Bank Guarantee of ₹1.50 Cr mandatory for all bidding consortia.");
        }

        // 11. Initial Matches
        saveInitialMatch(startup, t1);
        saveInitialMatch(startup, t2);
        saveInitialMatch(startup, t3);
        saveInitialMatch(startup, t4);
        saveInitialMatch(startup, t5);

        // 12. Compliance Items for all Tenders
        if (complianceItemRepository.findByTenderIdAndStartupId(t1.getId(), startup.getId()).isEmpty()) {
            saveCompItem(t1, startup, "Company PAN & GST Registration Certificate", "Statutory Documents", true, ComplianceStatus.READY, 3);
            saveCompItem(t1, startup, "DPIIT Recognition Certificate (for EMD & Turnover Relaxation)", "Startup Credentials", true, ComplianceStatus.READY, 8);
            saveCompItem(t1, startup, "ISO 9001:2015 Quality Management Certificate", "Quality & Standards", true, ComplianceStatus.READY, 7);
            saveCompItem(t1, startup, "Class-I Local Content (Make in India >=50%) Self-Declaration", "Statutory Declarations", true, ComplianceStatus.MISSING, 14);
            saveCompItem(t1, startup, "OEM / Prototype Technical Test Certificate", "Technical Submission", true, ComplianceStatus.NEEDS_REVIEW, 5);
        }

        if (complianceItemRepository.findByTenderIdAndStartupId(t2.getId(), startup.getId()).isEmpty()) {
            saveCompItem(t2, startup, "Company PAN & GST Registration Certificate", "Statutory Documents", true, ComplianceStatus.READY, 2);
            saveCompItem(t2, startup, "DPIIT Recognition Certificate (for EMD Exemption)", "Startup Credentials", true, ComplianceStatus.READY, 8);
            saveCompItem(t2, startup, "ISO 9001:2015 Quality Management Certificate", "Quality & Standards", true, ComplianceStatus.READY, 6);
            saveCompItem(t2, startup, "HL7 / FHIR Gateway Integration Architecture Document", "Technical Submission", true, ComplianceStatus.NEEDS_REVIEW, 9);
            saveCompItem(t2, startup, "Medical Device Safety & Telemetry Test Report", "Quality & Compliance", true, ComplianceStatus.MISSING, 6);
        }

        if (complianceItemRepository.findByTenderIdAndStartupId(t3.getId(), startup.getId()).isEmpty()) {
            saveCompItem(t3, startup, "Company PAN & GST Registration Certificate", "Statutory Documents", true, ComplianceStatus.READY, 3);
            saveCompItem(t3, startup, "DPIIT Recognition Certificate", "Startup Credentials", true, ComplianceStatus.READY, 11);
            saveCompItem(t3, startup, "MoRTH Grade-1 Highway Contractor License", "Statutory Licensing", true, ComplianceStatus.MISSING, 8);
            saveCompItem(t3, startup, "Hot Mix Plant (400 TPH) Ownership / Lease Agreement", "Machinery & Equipment", true, ComplianceStatus.MISSING, 14);
            saveCompItem(t3, startup, "CA Net Worth Certificate (> ₹15.00 Cr)", "Financial Submission", true, ComplianceStatus.MISSING, 5);
        }

        if (complianceItemRepository.findByTenderIdAndStartupId(t4.getId(), startup.getId()).isEmpty()) {
            saveCompItem(t4, startup, "Company PAN & GST Registration Certificate", "Statutory Documents", true, ComplianceStatus.READY, 2);
            saveCompItem(t4, startup, "DPIIT Recognition Certificate", "Startup Credentials", true, ComplianceStatus.READY, 3);
            saveCompItem(t4, startup, "ISO 9001:2015 Certificate", "Quality & Standards", true, ComplianceStatus.READY, 5);
            saveCompItem(t4, startup, "High-Reliability Embedded Software Architecture Plan", "Technical Bid", true, ComplianceStatus.READY, 8);
        }

        if (complianceItemRepository.findByTenderIdAndStartupId(t5.getId(), startup.getId()).isEmpty()) {
            saveCompItem(t5, startup, "Company PAN & GST Registration Certificate", "Statutory Documents", true, ComplianceStatus.READY, 2);
            saveCompItem(t5, startup, "DPIIT Recognition Certificate", "Startup Credentials", true, ComplianceStatus.READY, 3);
            saveCompItem(t5, startup, "Make in India Class-I Supplier Declaration", "Statutory Declarations", true, ComplianceStatus.READY, 2);
            saveCompItem(t5, startup, "MIL-STD Environmental Test Compliance Certificate", "Technical Envelope", true, ComplianceStatus.READY, 10);
        }

        // 13. Pipeline Opportunities across all 5 Kanban Stages
        // Column 1: INTERESTED -> Tender 2 (AIIMS IoT)
        if (opportunityRepository.findByStartupIdAndTenderId(startup.getId(), t2.getId()).isEmpty()) {
            Opportunity opp2 = new Opportunity();
            opp2.setStartup(startup);
            opp2.setTender(t2);
            opp2.setStage(OpportunityStage.INTERESTED);
            opp2.setPriority("MEDIUM");
            opp2.setReadinessPercent(60);
            opp2.setTargetSubmissionDate(LocalDate.now().plusDays(12));
            opp2.setNotes("Healthcare IoT opportunity. GFR 161(iv) turnover relaxation applicable.");
            Opportunity savedOpp2 = opportunityRepository.save(opp2);
            saveTask(savedOpp2, "Review AIIMS biomedical telemetry interoperability specifications", true);
            saveTask(savedOpp2, "Verify HL7 / FHIR data gateway cloud compatibility", false);
        }

        // Column 2: PREPARING -> Tender 1 (Edge AI Drone)
        if (opportunityRepository.findByStartupIdAndTenderId(startup.getId(), t1.getId()).isEmpty()) {
            Opportunity opp1 = new Opportunity();
            opp1.setStartup(startup);
            opp1.setTender(t1);
            opp1.setStage(OpportunityStage.PREPARING);
            opp1.setPriority("HIGH");
            opp1.setReadinessPercent(60);
            opp1.setTargetSubmissionDate(LocalDate.now().plusDays(20));
            opp1.setNotes("High-fit opportunity. Technical specifications directly aligned with Edge AI stack.");
            Opportunity savedOpp1 = opportunityRepository.save(opp1);
            saveTask(savedOpp1, "Generate and sign Make-in-India 50% Local Content Undertaking", false);
            saveTask(savedOpp1, "Consolidate Edge AI Jetson benchmark test logs for Technical Bid envelope", true);
            saveTask(savedOpp1, "Verify EMD Exemption document tag on GeM submission portal", false);
        }

        // Column 3: SUBMITTED -> Tender 4 (ISRO Sat Telemetry)
        if (opportunityRepository.findByStartupIdAndTenderId(startup.getId(), t4.getId()).isEmpty()) {
            Opportunity opp4 = new Opportunity();
            opp4.setStartup(startup);
            opp4.setTender(t4);
            opp4.setStage(OpportunityStage.SUBMITTED);
            opp4.setPriority("HIGH");
            opp4.setReadinessPercent(100);
            opp4.setTargetSubmissionDate(LocalDate.now().plusDays(5));
            opp4.setNotes("Technical and financial bid envelopes submitted on GeM portal. Awaiting opening.");
            Opportunity savedOpp4 = opportunityRepository.save(opp4);
            saveTask(savedOpp4, "Bid acknowledgment receipt downloaded and archived", true);
            saveTask(savedOpp4, "Prepare technical presentation for ISTRAC evaluation committee", true);
        }

        // Column 4: EVALUATION -> Tender 3 (NHAI Civil Highway)
        if (opportunityRepository.findByStartupIdAndTenderId(startup.getId(), t3.getId()).isEmpty()) {
            Opportunity opp3 = new Opportunity();
            opp3.setStartup(startup);
            opp3.setTender(t3);
            opp3.setStage(OpportunityStage.EVALUATION);
            opp3.setPriority("LOW");
            opp3.setReadinessPercent(40);
            opp3.setTargetSubmissionDate(LocalDate.now().minusDays(2));
            opp3.setNotes("Heavy civil construction tender. Technical bid under committee scrutiny.");
            Opportunity savedOpp3 = opportunityRepository.save(opp3);
            saveTask(savedOpp3, "Respond to NHAI pre-qualification technical query on JV consortia", false);
        }

        // Column 5: OUTCOME -> Tender 5 (BEL Defence EO/IR)
        if (opportunityRepository.findByStartupIdAndTenderId(startup.getId(), t5.getId()).isEmpty()) {
            Opportunity opp5 = new Opportunity();
            opp5.setStartup(startup);
            opp5.setTender(t5);
            opp5.setStage(OpportunityStage.OUTCOME);
            opp5.setPriority("HIGH");
            opp5.setReadinessPercent(100);
            opp5.setTargetSubmissionDate(LocalDate.now().minusDays(10));
            opp5.setNotes("Award of Contract (AOC) issued! L1 Winner for Edge Video Processing Modules.");
            Opportunity savedOpp5 = opportunityRepository.save(opp5);
            saveTask(savedOpp5, "Sign Master Supply Agreement with BEL Bangalore Division", true);
            saveTask(savedOpp5, "Submit Performance Security Guarantee (3% of contract value)", true);
        }
    }

    private void saveInitialMatch(Startup startup, Tender tender) {
        if (matchRepository.findByStartupIdAndTenderId(startup.getId(), tender.getId()).isEmpty()) {
            List<TenderRequirement> reqs = requirementRepository.findByTenderId(tender.getId());
            StartupTenderMatch match = matchingEngine.calculateMatch(startup, tender, reqs);
            matchRepository.save(match);
        }
    }

    private void saveCapability(Startup s, String cat, String name, String prof, String desc) {
        StartupCapability c = new StartupCapability();
        c.setStartup(s);
        c.setCategory(cat);
        c.setName(name);
        c.setProficiencyLevel(prof);
        c.setDescription(desc);
        capabilityRepository.save(c);
    }

    private void saveCert(Startup s, String type, String no, String body, LocalDate issue, LocalDate exp) {
        StartupCertification c = new StartupCertification();
        c.setStartup(s);
        c.setCertType(type);
        c.setCertNumber(no);
        c.setIssuingBody(body);
        c.setIssueDate(issue);
        c.setExpiryDate(exp);
        certificationRepository.save(c);
    }

    private void saveDoc(Startup s, String type, String name, String path) {
        StartupDocument d = new StartupDocument();
        d.setStartup(s);
        d.setDocType(type);
        d.setFileName(name);
        d.setFilePath(path);
        d.setFileSize(250000L);
        d.setMimeType("application/pdf");
        documentRepository.save(d);
    }

    private void saveRule(String code, String name, String auth, String desc, String url, LocalDate date) {
        if (ruleRepository.findByRuleCode(code).isEmpty()) {
            EligibilityRule r = new EligibilityRule();
            r.setRuleCode(code);
            r.setName(name);
            r.setAuthority(auth);
            r.setDescription(desc);
            r.setSourceUrl(url);
            r.setEffectiveDate(date);
            ruleRepository.save(r);
        }
    }

    private Tender saveTender(String ref, String title, String dept, String auth, String cat, BigDecimal val, BigDecimal emd, Instant pub, Instant close, String portal, String url) {
        return tenderRepository.findByTenderRefNo(ref).orElseGet(() -> {
            Tender t = new Tender();
            t.setTenderRefNo(ref);
            t.setTitle(title);
            t.setDepartment(dept);
            t.setAuthority(auth);
            t.setCategory(cat);
            t.setEstimatedValueInr(val);
            t.setEmdInr(emd);
            t.setPublishedDate(pub);
            t.setClosingDate(close);
            t.setSourcePortal(portal);
            t.setSourceUrl(url);
            return tenderRepository.save(t);
        });
    }

    private void saveReq(Tender t, RequirementType type, String text, String val, String unit, RequirementOperator op, boolean mand, boolean pref, int page, String sec, String snip) {
        TenderRequirement r = new TenderRequirement();
        r.setTender(t);
        r.setType(type);
        r.setRequirementText(text);
        r.setNormalizedValue(val);
        r.setUnit(unit);
        r.setOperator(op);
        r.setMandatory(mand);
        r.setPreferred(pref);
        r.setSourcePage(page);
        r.setSourceSection(sec);
        r.setSourceSnippet(snip);
        requirementRepository.save(r);
    }

    private void saveChunk(Tender t, int page, String sec, int idx, String content) {
        DocumentChunk c = new DocumentChunk();
        c.setTender(t);
        c.setPageNumber(page);
        c.setSectionTitle(sec);
        c.setChunkIndex(idx);
        c.setContent(content);
        chunkRepository.save(c);
    }

    private void saveCompItem(Tender t, Startup s, String title, String cat, boolean mand, ComplianceStatus st, int page) {
        ComplianceItem item = new ComplianceItem();
        item.setTender(t);
        item.setStartup(s);
        item.setTitle(title);
        item.setCategory(cat);
        item.setMandatory(mand);
        item.setStatus(st);
        item.setTenderSourcePage(page);
        item.setDueDate(LocalDate.now().plusDays(15));
        complianceItemRepository.save(item);
    }

    private void saveTask(Opportunity opp, String title, boolean comp) {
        OpportunityTask t = new OpportunityTask();
        t.setOpportunity(opp);
        t.setTitle(title);
        t.setCompleted(comp);
        t.setDueDate(LocalDate.now().plusDays(10));
        opportunityTaskRepository.save(t);
    }
}
