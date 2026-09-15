export interface User {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  roles: string[];
  startupId?: number;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  email: string;
  fullName: string;
  roles: string[];
  startupId?: number;
}

export interface Capability {
  id?: number;
  category: string;
  name: string;
  proficiencyLevel: string;
  description?: string;
}

export interface Certification {
  id?: number;
  certType: string;
  certNumber?: string;
  issuingBody?: string;
  issueDate?: string;
  expiryDate?: string;
  documentUrl?: string;
}

export interface StartupDocument {
  id: number;
  docType: string;
  fileName: string;
  filePath: string;
  fileSize?: number;
  mimeType?: string;
  uploadedAt?: string;
}

export interface StartupProfile {
  id?: number;
  userId?: number;
  companyName: string;
  legalName?: string;
  dpiitRecognized: boolean;
  dpiitNumber?: string;
  udyamNumber?: string;
  incorporationDate?: string;
  annualTurnoverInr: number;
  netWorthInr?: number;
  primarySector: string;
  state?: string;
  city?: string;
  website?: string;
  capabilityFingerprint?: string;
  capabilities: Capability[];
  certifications: Certification[];
  documents?: StartupDocument[];
}

export interface TenderSummary {
  id: number;
  tenderRefNo: string;
  title: string;
  department: string;
  authority?: string;
  category: string;
  estimatedValueInr?: number;
  emdInr?: number;
  publishedDate?: string;
  closingDate: string;
  sourcePortal: string;
  sourceUrl?: string;
  status: string;
  matchScore?: number;
  eligibilityStatus?: 'PASS' | 'FAIL' | 'NEEDS_REVIEW' | 'UNCHECKED';
  daysRemaining?: number;
}

export interface TenderSection {
  id: number;
  sectionCode?: string;
  title: string;
  pageStart?: number;
  pageEnd?: number;
  rawText?: string;
}

export interface TenderRequirement {
  id: number;
  tenderId: number;
  type: string;
  requirementText: string;
  normalizedValue?: string;
  unit?: string;
  operator: string;
  mandatory: boolean;
  preferred: boolean;
  sourcePage?: number;
  sourceSection?: string;
  sourceSnippet?: string;
  confidence: string;
  reviewStatus: string;
}

export interface TenderDetail extends TenderSummary {
  documentUrl?: string;
  sections: TenderSection[];
  requirements: TenderRequirement[];
}

export interface EligibilityEvaluationItem {
  id: number;
  requirementType: string;
  requirementText: string;
  startupValue: string;
  tenderValue: string;
  operator: string;
  result: 'PASS' | 'FAIL' | 'NEEDS_REVIEW' | 'NOT_APPLICABLE' | 'MISSING_DATA';
  reason: string;
  sourcePage?: number;
  sourceSection?: string;
  appliedRuleCode?: string;
}

export interface RuleCitation {
  ruleCode: string;
  name: string;
  authority: string;
  description: string;
  sourceUrl?: string;
  effectiveDate?: string;
  version?: string;
}

export interface EligibilitySummary {
  tenderId: number;
  startupId: number;
  overallMatchScore: number;
  overallStatus: 'PASS' | 'FAIL' | 'NEEDS_REVIEW';
  evaluations: EligibilityEvaluationItem[];
  appliedRelaxations: RuleCitation[];
}

export interface MatchDetails {
  tenderId: number;
  startupId: number;
  overallScore: number;
  technicalScore: number;
  sectorScore: number;
  eligibilityScore: number;
  readinessScore: number;
  fitScore: number;
  explanation: {
    summary: string;
    strengths?: string[];
    gaps?: string[];
  };
}

export interface ComplianceEvidence {
  id: number;
  documentId?: number;
  fileName: string;
  docType: string;
  filePath?: string;
  verificationStatus: string;
  verifiedBy?: string;
}

export interface ComplianceItem {
  id: number;
  tenderId: number;
  startupId: number;
  title: string;
  category?: string;
  mandatory: boolean;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'READY' | 'MISSING' | 'NEEDS_REVIEW' | 'VERIFIED';
  tenderSourcePage?: number;
  dueDate?: string;
  notes?: string;
  evidence: ComplianceEvidence[];
}

export interface OpportunityTask {
  id: number;
  opportunityId: number;
  title: string;
  description?: string;
  dueDate?: string;
  completed: boolean;
  completedAt?: string;
}

export interface Opportunity {
  id: number;
  startupId: number;
  tenderId: number;
  tender: TenderSummary;
  stage: 'INTERESTED' | 'PREPARING' | 'SUBMITTED' | 'EVALUATION' | 'OUTCOME';
  priority: string;
  readinessPercent: number;
  targetSubmissionDate?: string;
  notes?: string;
  updatedAt?: string;
  tasks: OpportunityTask[];
}

export interface Citation {
  page: number;
  section: string;
  snippet: string;
}

export interface QaMessage {
  id?: number;
  role: 'USER' | 'ASSISTANT';
  content: string;
  citations?: Citation[];
  createdAt?: string;
}

export interface NotificationItem {
  id: number;
  type: string;
  title: string;
  message: string;
  link?: string;
  isRead: boolean;
  createdAt: string;
}

export interface AuditLogItem {
  id: number;
  actorId?: number;
  actorEmail?: string;
  action: string;
  entityType: string;
  entityId?: string;
  ipAddress?: string;
  metadataJson?: string;
  createdAt: string;
}
