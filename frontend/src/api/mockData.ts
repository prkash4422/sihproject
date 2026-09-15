import {
  StartupProfile,
  TenderSummary,
  ComplianceItem,
  Opportunity
} from '../types';

export const MOCK_USER = {
  id: 1,
  email: 'founder@aerodef.in',
  fullName: 'Vikramaditya Sharma',
  role: 'STARTUP_FOUNDER',
  token: 'mock-jwt-token-aerodef-2026'
};

export const MOCK_STARTUP: StartupProfile = {
  id: 1,
  companyName: 'AeroDef Cognitive Systems Private Limited',
  dpiitRecognized: true,
  dpiitNumber: 'DIPP98741',
  incorporationDate: '2022-04-14',
  annualTurnoverInr: 18000000,
  primarySector: 'Defence Electronics & Aerospace AI',
  capabilities: [
    { id: 1, category: 'Artificial Intelligence', name: 'Real-time Edge Video Analytics & Object Detection (25 FPS)', proficiencyLevel: 'EXPERT', description: 'Deep neural networks optimized for NVIDIA Jetson Xavier / Orin edge platforms.' },
    { id: 2, category: 'Embedded Systems', name: 'MIL-STD-810G Rugged Hardware Integration', proficiencyLevel: 'ADVANCED', description: 'Thermal, vibration and shock rated enclosure engineering for combat vehicle telemetry.' },
    { id: 3, category: 'Medical & IoT', name: 'HL7 / FHIR Gateway Protocol Telemetry', proficiencyLevel: 'INTERMEDIATE', description: 'Biomedical sensor streaming and alert dispatch.' }
  ],
  certifications: [
    { id: 1, certType: 'ISO 9001:2015', certNumber: 'ISO-9001-QMS-2023-8812', issuingBody: 'Bureau Veritas India', issueDate: '2023-01-15', expiryDate: '2026-01-14' },
    { id: 2, certType: 'DPIIT Startup Certificate', certNumber: 'DIPP98741', issuingBody: 'Department for Promotion of Industry and Internal Trade', issueDate: '2022-06-20', expiryDate: '2032-06-19' }
  ],
  documents: [
    { id: 1, docType: 'DPIIT_CERTIFICATE', fileName: 'DPIIT_Recognition_Certificate_AeroDef.pdf', filePath: '/docs/dpiit.pdf', uploadedAt: '2026-01-10T10:00:00Z' },
    { id: 2, docType: 'ISO_9001', fileName: 'ISO_9001_2015_Certificate_BureauVeritas.pdf', filePath: '/docs/iso9001.pdf', uploadedAt: '2026-01-12T11:00:00Z' },
    { id: 3, docType: 'PAN_CARD', fileName: 'Company_PAN_AeroDef.pdf', filePath: '/docs/pan.pdf', uploadedAt: '2026-01-08T09:00:00Z' },
    { id: 4, docType: 'GST_CERTIFICATE', fileName: 'GSTIN_Registration_Certificate.pdf', filePath: '/docs/gst.pdf', uploadedAt: '2026-01-08T09:30:00Z' }
  ]
};

