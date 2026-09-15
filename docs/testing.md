# ProcurePilot Testing Strategy

## 1. Test Levels
- **Deterministic Unit Tests (Backend)**:
  - `EligibilityEngineTest`: Exact boundary conditions, exceeding threshold, below threshold, missing startup data, mandatory vs preferred, DPIIT startup exemptions (GFR 161(iv)), ambiguous clauses flagging `NEEDS_REVIEW`.
  - `MatchingEngineTest`: 40/25/20/10/5 weighted calculation, strength and gap rationale generation.
  - `AuthSecurityTest`: Token validity, expired token rejection, tenant isolation.
- **AI & RAG Tests (AI Service)**:
  - Grounded answer extraction tests.
  - Absence test (refusal on irrelevant or unmentioned requirements).
  - Citation validation tests.
- **Frontend Verification**:
  - TypeScript compilation check.
  - Form validation, state transition, and responsive rendering.
- **End-to-End Integration Flow**:
  - Onboarding -> Tender Search -> Tender Analysis -> Eligibility Breakdown -> Compliance Upload -> Grounded Q&A -> Kanban Progression.
