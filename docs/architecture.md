# ProcurePilot Architecture Document

## 1. System Overview
ProcurePilot is an intelligent procurement-assistance platform for startups participating in Indian public procurement (GeM, CPPP, State Portals, PSUs). It provides an intelligence and workflow layer that ingests tenders, parses complex requirements, deterministically evaluates startup eligibility (applying GFR 161(iv) and DPIIT public procurement policy relaxations), calculates explainable match scores, generates compliance checklists, and provides grounded tender Q&A with strict document citations.

## 2. High-Level Architecture

```
+-------------------------------------------------------------------------+
|                              Frontend                                   |
|       React 18 + TypeScript + Vite + Tailwind CSS + Lucide Icons        |
|    - 12-Step Guided Onboarding      - Opportunity Kanban Tracker       |
|    - Tender Discovery & Search      - Compliance Evidence Workspace     |
|    - Tender Detail & Workspace      - Grounded RAG Chat Interface       |
|    - Deterministic Breakdown        - Admin Review & Rule Manager       |
+------------------------------------+------------------------------------+
                                     | REST (JSON / JWT)
                                     v
+-------------------------------------------------------------------------+
|                           Backend (Java 17)                             |
|                  Spring Boot 3.3.x + Spring Security                    |
|  - Auth & Role Guards (STARTUP / PROCUREMENT_ADMIN / SYSTEM_ADMIN)      |
|  - Deterministic Eligibility Rule Engine (GFR 161(iv), DPIIT, PPP-MII)  |
|  - Transparent Weighted Matching Engine (40/25/20/10/5 model)           |
|  - Compliance Checklist & Document Evidence Verification                |
|  - Opportunity Lifecycle & Task Management                              |
|  - Audit Logging & Structured Error Pipeline                            |
+-------------------+--------------------------------+--------------------+
                    | JPA / Flyway                   | HTTP Client
                    v                                v
+------------------------------------+   +--------------------------------+
|       Database (PostgreSQL)        |   |       AI Service (Python)      |
|  - 22 Normalized Entities          |   |       FastAPI + Pydantic v2    |
|  - Multi-tenant tenant isolation   |   |  - Page-aware PDF Parser       |
|  - Complete Audit Trail            |   |  - Document Chunking           |
|  - Realistic SIH Demo Seed Data    |   |  - Grounded RAG with Citations |
+------------------------------------+   +--------------------------------+
```

## 3. Core Subsystems

### 3.1 Deterministic Eligibility Engine (Java)
- **Principle**: AI does NOT decide eligibility.
- Evaluates:
  1. Turnover criteria vs startup turnover (including ₹/Lakhs/Crores conversion).
  2. Experience criteria vs startup operational years.
  3. Required certifications (ISO, CMMI, BIS, etc.) vs active verified startup credentials.
  4. DPIIT Recognition exemptions: GFR Rule 161(iv) exemption for turnover and prior experience in non-safety-critical procurements.
  5. Produces: `PASS`, `FAIL`, `NEEDS_REVIEW`, `MISSING_DATA`, `NOT_APPLICABLE` with exact rule citations and page references.

### 3.2 Transparent Matching Engine (Java)
- **Weighted Factors**:
  - Technical Capability Match: 40%
  - Sector / Product Relevance: 25%
  - Eligibility Completeness: 20%
  - Document Readiness: 10%
  - Opportunity Fit: 5%
- Generates clear qualitative explanations highlighting both positive alignment and critical missing gaps.

### 3.3 Document Processing & Grounded RAG (Python FastAPI)
- Extracts text and tables from PDFs with exact page number and section preservation.
- Grounded Q&A searches document chunks, verifies citations, and falls back to strict refusal if evidence is not present, preventing hallucinations.