export const MOCK_TENDERS: TenderSummary[] = [
  {
    id: 1,
    tenderRefNo: 'GEM/2026/B/892301',
    title: 'Procurement of AI-Powered Edge Drone Surveillance & Intelligent Video Analytics System for Perimeter Security',
    department: 'Ministry of Defence / Smart Cities Mission',
    authority: 'Directorate General of Border Security Operations',
    category: 'Defence & Surveillance AI',
    estimatedValueInr: 28500000,
    emdInr: 300000,
    publishedDate: '2026-09-01T10:00:00Z',
    closingDate: '2026-10-15T17:00:00Z',
    sourcePortal: 'GeM (Government e-Marketplace)',
    sourceUrl: 'https://gem.gov.in',
    status: 'ACTIVE',
    daysRemaining: 30,
    matchScore: 89,
    eligibilityStatus: 'PASS'
  },
  {
    id: 5,
    tenderRefNo: 'BEL/DEF/2025/EO-11',
    title: 'Supply of Ruggedized Edge Video Analytics Embedded Units for Armoured Combat Vehicles',
    department: 'Bharat Electronics Limited (BEL)',
    authority: 'Military Communications & Avionics Strategic Business Unit',
    category: 'Defence Electronics & Rugged AI',
    estimatedValueInr: 35000000,
    emdInr: 700000,
    publishedDate: '2026-08-20T10:00:00Z',
    closingDate: '2026-09-28T17:00:00Z',
    sourcePortal: 'CPPP (eProcurement)',
    sourceUrl: 'https://eprocure.gov.in',
    status: 'ACTIVE',
    daysRemaining: 13,
    matchScore: 85,
    eligibilityStatus: 'PASS'
  },
  {
    id: 4,
    tenderRefNo: 'ISRO/SAT/2026/099',
    title: 'Development & Deployment of Autonomous Satellite Ground Station Telemetry AI Controller',
    department: 'Indian Space Research Organisation (ISRO)',
    authority: 'ISTRAC Telemetry Network Command',
    category: 'Space & Embedded Telemetry',
    estimatedValueInr: 18000000,
    emdInr: 360000,
    publishedDate: '2026-09-05T10:00:00Z',
    closingDate: '2026-10-05T17:00:00Z',
    sourcePortal: 'ISRO e-Procurement Portal',
    sourceUrl: 'https://eproc.isro.gov.in',
    status: 'ACTIVE',
    daysRemaining: 20,
    matchScore: 76,
    eligibilityStatus: 'PASS'
  },
  {
    id: 2,
    tenderRefNo: 'AIIMS/PROC/2026/IOT-77',
    title: 'Supply, Installation & Maintenance of Smart Hospital IoT Patient Telemetry & Continuous Vitals Monitoring Network',
    department: 'All India Institute of Medical Sciences (AIIMS) New Delhi',
    authority: 'Biomedical Engineering Procurement Cell',
    category: 'Healthcare & Medical IoT',
    estimatedValueInr: 45000000,
    emdInr: 900000,
    publishedDate: '2026-08-25T10:00:00Z',
    closingDate: '2026-09-30T17:00:00Z',
    sourcePortal: 'CPPP (Central Public Procurement Portal)',
    sourceUrl: 'https://eprocure.gov.in',
    status: 'ACTIVE',
    daysRemaining: 15,
    matchScore: 68,
    eligibilityStatus: 'NEEDS_REVIEW'
  },
  {
    id: 3,
    tenderRefNo: 'NHAI/TECH/2026/CIVIL-402',
    title: 'Engineering, Procurement & Construction (EPC) of 4-Lane Highway Bypass and Grade Separators on NH-48 Corridor',
    department: 'National Highways Authority of India (NHAI)',
    authority: 'Ministry of Road Transport and Highways (MoRTH)',
    category: 'Heavy Civil Infrastructure',
    estimatedValueInr: 1250000000,
    emdInr: 15000000,
    publishedDate: '2026-08-10T10:00:00Z',
    closingDate: '2026-09-20T17:00:00Z',
    sourcePortal: 'CPPP (MoRTH e-Tender)',
    sourceUrl: 'https://etenders.gov.in',
    status: 'ACTIVE',
    daysRemaining: 5,
    matchScore: 58,
    eligibilityStatus: 'NEEDS_REVIEW'
  }
];

