-- ProcurePilot Flyway Migration V2: SIH Demonstration Seed Data

-- 1. Insert Roles
INSERT INTO roles (name) VALUES ('ROLE_STARTUP') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_PROCUREMENT_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_SYSTEM_ADMIN') ON CONFLICT (name) DO NOTHING;

-- 2. Insert Users (BCrypt hash for 'Password123!' is $2a$10$7Z8V4R3K1q... and $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG)
INSERT INTO users (id, email, password_hash, full_name, phone, status)
VALUES (1, 'founder@aerodef.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Arjun Sharma', '+91 9876543210', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (id, email, password_hash, full_name, phone, status)
VALUES (2, 'admin@procurepilot.gov.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Dr. Ramesh Kumar (Director Procurement)', '+91 9811223344', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

-- Map User Roles
INSERT INTO user_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE name = 'ROLE_STARTUP'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT 2, id FROM roles WHERE name = 'ROLE_PROCUREMENT_ADMIN'
ON CONFLICT DO NOTHING;

-- 3. Insert Startup Profile
INSERT INTO startups (id, user_id, company_name, legal_name, dpiit_recognized, dpiit_number, udyam_number, incorporation_date, annual_turnover_inr, net_worth_inr, primary_sector, state, city, website, capability_fingerprint)
VALUES (
    1,
    1,
    'AeroDef AI Technologies Pvt Ltd',
    'AeroDef Artificial Intelligence Technologies Private Limited',
    TRUE,
    'DIPP98741',
    'UDYAM-DL-01-0023412',
    '2023-03-15',
    18000000.00, -- 1.8 Crore INR
    25000000.00, -- 2.5 Crore INR
    'Defense, AI & Computer Vision',
    'Delhi',
    'New Delhi',
    'https://aerodef.in',
    'ai-surveillance;edge-computing;drone-telemetry;object-detection;iso-9001;embedded-linux;realtime-analytics'
) ON CONFLICT (user_id) DO NOTHING;

-- 4. Insert Startup Capabilities
INSERT INTO startup_capabilities (startup_id, category, name, proficiency_level, description) VALUES
(1, 'Artificial Intelligence', 'Edge AI Video Analytics & Object Detection', 'EXPERT', 'Low-latency YOLO & TensorRT models on NVIDIA Jetson embedded hardware'),
(1, 'Unmanned Systems', 'Autonomous Drone Navigation & Telemetry', 'ADVANCED', 'MAVLink & ROS2 based automated patrol flight controllers'),
(1, 'Software & Cloud', 'Real-time Command & Control Dashboard', 'EXPERT', 'High-throughput WebRTC video streaming and geospatial event tracking'),
(1, 'Embedded Hardware', 'Ruggedized Mil-Spec Edge Computing Unit', 'INTERMEDIATE', 'IP67 rated compute enclosures tested for extreme thermal envelopes');

-- 5. Insert Startup Certifications
INSERT INTO startup_certifications (startup_id, cert_type, cert_number, issuing_body, issue_date, expiry_date, document_url) VALUES
(1, 'ISO 9001:2015', 'ISO-QMS-2023-9912', 'Bureau Veritas India', '2023-06-01', '2026-05-31', '/uploads/cert_iso9001.pdf'),
(1, 'DPIIT Recognition Certificate', 'DIPP98741', 'Department for Promotion of Industry and Internal Trade', '2023-04-10', '2033-04-09', '/uploads/dpiit_cert.pdf'),
(1, 'Udyam Registration', 'UDYAM-DL-01-0023412', 'Ministry of Micro, Small and Medium Enterprises', '2023-04-15', '2035-04-15', '/uploads/udyam_cert.pdf');

-- 6. Insert Startup Documents
INSERT INTO startup_documents (startup_id, doc_type, file_name, file_path, file_size, mime_type, checksum) VALUES
(1, 'DPIIT_CERTIFICATE', 'DPIIT_Recognition_Certificate.pdf', '/uploads/DPIIT_Recognition_Certificate.pdf', 245120, 'application/pdf', 'a9b2c3d4e5f6'),
(1, 'GST_CERTIFICATE', 'GST_Registration_07AABCA1234F1Z5.pdf', '/uploads/GST_Registration.pdf', 312000, 'application/pdf', 'b8c7d6e5f4a3'),
(1, 'PAN_CARD', 'Company_PAN_AABCA1234F.pdf', '/uploads/Company_PAN.pdf', 189000, 'application/pdf', 'c7d6e5f4a3b2'),
(1, 'AUDITED_FINANCIALS', 'Audited_Balance_Sheet_FY2025.pdf', '/uploads/Audited_Balance_Sheet_FY2025.pdf', 1450000, 'application/pdf', 'd6e5f4a3b2c1'),
(1, 'ISO_9001', 'ISO_9001_Quality_Management.pdf', '/uploads/ISO_9001.pdf', 420000, 'application/pdf', 'e5f4a3b2c1d0');

-- 7. Insert Master Public Procurement Relaxation Rules
INSERT INTO eligibility_rules (id, rule_code, name, authority, description, source_url, effective_date, version, active) VALUES
(1, 'GFR-161-IV', 'Exemption from Prior Turnover and Prior Experience for Startups', 'Ministry of Finance / DoE', 'Rule 161(iv) of General Financial Rules (GFR) 2017 allows relaxation of prior turnover and prior experience criteria for recognized Startups, subject to meeting technical specifications and quality standards.', 'https://doe.gov.in/procurement-policy', '2017-03-08', 'v1.0', TRUE),
(2, 'DPIIT-OM-2016', 'Relaxation of Norms for Public Procurement for Startups', 'DPIIT, Ministry of Commerce & Industry', 'Office Memorandum No. 5(4)/2017-BE-I directing all Central Ministries/Departments/PSUs to relax condition of prior turnover and experience for DPIIT recognized startups.', 'https://www.startupindia.gov.in', '2016-03-10', 'v1.0', TRUE),
(3, 'PPP-MII-2017', 'Public Procurement (Preference to Make in India) Order', 'DPIIT', 'Preference to locally manufactured goods with local content thresholds (Class-I: >=50%, Class-II: >=20%).', 'https://dpiit.gov.in/public-procurement-order', '2017-06-15', 'v2.1', TRUE),
(4, 'MSE-EMD-EXEMPTION', 'EMD and Tender Fee Exemption for MSEs & Startups', 'Ministry of MSME / GeM Guidelines', 'Exemption from payment of Earnest Money Deposit (EMD) and tender document fees for all DPIIT recognized startups and registered MSEs.', 'https://gem.gov.in/help', '2018-01-01', 'v1.2', TRUE)
ON CONFLICT (rule_code) DO NOTHING;

-- 8. Insert SIH Demo Realistic Tenders
INSERT INTO tenders (id, tender_ref_no, title, department, authority, category, estimated_value_inr, emd_inr, published_date, closing_date, source_portal, source_url, status, document_url) VALUES
(
    1,
    'GEM/2026/B/892301',
    'Procurement of AI-Powered Edge Drone Surveillance & Intelligent Video Analytics System for Perimeter Security',
    'Ministry of Defence / Smart Cities Mission',
    'Directorate General of Border Intelligence & Urban Surveillance',
    'Defence & Surveillance AI',
    15000000.00, -- 1.50 Crore INR
    300000.00,   -- 3.00 Lakh EMD (Exempt for Startups)
    '2026-09-01 10:00:00',
    '2026-10-15 17:00:00',
    'GeM (Government e-Marketplace)',
    'https://gem.gov.in/tenders/gem-2026-b-892301',
    'PUBLISHED',
    '/documents/tender_gem_892301_surveillance.pdf'
),
(
    2,
    'AIIMS/PROC/2026/IOT-77',
    'Supply, Installation & Maintenance of Smart Hospital IoT Patient Telemetry & Continuous Vitals Monitoring Network',
    'All India Institute of Medical Sciences (AIIMS) New Delhi',
    'Department of Biomedical Engineering & Hospital Informatics',
    'Healthcare & Medical IoT',
    45000000.00, -- 4.50 Crore INR
    900000.00,   -- 9.00 Lakh EMD
    '2026-09-05 09:30:00',
    '2026-09-28 15:00:00',
    'CPPP (Central Public Procurement Portal)',
    'https://eprocure.gov.in/epublish/app?tenderId=AIIMS-IOT-77',
    'PUBLISHED',
    '/documents/tender_aiims_iot_77.pdf'
),
(
    3,
    'NHAI/TECH/2026/CIVIL-402',
    'Engineering, Procurement & Construction (EPC) of 4-Lane Highway Bypass and Grade Separators on NH-48 Corridor',
    'National Highways Authority of India (NHAI)',
    'Ministry of Road Transport and Highways',
    'Heavy Civil Infrastructure',
    750000000.00, -- 75 Crore INR
    15000000.00,  -- 1.50 Crore EMD
    '2026-08-20 11:00:00',
    '2026-10-30 18:00:00',
    'CPPP (eProcurement)',
    'https://eprocure.gov.in/nhai/civil-402',
    'PUBLISHED',
    '/documents/tender_nhai_civil_402.pdf'
) ON CONFLICT (tender_ref_no) DO NOTHING;

-- 9. Insert Extracted Tender Requirements for Tender 1 (High Match)
INSERT INTO tender_requirements (tender_id, type, requirement_text, normalized_value, unit, operator, mandatory, preferred, source_page, source_section, source_snippet, confidence, review_status) VALUES
(1, 'TURNOVER', 'The bidder must have an average annual turnover of at least ₹1.00 Crore over the last three financial years. (DPIIT recognized startups exempted as per GFR 161(iv))', '10000000', 'INR', 'GTE', TRUE, FALSE, 4, 'Clause 3.1 - Financial Eligibility', 'Average annual turnover shall not be less than INR 1.00 Cr in preceding three financial years.', 'HIGH', 'APPROVED'),
(1, 'EXPERIENCE', 'Bidder should have minimum 2 years of proven experience in deploying Computer Vision, AI Object Detection or Unmanned Aerial Systems.', '2', 'YEARS', 'GTE', TRUE, FALSE, 5, 'Clause 3.2 - Technical Experience', 'The bidder should possess minimum 2 years experience in Computer Vision or UAV surveillance systems.', 'HIGH', 'APPROVED'),
(1, 'CERTIFICATION', 'Bidder must possess a valid ISO 9001:2015 Quality Management System Certification at the time of bidding.', 'ISO 9001:2015', 'TEXT', 'EQUALS', TRUE, FALSE, 7, 'Clause 4.1 - Quality Certifications', 'Copy of valid ISO 9001:2015 certification from an accredited body is mandatory.', 'HIGH', 'APPROVED'),
(1, 'TECHNICAL', 'The edge AI unit must support real-time low-latency object detection (>25 FPS at 1080p) and automated geo-referenced target classification.', '25 FPS @ 1080p', 'TEXT', 'CONTAINS', TRUE, FALSE, 12, 'Clause 6.3 - Technical Specifications', 'Edge inference system shall achieve frame rates >= 25 FPS with multi-class vehicle and personnel recognition.', 'HIGH', 'APPROVED'),
(1, 'LEGAL', 'The bidder must be an Indian entity registered under Companies Act or LLP Act, with valid GSTIN and PAN.', 'GSTIN & PAN', 'TEXT', 'EQUALS', TRUE, FALSE, 3, 'Clause 2.1 - Statutory Registrations', 'Valid GST registration certificate and permanent account number (PAN) must be furnished.', 'HIGH', 'APPROVED'),
(1, 'DOCUMENT', 'DPIIT Startup Recognition Certificate or Udyam MSME Certificate for claim of EMD exemption and eligibility relaxation.', 'DPIIT / UDYAM', 'TEXT', 'EQUALS', FALSE, TRUE, 8, 'Clause 4.5 - Startup Benefits', 'Startups seeking exemption from prior turnover/experience must submit valid DPIIT certificate.', 'HIGH', 'APPROVED');

-- 10. Insert Document Chunks for Grounded RAG on Tender 1
INSERT INTO document_chunks (tender_id, page_number, section_title, chunk_index, content, token_count) VALUES
(1, 4, 'Clause 3.1 - Financial Eligibility', 1, 'Clause 3.1: Minimum Financial Eligibility. The bidder should have an average annual turnover of at least ₹1.00 Crore over the last three financial years. Relaxation Note: In accordance with Rule 161(iv) of General Financial Rules (GFR) 2017 and DPIIT notification No. 5(4)/2017-BE-I, recognized startups shall be exempted from the condition of prior turnover, provided they meet quality and technical specifications.', 72),
(1, 5, 'Clause 3.2 - Technical Experience', 2, 'Clause 3.2: Prior Experience Criteria. The bidder should possess a minimum of 2 years experience in the deployment of Computer Vision, Edge AI, or Unmanned Aerial Systems. For DPIIT recognized startups, prior experience criteria may be relaxed if the bidder demonstrates certified technical capability and satisfactory prototype test results as evaluated by the technical evaluation committee.', 68),
(1, 7, 'Clause 4.1 - Quality Certifications', 3, 'Clause 4.1: Quality Standards & Statutory Compliance. The bidder must possess a valid ISO 9001:2015 Quality Management System Certification. Startups claiming exemption from technical experience must hold ISO 9001 or equivalent CMMI Level 3 certification to establish quality management protocols.', 54),
(1, 8, 'Clause 4.5 - Startup Benefits & EMD Exemption', 4, 'Clause 4.5: Benefits for Startups & MSEs. As per Government of India public procurement policies, DPIIT recognized startups are 100% exempt from submitting Earnest Money Deposit (EMD) of ₹3,00,000. Startups must upload their valid DPIIT Certificate of Recognition on the portal at the time of online bid submission.', 58),
(1, 14, 'Clause 7.2 - Local Content & Delivery Schedule', 5, 'Clause 7.2: Make in India & Delivery. The procurement falls under Class-I Local Supplier category with minimum 50% local value addition. Complete supply and commissioning shall be completed within 90 days from the date of Award of Contract (AOC).', 49);

-- 11. Insert Pre-Calculated Match for Demo
INSERT INTO startup_tender_matches (id, startup_id, tender_id, overall_score, technical_score, sector_score, eligibility_score, readiness_score, fit_score, explanation_json) VALUES
(
    1,
    1,
    1,
    88,
    94,
    92,
    85,
    80,
    90,
    '{"summary": "Strong match (88%). Your registered capabilities in Edge AI Video Analytics, Drone Telemetry and ISO 9001:2015 directly satisfy all mandatory technical clauses. Financial turnover of ₹1.8 Cr exceeds ₹1.0 Cr threshold and DPIIT recognition confers full GFR 161(iv) exemption.", "strengths": ["Technical capability directly covers Edge AI and object detection requirements", "Valid ISO 9001:2015 certificate is active and verified", "Turnover of ₹1.8 Cr satisfies the ₹1.0 Cr criteria", "DPIIT recognized startup eligible for EMD exemption (₹3 Lakh saved)"], "gaps": ["Ensure prototype test report is uploaded to substantiate technical experience relaxation", "Submit Make-in-India Class-I 50% local content self-declaration"]}'
) ON CONFLICT (startup_id, tender_id) DO NOTHING;

-- 12. Insert Granular Eligibility Evaluations for Tender 1
INSERT INTO eligibility_evaluations (match_id, requirement_id, startup_value, tender_value, operator, result, reason, applied_relaxation_rule_id, source_page, source_section) VALUES
(1, 1, '₹1.80 Crore', '₹1.00 Crore', 'GTE', 'PASS', 'Startup turnover of ₹1.80 Cr satisfies the requirement. Furthermore, DPIIT recognized startup status confers GFR 161(iv) exemption protection.', 1, 4, 'Clause 3.1 - Financial Eligibility'),
(1, 2, '3 Years (Incorp 2023)', '2 Years', 'GTE', 'PASS', 'Startup has 3 years operational experience in Computer Vision & Autonomous Systems, meeting the minimum 2 years criteria.', 1, 5, 'Clause 3.2 - Technical Experience'),
(1, 3, 'ISO 9001:2015 (Valid till 2026)', 'ISO 9001:2015', 'EQUALS', 'PASS', 'Valid ISO 9001:2015 certificate issued by Bureau Veritas is active on file.', NULL, 7, 'Clause 4.1 - Quality Certifications'),
(1, 4, 'Edge AI Object Detection (EXPERT)', '25 FPS @ 1080p', 'CONTAINS', 'PASS', 'Registered expertise in YOLO/TensorRT edge processing satisfies technical speed and resolution parameters.', NULL, 12, 'Clause 6.3 - Technical Specifications'),
(1, 5, 'GSTIN & PAN (Verified)', 'GSTIN & PAN', 'EQUALS', 'PASS', 'GST registration and company PAN are uploaded and verified.', NULL, 3, 'Clause 2.1 - Statutory Registrations');

-- 13. Insert Compliance Checklist Items for Tender 1
INSERT INTO compliance_items (id, tender_id, startup_id, title, category, mandatory, status, tender_source_page, due_date, notes) VALUES
(1, 1, 1, 'Company PAN & GST Registration Certificate', 'Statutory Documents', TRUE, 'READY', 3, '2026-10-10', 'Both certificates verified in startup profile.'),
(2, 1, 1, 'DPIIT Recognition Certificate (for EMD & Turnover Relaxation)', 'Startup Credentials', TRUE, 'READY', 8, '2026-10-10', 'DIPP98741 active and valid until 2033.'),
(3, 1, 1, 'ISO 9001:2015 Quality Management Certificate', 'Quality & Standards', TRUE, 'READY', 7, '2026-10-10', 'Bureau Veritas certificate active till May 2026.'),
(4, 1, 1, 'Class-I Local Content (Make in India >=50%) Self-Declaration', 'Statutory Declarations', TRUE, 'MISSING', 14, '2026-10-12', 'Upload signed declaration on company letterhead confirming local BOM content.'),
(5, 1, 1, 'OEM / Prototype Technical Test Certificate', 'Technical Submission', TRUE, 'NEEDS_REVIEW', 5, '2026-10-12', 'Review edge compute thermal and benchmark test logs before final submission.');

-- Map Verified Evidence
INSERT INTO compliance_evidence (compliance_item_id, startup_document_id, verification_status, verified_by, verified_at) VALUES
(1, 2, 'VERIFIED', 'System Verification Engine', '2026-09-15 10:00:00'),
(2, 1, 'VERIFIED', 'DPIIT Portal Sync', '2026-09-15 10:00:00'),
(3, 5, 'VERIFIED', 'System Verification Engine', '2026-09-15 10:00:00');

-- 14. Insert Opportunity Tracker Record
INSERT INTO opportunities (id, startup_id, tender_id, stage, priority, readiness_percent, target_submission_date, notes) VALUES
(1, 1, 1, 'PREPARING', 'HIGH', 78, '2026-10-12', 'High-priority opportunity. Technical specifications fully aligned. Complete local content declaration and prototype test logs.')
ON CONFLICT (startup_id, tender_id) DO NOTHING;

-- Insert Opportunity Tasks
INSERT INTO opportunity_tasks (opportunity_id, title, description, due_date, completed, completed_at) VALUES
(1, 'Generate and sign Make-in-India 50% Local Content Undertaking', 'Download template and affix authorized signatory digital signature', '2026-10-05', FALSE, NULL),
(1, 'Consolidate Edge AI Jetson benchmark test logs for Technical Bid envelope', 'Format video FPS benchmarks into PDF technical compliance annexure', '2026-10-07', TRUE, '2026-09-14 16:30:00'),
(1, 'Verify EMD Exemption document tag on GeM submission portal', 'Select Startup exemption radio button on GeM portal and attach DPIIT certificate', '2026-10-10', FALSE, NULL);

-- 15. Insert Initial Notifications
INSERT INTO notifications (user_id, type, title, message, link, is_read) VALUES
(1, 'HIGH_MATCH_TENDER', 'High Fit Tender Discovered (88% Match)', 'AI-Powered Edge Drone Surveillance & Intelligent Video Analytics System has been identified as a high match for AeroDef AI.', '/tenders/1', FALSE),
(1, 'DEADLINE_ALERT', 'Tender Deadline in 30 Days', 'GEM/2026/B/892301 closing date is 15 Oct 2026. 2 compliance tasks remaining.', '/opportunities', FALSE);

-- 16. Insert Initial Audit Log Event
INSERT INTO audit_logs (actor_id, actor_email, action, entity_type, entity_id, ip_address, metadata_json) VALUES
(1, 'founder@aerodef.in', 'MATCH_EVALUATION_COMPLETED', 'Tender', '1', '127.0.0.1', '{"score": 88, "eligibility": "PASS", "rule": "GFR-161-IV"}');
