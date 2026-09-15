# ProcurePilot Security & Compliance Blueprint

## 1. Authentication & Authorization
- **Stateless JWT**: Signed using HMAC-SHA512 with configurable secret and expiration.
- **Role-Based Access Control (RBAC)**:
  - `ROLE_STARTUP`: Access to owned profile, documents, matched opportunities, and Q&A.
  - `ROLE_PROCUREMENT_ADMIN`: Ingest tenders, review extraction queue, curate eligibility rules.
  - `ROLE_SYSTEM_ADMIN`: System health, audit log inspection, user governance.
- **Tenant Isolation**: Startup documents and match details are partitioned by `startup_id`. File access validates resource ownership before streaming content.

## 2. File Security & Malware Prevention
- **Validation**: Strict whitelist validation for MIME types (`application/pdf`, `image/png`, `image/jpeg`).
- **File Size Limits**: Max 25MB for tender PDFs, 10MB for startup compliance evidence.
- **Path Traversal Prevention**: Filenames are sanitized and stored using randomized UUID keys.

## 3. Data Protection & Sensitive Information
- **Password Security**: BCrypt password hashing with work factor 12.
- **Sanitized Logging**: Sensitive credentials, JWT tokens, and PII are redacted from application and audit logs.
- **CORS Configuration**: Restricts origin access to authorized frontend domains.