export const MOCK_COMPLIANCE: Record<number, ComplianceItem[]> = {
  1: [
    { id: 101, tenderId: 1, startupId: 1, title: 'Company PAN & GST Registration Certificate', category: 'Statutory Documents', mandatory: true, status: 'READY', tenderSourcePage: 3, dueDate: '2026-10-10', evidence: [] },
    { id: 102, tenderId: 1, startupId: 1, title: 'DPIIT Recognition Certificate (for EMD & Turnover Relaxation)', category: 'Startup Credentials', mandatory: true, status: 'READY', tenderSourcePage: 8, dueDate: '2026-10-10', evidence: [] },
    { id: 103, tenderId: 1, startupId: 1, title: 'ISO 9001:2015 Quality Management Certificate', category: 'Quality & Standards', mandatory: true, status: 'READY', tenderSourcePage: 7, dueDate: '2026-10-10', evidence: [] },
    { id: 104, tenderId: 1, startupId: 1, title: 'Class-I Local Content (Make in India >=50%) Self-Declaration', category: 'Statutory Declarations', mandatory: true, status: 'MISSING', tenderSourcePage: 14, dueDate: '2026-10-12', evidence: [] },
    { id: 105, tenderId: 1, startupId: 1, title: 'OEM / Prototype Technical Test Certificate', category: 'Technical Submission', mandatory: true, status: 'NEEDS_REVIEW', tenderSourcePage: 5, dueDate: '2026-10-12', evidence: [] }
  ],
  2: [
    { id: 201, tenderId: 2, startupId: 1, title: 'Company PAN & GST Registration Certificate', category: 'Statutory Documents', mandatory: true, status: 'READY', tenderSourcePage: 2, dueDate: '2026-09-25', evidence: [] },
    { id: 202, tenderId: 2, startupId: 1, title: 'DPIIT Recognition Certificate (for EMD Exemption)', category: 'Startup Credentials', mandatory: true, status: 'READY', tenderSourcePage: 8, dueDate: '2026-09-25', evidence: [] },
    { id: 203, tenderId: 2, startupId: 1, title: 'ISO 9001:2015 Quality Management Certificate', category: 'Quality & Standards', mandatory: true, status: 'READY', tenderSourcePage: 6, dueDate: '2026-09-25', evidence: [] },
    { id: 204, tenderId: 2, startupId: 1, title: 'HL7 / FHIR Gateway Integration Architecture Document', category: 'Technical Submission', mandatory: true, status: 'READY', tenderSourcePage: 9, dueDate: '2026-09-27', evidence: [] },
    { id: 205, tenderId: 2, startupId: 1, title: 'Medical Device Safety & Telemetry Test Report', category: 'Quality & Compliance', mandatory: true, status: 'MISSING', tenderSourcePage: 6, dueDate: '2026-09-27', evidence: [] }
  ],
  3: [
    { id: 301, tenderId: 3, startupId: 1, title: 'Company PAN & GST Registration Certificate', category: 'Statutory Documents', mandatory: true, status: 'READY', tenderSourcePage: 3, dueDate: '2026-09-18', evidence: [] },
    { id: 302, tenderId: 3, startupId: 1, title: 'DPIIT Recognition Certificate', category: 'Startup Credentials', mandatory: true, status: 'READY', tenderSourcePage: 11, dueDate: '2026-09-18', evidence: [] },
    { id: 303, tenderId: 3, startupId: 1, title: 'MoRTH Grade-1 Highway Contractor License', category: 'Statutory Licensing', mandatory: true, status: 'MISSING', tenderSourcePage: 8, dueDate: '2026-09-18', evidence: [] },
    { id: 304, tenderId: 3, startupId: 1, title: 'Hot Mix Plant (400 TPH) Ownership / Lease Agreement', category: 'Machinery & Equipment', mandatory: true, status: 'MISSING', tenderSourcePage: 14, dueDate: '2026-09-18', evidence: [] },
    { id: 305, tenderId: 3, startupId: 1, title: 'CA Net Worth Certificate (> ₹15.00 Cr)', category: 'Financial Submission', mandatory: true, status: 'MISSING', tenderSourcePage: 5, dueDate: '2026-09-18', evidence: [] }
  ],
  4: [
    { id: 401, tenderId: 4, startupId: 1, title: 'Company PAN & GST Registration Certificate', category: 'Statutory Documents', mandatory: true, status: 'READY', tenderSourcePage: 2, dueDate: '2026-10-01', evidence: [] },
    { id: 402, tenderId: 4, startupId: 1, title: 'DPIIT Recognition Certificate', category: 'Startup Credentials', mandatory: true, status: 'READY', tenderSourcePage: 3, dueDate: '2026-10-01', evidence: [] },
    { id: 403, tenderId: 4, startupId: 1, title: 'ISO 9001:2015 Certificate', category: 'Quality & Standards', mandatory: true, status: 'READY', tenderSourcePage: 5, dueDate: '2026-10-01', evidence: [] },
    { id: 404, tenderId: 4, startupId: 1, title: 'High-Reliability Embedded Software Architecture Plan', category: 'Technical Bid', mandatory: true, status: 'READY', tenderSourcePage: 8, dueDate: '2026-10-01', evidence: [] }
  ],
  5: [
    { id: 501, tenderId: 5, startupId: 1, title: 'Company PAN & GST Registration Certificate', category: 'Statutory Documents', mandatory: true, status: 'READY', tenderSourcePage: 2, dueDate: '2026-09-24', evidence: [] },
    { id: 502, tenderId: 5, startupId: 1, title: 'DPIIT Recognition Certificate', category: 'Startup Credentials', mandatory: true, status: 'READY', tenderSourcePage: 3, dueDate: '2026-09-24', evidence: [] },
    { id: 503, tenderId: 5, startupId: 1, title: 'Make in India Class-I Supplier Declaration', category: 'Statutory Declarations', mandatory: true, status: 'READY', tenderSourcePage: 2, dueDate: '2026-09-24', evidence: [] },
    { id: 504, tenderId: 5, startupId: 1, title: 'MIL-STD Environmental Test Compliance Certificate', category: 'Technical Envelope', mandatory: true, status: 'READY', tenderSourcePage: 10, dueDate: '2026-09-24', evidence: [] }
  ]
};

