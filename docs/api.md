# ProcurePilot REST API Specification

All endpoints return standard enveloped responses:
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully",
  "timestamp": "2026-09-15T12:00:00Z"
}
```

## 1. Authentication (`/api/auth`)
- `POST /api/auth/register`: Register new startup or user account.
- `POST /api/auth/login`: Authenticate and receive JWT access token.
- `GET /api/auth/me`: Current authenticated user and assigned roles.

## 2. Startup Profile & Documents (`/api/startups`)
- `GET /api/startups/me`: Retrieve startup profile and capability fingerprint.
- `PUT /api/startups/me`: Update multi-step profile data.
- `POST /api/startups/me/documents`: Upload evidence files (PAN, GST, DPIIT certificate).
- `GET /api/startups/me/documents`: List uploaded evidence with verification states.

## 3. Tenders & Discovery (`/api/tenders`)
- `GET /api/tenders`: List and search tenders with faceted filters (category, department, value, closing date).
- `GET /api/tenders/{id}`: Detailed tender overview, dates, requirements, and attached document metadata.
- `POST /api/tenders/{id}/analyse`: Trigger asynchronous requirement extraction, eligibility evaluation, and match calculation.
- `GET /api/tenders/{id}/analysis-status`: Poll processing status of the analysis job.
- `GET /api/tenders/{id}/requirements`: List structured requirements with confidence, operator, and source page.
- `GET /api/tenders/{id}/eligibility`: Retrieve deterministic evaluation breakdown (`PASS`, `FAIL`, `NEEDS_REVIEW`).
- `GET /api/tenders/{id}/match`: Retrieve transparent 5-factor match score and explainability narrative.
- `POST /api/tenders/{id}/questions`: Submit question to grounded RAG engine and receive answer with page/section citations.

## 4. Compliance & Evidence Workspace (`/api/compliance`)
- `GET /api/compliance/tender/{tenderId}`: Retrieve dynamic compliance checklist.
- `POST /api/compliance/items/{itemId}/evidence`: Link startup document as compliance evidence.
- `PATCH /api/compliance/items/{itemId}/status`: Update compliance item status.

## 5. Opportunity Tracker (`/api/opportunities`)
- `GET /api/opportunities`: List tracked opportunities across Kanban stages.
- `POST /api/opportunities`: Add tender to opportunity pipeline.
- `PATCH /api/opportunities/{id}/stage`: Transition opportunity stage (`INTERESTED` -> `PREPARING` -> `SUBMITTED`).
- `POST /api/opportunities/{id}/tasks`: Add actionable preparation task.

## 6. Admin & Governance (`/api/admin`)
- `GET /api/admin/reviews`: Review queue for low-confidence extractions and ambiguous requirements.
- `PATCH /api/admin/requirements/{id}`: Override/correct extracted requirement.
- `GET /api/admin/rules`: List versioned public procurement relaxation rules.
- `POST /api/admin/rules`: Create or update policy relaxation rule.
- `GET /api/admin/audit`: Query immutable audit trail events.
