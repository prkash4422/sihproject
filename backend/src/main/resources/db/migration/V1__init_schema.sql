-- ProcurePilot Flyway Migration V1: Initial Schema
-- PostgreSQL DDL with indexes, constraints and audit timestamps

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS startups (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    dpiit_recognized BOOLEAN DEFAULT FALSE,
    dpiit_number VARCHAR(100),
    udyam_number VARCHAR(100),
    incorporation_date DATE,
    annual_turnover_inr NUMERIC(18, 2) DEFAULT 0,
    net_worth_inr NUMERIC(18, 2) DEFAULT 0,
    primary_sector VARCHAR(100),
    state VARCHAR(100),
    city VARCHAR(100),
    website VARCHAR(255),
    capability_fingerprint TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS startup_capabilities (
    id BIGSERIAL PRIMARY KEY,
    startup_id BIGINT NOT NULL REFERENCES startups(id) ON DELETE CASCADE,
    category VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    proficiency_level VARCHAR(50) DEFAULT 'ADVANCED',
    description TEXT
);

CREATE TABLE IF NOT EXISTS startup_certifications (
    id BIGSERIAL PRIMARY KEY,
    startup_id BIGINT NOT NULL REFERENCES startups(id) ON DELETE CASCADE,
    cert_type VARCHAR(100) NOT NULL,
    cert_number VARCHAR(100),
    issuing_body VARCHAR(255),
    issue_date DATE,
    expiry_date DATE,
    document_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS startup_documents (
    id BIGSERIAL PRIMARY KEY,
    startup_id BIGINT NOT NULL REFERENCES startups(id) ON DELETE CASCADE,
    doc_type VARCHAR(100) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    checksum VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenders (
    id BIGSERIAL PRIMARY KEY,
    tender_ref_no VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(500) NOT NULL,
    department VARCHAR(255) NOT NULL,
    authority VARCHAR(255),
    category VARCHAR(100),
    estimated_value_inr NUMERIC(18, 2),
    emd_inr NUMERIC(18, 2),
    published_date TIMESTAMP,
    closing_date TIMESTAMP NOT NULL,
    source_portal VARCHAR(100) DEFAULT 'GeM',
    source_url VARCHAR(500),
    status VARCHAR(50) DEFAULT 'PUBLISHED',
    document_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tender_sections (
    id BIGSERIAL PRIMARY KEY,
    tender_id BIGINT NOT NULL REFERENCES tenders(id) ON DELETE CASCADE,
    section_code VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    page_start INT,
    page_end INT,
    raw_text TEXT
);

CREATE TABLE IF NOT EXISTS tender_requirements (
    id BIGSERIAL PRIMARY KEY,
    tender_id BIGINT NOT NULL REFERENCES tenders(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    requirement_text TEXT NOT NULL,
    normalized_value VARCHAR(255),
    unit VARCHAR(50),
    operator VARCHAR(50) DEFAULT 'GTE',
    mandatory BOOLEAN DEFAULT TRUE,
    preferred BOOLEAN DEFAULT FALSE,
    source_page INT,
    source_section VARCHAR(255),
    source_snippet TEXT,
    confidence VARCHAR(50) DEFAULT 'HIGH',
    review_status VARCHAR(50) DEFAULT 'APPROVED'
);

CREATE TABLE IF NOT EXISTS eligibility_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    authority VARCHAR(100) NOT NULL,
    description TEXT,
    source_url VARCHAR(500),
    effective_date DATE,
    version VARCHAR(50) DEFAULT 'v1.0',
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS eligibility_rule_versions (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL REFERENCES eligibility_rules(id) ON DELETE CASCADE,
    version_tag VARCHAR(50) NOT NULL,
    relaxation_type VARCHAR(100) NOT NULL,
    criteria_conditions_json TEXT,
    source_clause VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS startup_tender_matches (
    id BIGSERIAL PRIMARY KEY,
    startup_id BIGINT NOT NULL REFERENCES startups(id) ON DELETE CASCADE,
    tender_id BIGINT NOT NULL REFERENCES tenders(id) ON DELETE CASCADE,
    overall_score INT NOT NULL,
    technical_score INT NOT NULL,
    sector_score INT NOT NULL,
    eligibility_score INT NOT NULL,
    readiness_score INT NOT NULL,
    fit_score INT NOT NULL,
    explanation_json TEXT,
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(startup_id, tender_id)
);

CREATE TABLE IF NOT EXISTS eligibility_evaluations (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL REFERENCES startup_tender_matches(id) ON DELETE CASCADE,
    requirement_id BIGINT REFERENCES tender_requirements(id) ON DELETE SET NULL,
    startup_value VARCHAR(255),
    tender_value VARCHAR(255),
    operator VARCHAR(50),
    result VARCHAR(50) NOT NULL,
    reason TEXT,
    applied_relaxation_rule_id BIGINT REFERENCES eligibility_rules(id) ON DELETE SET NULL,
    source_page INT,
    source_section VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS compliance_items (
    id BIGSERIAL PRIMARY KEY,
    tender_id BIGINT NOT NULL REFERENCES tenders(id) ON DELETE CASCADE,
    startup_id BIGINT NOT NULL REFERENCES startups(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    mandatory BOOLEAN DEFAULT TRUE,
    status VARCHAR(50) DEFAULT 'NOT_STARTED',
    tender_source_page INT,
    due_date DATE,
    notes TEXT
);

CREATE TABLE IF NOT EXISTS compliance_evidence (
    id BIGSERIAL PRIMARY KEY,
    compliance_item_id BIGINT NOT NULL REFERENCES compliance_items(id) ON DELETE CASCADE,
    startup_document_id BIGINT REFERENCES startup_documents(id) ON DELETE SET NULL,
    verification_status VARCHAR(50) DEFAULT 'PENDING',
    verified_by VARCHAR(255),
    verified_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS opportunities (
    id BIGSERIAL PRIMARY KEY,
    startup_id BIGINT NOT NULL REFERENCES startups(id) ON DELETE CASCADE,
    tender_id BIGINT NOT NULL REFERENCES tenders(id) ON DELETE CASCADE,
    stage VARCHAR(50) DEFAULT 'INTERESTED',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    readiness_percent INT DEFAULT 0,
    target_submission_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(startup_id, tender_id)
);

CREATE TABLE IF NOT EXISTS opportunity_tasks (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    link VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    actor_id BIGINT,
    actor_email VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    ip_address VARCHAR(100),
    metadata_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_processing_jobs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    job_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'QUEUED',
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS document_chunks (
    id BIGSERIAL PRIMARY KEY,
    tender_id BIGINT NOT NULL REFERENCES tenders(id) ON DELETE CASCADE,
    page_number INT NOT NULL,
    section_title VARCHAR(255),
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    token_count INT
);

CREATE TABLE IF NOT EXISTS qa_sessions (
    id BIGSERIAL PRIMARY KEY,
    tender_id BIGINT NOT NULL REFERENCES tenders(id) ON DELETE CASCADE,
    startup_id BIGINT NOT NULL REFERENCES startups(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qa_messages (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES qa_sessions(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    citations_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CREATE INDEXES for high-performance lookup
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_tenders_closing_date ON tenders(closing_date);
CREATE INDEX IF NOT EXISTS idx_tenders_category ON tenders(category);
CREATE INDEX IF NOT EXISTS idx_tenders_department ON tenders(department);
CREATE INDEX IF NOT EXISTS idx_req_tender_id ON tender_requirements(tender_id);
CREATE INDEX IF NOT EXISTS idx_matches_startup_tender ON startup_tender_matches(startup_id, tender_id);
CREATE INDEX IF NOT EXISTS idx_compliance_tender_startup ON compliance_items(tender_id, startup_id);
CREATE INDEX IF NOT EXISTS idx_opportunities_startup_stage ON opportunities(startup_id, stage);
CREATE INDEX IF NOT EXISTS idx_chunks_tender_page ON document_chunks(tender_id, page_number);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON audit_logs(created_at);