export const MOCK_OPPORTUNITIES: Opportunity[] = [
  {
    id: 1,
    startupId: 1,
    tenderId: 2,
    stage: 'INTERESTED',
    priority: 'MEDIUM',
    readinessPercent: 80,
    targetSubmissionDate: '2026-09-27',
    notes: 'Healthcare IoT opportunity. GFR 161(iv) turnover relaxation applicable.',
    tender: MOCK_TENDERS[3],
    tasks: [
      { id: 1, opportunityId: 1, title: 'Review AIIMS biomedical telemetry interoperability specifications', completed: true },
      { id: 2, opportunityId: 1, title: 'Verify HL7 / FHIR data gateway cloud compatibility', completed: true }
    ]
  },
  {
    id: 2,
    startupId: 1,
    tenderId: 1,
    stage: 'PREPARING',
    priority: 'HIGH',
    readinessPercent: 60,
    targetSubmissionDate: '2026-10-05',
    notes: 'High-fit opportunity. Technical specifications directly aligned with Edge AI stack.',
    tender: MOCK_TENDERS[0],
    tasks: [
      { id: 3, opportunityId: 2, title: 'Generate and sign Make-in-India 50% Local Content Undertaking', completed: false },
      { id: 4, opportunityId: 2, title: 'Consolidate Edge AI Jetson benchmark test logs for Technical Bid envelope', completed: true },
      { id: 5, opportunityId: 2, title: 'Verify EMD Exemption document tag on GeM submission portal', completed: false }
    ]
  },
  {
    id: 3,
    startupId: 1,
    tenderId: 4,
    stage: 'SUBMITTED',
    priority: 'HIGH',
    readinessPercent: 100,
    targetSubmissionDate: '2026-09-20',
    notes: 'Technical and financial bid envelopes submitted on GeM portal. Awaiting opening.',
    tender: MOCK_TENDERS[2],
    tasks: [
      { id: 6, opportunityId: 3, title: 'Bid acknowledgment receipt downloaded and archived', completed: true },
      { id: 7, opportunityId: 3, title: 'Prepare technical presentation for ISTRAC evaluation committee', completed: true }
    ]
  },
  {
    id: 4,
    startupId: 1,
    tenderId: 3,
    stage: 'EVALUATION',
    priority: 'LOW',
    readinessPercent: 40,
    targetSubmissionDate: '2026-09-13',
    notes: 'Heavy civil construction tender. Technical bid under committee scrutiny.',
    tender: MOCK_TENDERS[4],
    tasks: [
      { id: 8, opportunityId: 4, title: 'Respond to NHAI pre-qualification technical query on JV consortia', completed: false }
    ]
  },
  {
    id: 5,
    startupId: 1,
    tenderId: 5,
    stage: 'OUTCOME',
    priority: 'HIGH',
    readinessPercent: 100,
    targetSubmissionDate: '2026-09-05',
    notes: 'Award of Contract (AOC) issued! L1 Winner for Edge Video Processing Modules.',
    tender: MOCK_TENDERS[1],
    tasks: [
      { id: 9, opportunityId: 5, title: 'Sign Master Supply Agreement with BEL Bangalore Division', completed: true },
      { id: 10, opportunityId: 5, title: 'Submit Performance Security Guarantee (3% of contract value)', completed: true }
    ]
  }
];
