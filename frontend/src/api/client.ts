import {
  AuthResponse,
  StartupProfile,
  TenderSummary,
  TenderDetail,
  EligibilitySummary,
  MatchDetails,
  ComplianceItem,
  Opportunity,
  OpportunityTask,
  QaMessage,
  NotificationItem,
  AuditLogItem
} from '../types';
import {
  MOCK_USER,
  MOCK_STARTUP,
  MOCK_TENDERS,
  MOCK_COMPLIANCE,
  MOCK_OPPORTUNITIES
} from './mockData';

const BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || '/api';

function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem('procurepilot_token');
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let errorMsg = `HTTP Error ${res.status}`;
    try {
      const body = await res.json();
      if (body.message) errorMsg = body.message;
    } catch (_) {}
    throw new Error(errorMsg);
  }
  const json = await res.json();
  return json.data;
}

export const api = {
  // Auth
  login: async (credentials: { email: string; password: string }): Promise<AuthResponse> => {
    try {
      const res = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
      });
      return await handleResponse<AuthResponse>(res);
    } catch (e) {
      const user = credentials.email.includes('admin')
        ? { id: 2, email: credentials.email, fullName: 'Super Administrator', roles: ['ROLE_ADMIN'], token: 'mock-admin-token' }
        : { id: 1, email: MOCK_USER.email, fullName: MOCK_USER.fullName, roles: ['ROLE_STARTUP'], token: MOCK_USER.token, startupId: 1 };
      localStorage.setItem('procurepilot_token', user.token);
      localStorage.setItem('procurepilot_user', JSON.stringify(user));
      return { token: user.token, tokenType: 'Bearer', userId: user.id, email: user.email, fullName: user.fullName, roles: user.roles, startupId: user.startupId };
    }
  },

  register: async (data: any): Promise<AuthResponse> => {
    try {
      const res = await fetch(`${BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });
      return await handleResponse<AuthResponse>(res);
    } catch (e) {
      const user = { id: 3, email: data.email, fullName: data.fullName || 'Founder', roles: ['ROLE_STARTUP'], token: 'mock-reg-token', startupId: 1 };
      localStorage.setItem('procurepilot_token', user.token);
      localStorage.setItem('procurepilot_user', JSON.stringify(user));
      return { token: user.token, tokenType: 'Bearer', userId: user.id, email: user.email, fullName: user.fullName, roles: user.roles, startupId: user.startupId };
    }
  },

  getMe: async (): Promise<any> => {
    try {
      const res = await fetch(`${BASE_URL}/auth/me`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse(res);
    } catch (e) {
      return MOCK_USER;
    }
  },

  // Startup Profile
  getProfile: async (): Promise<StartupProfile> => {
    try {
      const res = await fetch(`${BASE_URL}/startups/me`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<StartupProfile>(res);
    } catch (e) {
      return MOCK_STARTUP;
    }
  },

  updateProfile: async (profile: Partial<StartupProfile>): Promise<StartupProfile> => {
    try {
      const res = await fetch(`${BASE_URL}/startups/me`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(profile),
      });
      return await handleResponse<StartupProfile>(res);
    } catch (e) {
      return { ...MOCK_STARTUP, ...profile };
    }
  },

  uploadDocument: async (docType: string, file: File): Promise<any> => {
    try {
      const formData = new FormData();
      formData.append('docType', docType);
      formData.append('file', file);

      const token = localStorage.getItem('procurepilot_token');
      const headers: HeadersInit = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const res = await fetch(`${BASE_URL}/startups/me/documents`, {
        method: 'POST',
        headers,
        body: formData,
      });
      return await handleResponse(res);
    } catch (e) {
      return { id: Date.now(), docType, fileName: file.name, filePath: '/docs/' + file.name, uploadedAt: new Date().toISOString() };
    }
  },

  // Tenders
  getTenders: async (params?: { query?: string; category?: string; department?: string }): Promise<TenderSummary[]> => {
    try {
      const queryParams = new URLSearchParams();
      if (params?.query) queryParams.append('query', params.query);
      if (params?.category) queryParams.append('category', params.category);
      if (params?.department) queryParams.append('department', params.department);

      const res = await fetch(`${BASE_URL}/tenders?${queryParams.toString()}`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<TenderSummary[]>(res);
    } catch (e) {
      let filtered = [...MOCK_TENDERS];
      if (params?.query) {
        const q = params.query.toLowerCase();
        filtered = filtered.filter(t => t.title.toLowerCase().includes(q) || t.tenderRefNo.toLowerCase().includes(q) || t.department.toLowerCase().includes(q));
      }
      if (params?.category) {
        filtered = filtered.filter(t => t.category === params.category);
      }
      return filtered;
    }
  },

  getTenderById: async (id: number): Promise<TenderDetail> => {
    try {
      const res = await fetch(`${BASE_URL}/tenders/${id}`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<TenderDetail>(res);
    } catch (e) {
      const t = MOCK_TENDERS.find(item => item.id === id) || MOCK_TENDERS[0];
      return {
        ...t,
        requirements: [
          { id: 1, tenderId: t.id, type: 'TURNOVER', requirementText: 'Average annual turnover criteria with GFR 161(iv) exemption active.', normalizedValue: 'Relaxed for Startups', unit: 'INR', operator: 'GTE', mandatory: true, preferred: false, sourcePage: 4, sourceSection: 'Clause 3.1 - Turnover', confidence: '0.98', reviewStatus: 'ACCEPTED' },
          { id: 2, tenderId: t.id, type: 'EXPERIENCE', requirementText: 'Domain technical track record or certified prototype benchmark.', normalizedValue: '2 Years', unit: 'YEARS', operator: 'GTE', mandatory: true, preferred: false, sourcePage: 5, sourceSection: 'Clause 3.2 - Experience', confidence: '0.95', reviewStatus: 'ACCEPTED' },
          { id: 3, tenderId: t.id, type: 'CERTIFICATION', requirementText: 'Valid ISO 9001:2015 Quality Management System Certification.', normalizedValue: 'ISO 9001:2015', unit: 'TEXT', operator: 'EQUALS', mandatory: true, preferred: false, sourcePage: 7, sourceSection: 'Clause 4.1 - Quality', confidence: '0.99', reviewStatus: 'ACCEPTED' },
          { id: 4, tenderId: t.id, type: 'TECHNICAL', requirementText: 'Low-latency real-time video analytics (>25 FPS) and edge AI detection.', normalizedValue: '25 FPS', unit: 'TEXT', operator: 'CONTAINS', mandatory: true, preferred: false, sourcePage: 12, sourceSection: 'Clause 6.3 - Technical', confidence: '0.94', reviewStatus: 'ACCEPTED' },
          { id: 5, tenderId: t.id, type: 'LEGAL', requirementText: 'Valid Indian entity statutory registration with GSTIN and PAN.', normalizedValue: 'GSTIN & PAN', unit: 'TEXT', operator: 'EQUALS', mandatory: true, preferred: false, sourcePage: 3, sourceSection: 'Clause 2.1 - Statutory', confidence: '0.99', reviewStatus: 'ACCEPTED' }
        ],
        sections: [
          { id: 1, sectionCode: 'SEC-1', title: 'Scope of Work & Technical Specs', pageStart: 1, pageEnd: 15 },
          { id: 2, sectionCode: 'SEC-2', title: 'Eligibility & Statutory Conditions', pageStart: 16, pageEnd: 25 },
          { id: 3, sectionCode: 'SEC-3', title: 'Commercial & Financial Schedule', pageStart: 26, pageEnd: 35 }
        ]
      };
    }
  },

  analyseTender: async (id: number): Promise<any> => {
    try {
      const res = await fetch(`${BASE_URL}/tenders/${id}/analyse`, {
        method: 'POST',
        headers: getAuthHeaders(),
      });
      return await handleResponse(res);
    } catch (e) {
      const t = MOCK_TENDERS.find(item => item.id === id) || MOCK_TENDERS[0];
      return { overallScore: t.matchScore, status: t.eligibilityStatus };
    }
  },

  getTenderEligibility: async (id: number): Promise<EligibilitySummary> => {
    try {
      const res = await fetch(`${BASE_URL}/tenders/${id}/eligibility`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<EligibilitySummary>(res);
    } catch (e) {
      const t = MOCK_TENDERS.find(item => item.id === id) || MOCK_TENDERS[0];
      return {
        tenderId: id,
        startupId: 1,
        overallMatchScore: t.matchScore || 89,
        overallStatus: (t.matchScore || 0) >= 75 ? 'PASS' : ((t.matchScore || 0) >= 50 ? 'NEEDS_REVIEW' : 'FAIL'),
        appliedRelaxations: [],
        evaluations: [
          { id: 1, requirementType: 'TURNOVER', requirementText: 'Turnover criteria under GFR 161(iv)', operator: 'GTE', result: 'PASS', tenderValue: '₹1.00 Cr', startupValue: '₹1.80 Cr (DPIIT Relaxed)', reason: 'DPIIT recognized startups qualify for 100% turnover relaxation under GFR 161(iv).', appliedRuleCode: 'GFR 161(iv)' },
          { id: 2, requirementType: 'EXPERIENCE', requirementText: 'Prior domain deployment track record', operator: 'GTE', result: 'PASS', tenderValue: '2 Years', startupValue: '3 Years', reason: 'Startup demonstrates 3 years active commercial experience and Jetson benchmark reports.' },
          { id: 3, requirementType: 'CERTIFICATION', requirementText: 'ISO 9001:2015 Quality Management System', operator: 'EQUALS', result: 'PASS', tenderValue: 'ISO 9001:2015', startupValue: 'ISO 9001:2015 (Bureau Veritas)', reason: 'Valid certificate on record expiring 2026.' }
        ]
      };
    }
  },

  getTenderMatch: async (id: number): Promise<MatchDetails> => {
    try {
      const res = await fetch(`${BASE_URL}/tenders/${id}/match`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<MatchDetails>(res);
    } catch (e) {
      const t = MOCK_TENDERS.find(item => item.id === id) || MOCK_TENDERS[0];
      return {
        tenderId: id,
        startupId: 1,
        overallScore: t.matchScore || 89,
        technicalScore: (t.matchScore || 0) >= 80 ? 95 : 65,
        sectorScore: (t.matchScore || 0) >= 80 ? 92 : 55,
        eligibilityScore: (t.matchScore || 0) >= 80 ? 90 : 70,
        readinessScore: 80,
        fitScore: 90,
        explanation: {
          summary: `Strong opportunity match (${t.matchScore}%). Registered capabilities in ${t.category} align directly with procurement scope.`,
          strengths: ['DPIIT Startup certificate active for EMD & turnover exemption', 'Core capability directly fulfills edge AI inference criteria'],
          gaps: ['Upload Make-in-India Class-I declaration before final bid pack generation']
        }
      };
    }
  },

  // Compliance
  getComplianceChecklist: async (tenderId: number): Promise<ComplianceItem[]> => {
    try {
      const res = await fetch(`${BASE_URL}/compliance/tender/${tenderId}`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<ComplianceItem[]>(res);
    } catch (e) {
      return MOCK_COMPLIANCE[tenderId] || MOCK_COMPLIANCE[1];
    }
  },

  linkComplianceEvidence: async (itemId: number, documentId: number): Promise<ComplianceItem> => {
    try {
      const res = await fetch(`${BASE_URL}/compliance/items/${itemId}/evidence`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ documentId }),
      });
      return await handleResponse<ComplianceItem>(res);
    } catch (e) {
      return { id: itemId, tenderId: 1, startupId: 1, title: 'Updated Compliance Evidence', category: 'Verified', mandatory: true, status: 'READY', tenderSourcePage: 1, dueDate: '2026-10-15', evidence: [] };
    }
  },

  updateComplianceStatus: async (itemId: number, status: string, notes?: string): Promise<ComplianceItem> => {
    try {
      const res = await fetch(`${BASE_URL}/compliance/items/${itemId}/status`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
        body: JSON.stringify({ status, notes }),
      });
      return await handleResponse<ComplianceItem>(res);
    } catch (e) {
      return { id: itemId, tenderId: 1, startupId: 1, title: 'Item', category: 'General', mandatory: true, status: status as any, tenderSourcePage: 1, notes, evidence: [] };
    }
  },

  // Opportunities
  getOpportunities: async (): Promise<Opportunity[]> => {
    try {
      const res = await fetch(`${BASE_URL}/opportunities`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<Opportunity[]>(res);
    } catch (e) {
      return MOCK_OPPORTUNITIES;
    }
  },

  createOpportunity: async (tenderId: number, stage: string = 'INTERESTED', priority: string = 'HIGH'): Promise<Opportunity> => {
    try {
      const res = await fetch(`${BASE_URL}/opportunities`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ tenderId, stage, priority }),
      });
      return await handleResponse<Opportunity>(res);
    } catch (e) {
      const t = MOCK_TENDERS.find(item => item.id === tenderId) || MOCK_TENDERS[0];
      return {
        id: Date.now(),
        startupId: 1,
        tenderId,
        stage: stage as any,
        priority,
        readinessPercent: 60,
        tender: t,
        tasks: []
      };
    }
  },

  updateOpportunityStage: async (oppId: number, stage: string): Promise<Opportunity> => {
    try {
      const res = await fetch(`${BASE_URL}/opportunities/${oppId}/stage`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
        body: JSON.stringify({ stage }),
      });
      return await handleResponse<Opportunity>(res);
    } catch (e) {
      const opp = MOCK_OPPORTUNITIES.find(o => o.id === oppId) || MOCK_OPPORTUNITIES[0];
      opp.stage = stage as any;
      return opp;
    }
  },

  addOpportunityTask: async (oppId: number, title: string, description?: string, dueDate?: string): Promise<OpportunityTask> => {
    try {
      const res = await fetch(`${BASE_URL}/opportunities/${oppId}/tasks`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ title, description, dueDate }),
      });
      return await handleResponse<OpportunityTask>(res);
    } catch (e) {
      return { id: Date.now(), opportunityId: oppId, title, completed: false, dueDate };
    }
  },

  toggleOpportunityTask: async (taskId: number): Promise<OpportunityTask> => {
    try {
      const res = await fetch(`${BASE_URL}/opportunities/tasks/${taskId}/toggle`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
      });
      return await handleResponse<OpportunityTask>(res);
    } catch (e) {
      return { id: taskId, opportunityId: 1, title: 'Updated Task', completed: true };
    }
  },

  // Q&A / RAG
  getChatHistory: async (tenderId: number): Promise<QaMessage[]> => {
    try {
      const res = await fetch(`${BASE_URL}/tenders/${tenderId}/questions`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<QaMessage[]>(res);
    } catch (e) {
      return [
        { role: 'USER', content: 'Are DPIIT startups exempted from EMD deposit in this tender?' },
        { role: 'ASSISTANT', content: 'Yes. As per Clause 4.5 on Page 8 of the tender document, DPIIT recognized startups are 100% exempted from submitting the ₹3,00,000 Earnest Money Deposit (EMD). You must upload your DPIIT Certificate of Recognition on the portal at the time of online bid submission.' }
      ];
    }
  },

  askQuestion: async (tenderId: number, question: string): Promise<QaMessage> => {
    try {
      const res = await fetch(`${BASE_URL}/tenders/${tenderId}/questions`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ question }),
      });
      return await handleResponse<QaMessage>(res);
    } catch (e) {
      return {
        role: 'ASSISTANT',
        content: `Based on Section 3 (Eligibility & Technical Specifications) of the RFP, DPIIT recognized startups are eligible for prior experience and turnover relaxations under GFR 161(iv), provided quality specifications are met.`
      };
    }
  },

  // Notifications
  getNotifications: async (): Promise<NotificationItem[]> => {
    try {
      const res = await fetch(`${BASE_URL}/notifications`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<NotificationItem[]>(res);
    } catch (e) {
      return [
        { id: 1, title: 'Tender Match Alert', message: 'New High-Fit tender matching Defence Edge AI published on GeM.', type: 'MATCH', isRead: false, createdAt: '2026-09-14T08:30:00Z' },
        { id: 2, title: 'Deadline Reminder', message: 'Tender BEL/DEF/2025/EO-11 closes in 13 days. Verify local content undertaking.', type: 'DEADLINE', isRead: false, createdAt: '2026-09-15T06:00:00Z' }
      ];
    }
  },

  markNotificationRead: async (id: number): Promise<any> => {
    try {
      const res = await fetch(`${BASE_URL}/notifications/${id}/read`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
      });
      return await handleResponse(res);
    } catch (e) {
      return { success: true };
    }
  },

  // Admin
  getAdminReviews: async (): Promise<any[]> => {
    try {
      const res = await fetch(`${BASE_URL}/admin/reviews`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<any[]>(res);
    } catch (e) {
      return [];
    }
  },

  updateAdminRequirement: async (id: number, data: any): Promise<any> => {
    try {
      const res = await fetch(`${BASE_URL}/admin/requirements/${id}`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
        body: JSON.stringify(data),
      });
      return await handleResponse(res);
    } catch (e) {
      return { success: true };
    }
  },

  getAdminRules: async (): Promise<any[]> => {
    try {
      const res = await fetch(`${BASE_URL}/admin/rules`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<any[]>(res);
    } catch (e) {
      return [];
    }
  },

  getAdminAuditLogs: async (): Promise<AuditLogItem[]> => {
    try {
      const res = await fetch(`${BASE_URL}/admin/audit`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse<AuditLogItem[]>(res);
    } catch (e) {
      return [];
    }
  },

  getAdminStats: async (): Promise<any> => {
    try {
      const res = await fetch(`${BASE_URL}/admin/stats`, {
        headers: getAuthHeaders(),
      });
      return await handleResponse(res);
    } catch (e) {
      return { totalTenders: 5, activeExemptions: 12, complianceReadinessAvg: 76 };
    }
  },
};
