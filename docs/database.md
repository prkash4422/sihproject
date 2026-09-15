# ProcurePilot Database Schema Specification

## 1. Relational Entities (PostgreSQL 15+)

### Core Identity & Access
- `users`: User identity, hashed credentials, contact info, status.
- `roles`: `STARTUP`, `PROCUREMENT_ADMIN`, `SYSTEM_ADMIN`.
- `user_roles`: Many-to-many relationship mapping users to security roles.

### Startup Profile & Evidence
- `startups`: DPIIT registration number, Udyam number, company name, incorporation date, turnover in INR, net worth, sector, location, capability fingerprint.
- `startup_capabilities`: Specific capabilities, keywords, and proficiency levels.
- `startup_certifications`: ISO, CMMI, BIS, etc., with issuing authority, dates, and document links.
- `startup_documents`: Uploaded evidence (PAN, GST, ITR, Balance Sheets, DPIIT Certificates) with checksums and mime types.

### Tenders & Requirements
- `tenders`: Reference number, title, department, estimated value, EMD, published/closing dates, source portal, status.
- `tender_documents`: Associated tender PDF documents and attachments.
- `tender_sections`: Page-aware structural sections of the tender document.
- `tender_requirements`: Parsed requirement statements, types (`TURNOVER`, `EXPERIENCE`, `CERTIFICATION`, `TECHNICAL`, `LOCATION`, `DOCUMENT`), normalized values, operators (`GTE`, `LTE`, `EQUALS`, `CONTAINS`), mandatory flags, confidence scores, and source page/section coordinates.

### Eligibility & Rules Engine
- `eligibility_rules`: Master rule catalogue for public procurement policies (e.g., GFR 161(iv), DPIIT Notification No. 5(4)/2017-BE-I).
- `eligibility_rule_versions`: Versioned rules containing relaxation condition definitions and applicability logic.
- `startup_tender_matches`: Overall and dimensional match scores (technical, sector, eligibility, readiness, fit) and explainable JSON breakdown.
- `eligibility_evaluations`: Granular evaluation rows for each requirement vs startup fact, recording result (`PASS`, `FAIL`, `NEEDS_REVIEW`, `MISSING_DATA`), evaluated reason, relaxation rule applied, and source page.

### Compliance & Opportunity Lifecycle
- `compliance_items`: Dynamic checklist items generated for a tender with status (`NOT_STARTED`, `IN_PROGRESS`, `READY`, `MISSING`, `NEEDS_REVIEW`, `VERIFIED`).
- `compliance_evidence`: Mapping of startup documents to required compliance checklist items.
- `opportunities`: Kanban opportunity stage (`INTERESTED`, `PREPARING`, `SUBMITTED`, `EVALUATION`, `OUTCOME`), readiness percentage, due dates.
- `opportunity_tasks`: Subtasks associated with opportunity preparation.

### AI, RAG & Audit Subsystems
- `ai_processing_jobs`: Asynchronous jobs tracking document parsing and requirement extraction status.
- `document_chunks`: Text chunks stored with page number and section metadata for RAG retrieval.
- `qa_sessions` & `qa_messages`: Conversational Q&A history with grounded citation metadata.
- `notifications`: User notification queue for upcoming deadlines and high-fit tender discoveries.
- `audit_logs`: Immutable audit log recording actor, action, target entity, timestamp, and metadata.
