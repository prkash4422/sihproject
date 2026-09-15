# 🇮🇳 ProcurePilot — Intelligent Public Procurement Intelligence for Indian Startups

[![React 19](https://img.shields.io/badge/Frontend-React%2019%20%2B%20Vite-61dafb?logo=react)](https://react.dev/)
[![Spring Boot 3](https://img.shields.io/badge/Backend-Spring%20Boot%203.3-6db33f?logo=springboot)](https://spring.io/projects/spring-boot)
[![FastAPI](https://img.shields.io/badge/AI%20Microservice-FastAPI%20%2B%20FAISS-009688?logo=fastapi)](https://fastapi.tiangolo.com/)
[![TypeScript](https://img.shields.io/badge/Language-TypeScript%205-3178c6?logo=typescript)](https://www.typescriptlang.org/)
[![Java 17](https://img.shields.io/badge/Language-Java%2017-ed8b00?logo=openjdk)](https://openjdk.org/)
[![Vercel Ready](https://img.shields.io/badge/Deployment-Vercel%20Ready-000000?logo=vercel)](https://vercel.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> **ProcurePilot** is a production-grade procurement assistance platform designed to empower DPIIT-recognized Indian startups to discover, evaluate, and win public tenders across **GeM (Government e-Marketplace)**, **CPPP (Central Public Procurement Portal)**, and **PSUs**.
>
> ⚠️ *Disclaimer: ProcurePilot provides intelligent decision support. It is not a replacement for official government portals and does not constitute a legally binding decision.*

---

## 📑 Table of Contents

- [Problem Statement](#-problem-statement)
- [System Architecture](#-system-architecture)
- [Core Platform Capabilities](#-core-platform-capabilities)
- [Deterministic Matching & Eligibility Rules](#-deterministic-matching--eligibility-rules)
- [Quick Start & Local Setup](#-quick-start--local-setup)
- [SIH 1-Click Demo Accounts](#-sih-1-click-demo-accounts)
- [Pre-Loaded Tender Dataset](#-pre-loaded-tender-dataset)
- [Vercel & Cloud Deployment](#-vercel--cloud-deployment)
- [Project Directory Structure](#-project-directory-structure)
- [REST API Endpoints Reference](#-rest-api-endpoints-reference)

---

## 🎯 Problem Statement

Indian startups frequently miss out on government tenders worth thousands of crores due to:
1. **Harsh Prior Turnover & Experience Clauses**: Startups assume they are disqualified, unaware that **Rule 161(iv) of General Financial Rules (GFR 2017)** and DPIIT notifications exempt recognized startups from turnover and prior experience criteria.
2. **Dense 100+ Page Tender PDFs**: High cognitive load in extracting exact technical parameters, clauses, and submission deadlines.
3. **Scattered Compliance Proofs**: Difficulty mapping startup certificates (PAN, GSTIN, ISO, DPIIT, OEM benchmarks) to tender requirements.
4. **Ungrounded AI Hallucinations**: Generic LLMs hallucinate eligibility criteria without citing official tender pages or legal clauses.

**ProcurePilot solves all 4 challenges** with deterministic legal rules, grounded vector RAG, and an interactive compliance workspace.

---

## 🏗️ System Architecture

```text
                                  ┌────────────────────────┐
                                  │   React 19 + Vite UI   │
                                  │ (Glassmorphism + Dark) │
                                  └───────────┬────────────┘
                                              │ HTTP / JSON
                                              ▼
                                  ┌────────────────────────┐
                                  │  Spring Boot 3 API GW  │
                                  │  (JWT Auth & Services) │
                                  └─────┬────────────┬─────┘
                                        │            │
            ┌───────────────────────────┘            └───────────────────────────┐
            ▼                                                                    ▼
┌───────────────────────┐                                            ┌───────────────────────┐
│  Deterministic Rules  │                                            │  FastAPI + FAISS RAG  │
│  & 5-Factor Matcher   │                                            │ (Page-Grounded Embed) │
└───────────┬───────────┘                                            └───────────┬───────────┘
            │                                                                    │
            └───────────────────────────┬────────────────────────────────────────┘
                                        ▼
                            ┌────────────────────────┐
                            │  Relational Database   │
                            │ (H2 / PostgreSQL JPA)  │
                            └────────────────────────┘
```

### Technology Stack Breakdown
- **Frontend**: React 19, TypeScript, Vite, Vanilla CSS Design System, Lucide Icons, Canvas Confetti.
- **Backend**: Spring Boot 3.3.4, Java 17, Spring Security (Stateless JWT), Spring Data JPA, Hibernate.
- **AI Microservice**: FastAPI, Python 3.11, Sentence Transformers embeddings, FAISS Vector Index.
- **Database**: H2 in-file database for zero-config demo; PostgreSQL ready for enterprise deployment.
- **DevOps**: Docker, Docker Compose, Multi-stage builds, Nginx reverse proxy, Vercel SPA rewrites.

---

## 🌟 Core Platform Capabilities

| Module | Description | Key Feature |
| :--- | :--- | :--- |
| 📊 **Dashboard & Metrics** | Real-time overview of active opportunities, top match scores, and upcoming deadlines. | Dynamic GFR 161(iv) status indicator and compliance readiness tracker. |
| 🔍 **Tender Discovery** | Keyword and category faceted tender search feed with match score badges. | Color-coded eligibility (`PASS` 🟢, `NEEDS REVIEW` 🟡, `FAIL` 🔴). |
| 🏢 **Tender Workspace** | 8-tab comprehensive tender analysis cockpit. | Clause extraction, 5-factor breakdown, gap analysis, and grounded Q&A. |
| ⚖️ **Deterministic Eligibility** | Non-hallucinative evaluation against statutory public procurement exemptions. | Rule 161(iv) turnover & experience relaxations with legal citations. |
| ✅ **Compliance Checklist** | Dynamic mapping between required criteria and startup vault documents. | Link certificates from vault or upload new PDFs with instant sync. |
| 💬 **Grounded AI Q&A** | Semantic RAG document question-answering. | Exact page, section, and quote citations for every answer. |
| 📌 **Opportunity Kanban** | 5-stage procurement bid pipeline tracker. | Drag/drop workflow stages from `INTERESTED` to `OUTCOME` with task lists. |
| 🛡️ **Admin Governance** | Back-office management portal for procurement officers. | Requirement curation, rule catalog versioning, and immutable audit logs. |

---

## ⚖️ Deterministic Matching & Eligibility Rules

ProcurePilot uses a **two-tier evaluation engine**:

### 1. Zero-Hallucination Deterministic Eligibility Engine
Evaluates turnover, experience, and certification criteria against registered startup credentials using deterministic logic:
- **GFR 161(iv) Turnover Relaxation**: If startup is DPIIT recognized, minimum turnover criteria are waived.
- **GFR 161(iv) Experience Relaxation**: Prior years requirement waived when certified prototype or technical capability is verified.
- **EMD Exemption**: 100% Earnest Money Deposit waiver applied under Central Public Procurement Policy.

### 2. Transparent 5-Factor Weighted Match Model
$$\text{Match Score} = (0.40 \times \text{Technical}) + (0.25 \times \text{Sector}) + (0.20 \times \text{Eligibility}) + (0.10 \times \text{Documents}) + (0.05 \times \text{Fit})$$

- **Technical Alignment (40%)**: Keyword & capability fingerprint overlap.
- **Sector Relevance (25%)**: Alignment with registered DPIIT primary industry domain.
- **Eligibility Completeness (20%)**: Percentage of passed mandatory clauses.
- **Document Readiness (10%)**: Availability of PAN, GSTIN, ISO, and DPIIT certificates in vault.
- **Opportunity Fit (5%)**: EMD exemption savings and startup preference benefits.

---

## 🚀 Quick Start & Local Setup

### Prerequisites
- **Java**: OpenJDK 17 or higher
- **Node.js**: v18.0.0 or higher & `npm`
- **Python**: 3.10 or higher & `pip`
- **Git**

### 1. Clone the Repository
```bash
git clone https://github.com/prkash4422/sihproject.git
cd sihproject
```

### 2. Run with Docker Compose (Recommended - 1 Command)
```bash
docker-compose up --build
```
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080/api`
- AI Microservice: `http://localhost:8000/docs`

---

### 3. Run Manually (Local Development)

#### Step A: Start Spring Boot Backend
```bash
cd backend
mvn spring-boot:run
```
*Backend will start on `http://localhost:8080` and auto-seed sample tenders and opportunities.*

#### Step B: Start FastAPI AI Service
```bash
cd ai-service
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```
*AI service will start on `http://localhost:8000`.*

#### Step C: Start React Frontend
```bash
cd frontend
npm install
npm run dev
```
*Frontend will launch on `http://localhost:5173`.*

---

## 🔑 SIH 1-Click Demo Accounts

The application is pre-seeded with interactive 1-click login buttons:

| Role | Email | Password | Access & Persona |
| :--- | :--- | :--- | :--- |
| 🚀 **Startup Founder** | `founder@aerodef.in` | `Password123!` | AeroDef Cognitive Systems (DPIIT Recognized, ₹1.8 Cr turnover, ISO 9001 certified). |
| 🏛️ **Procurement Admin** | `admin@procurepilot.gov.in` | `AdminSecret123!` | Ministry / PSU procurement officer with rule management and audit log access. |

---

## 📦 Pre-Loaded Tender Dataset

ProcurePilot comes pre-loaded with 5 realistic Indian public procurement tenders:

| ID | Tender Reference | Organization | Domain | Value | Match Score | Eligibility Status | Compliance |
| :---: | :--- | :--- | :--- | :---: | :---: | :---: | :---: |
| **1** | `GEM/2026/B/892301` | Ministry of Defence / Smart Cities | Defence & Edge AI | ₹2.85 Cr | **89%** | 🟢 **PASS (GFR 161-IV)** | 60% (3/5 items) |
| **5** | `BEL/DEF/2025/EO-11` | Bharat Electronics Limited (BEL) | Defence Electronics | ₹3.50 Cr | **85%** | 🟢 **PASS (GFR 161-IV)** | 100% (4/4 items) |
| **4** | `ISRO/SAT/2026/099` | ISRO / ISTRAC Network | Space & Telemetry | ₹1.80 Cr | **76%** | 🟢 **PASS (GFR 161-IV)** | 100% (4/4 items) |
| **2** | `AIIMS/PROC/2026/IOT-77` | AIIMS New Delhi | Healthcare IoT | ₹4.50 Cr | **68%** | 🟡 **NEEDS REVIEW** | 80% (4/5 items) |
| **3** | `NHAI/TECH/2026/CIVIL-402`| NHAI Corridor Division | Heavy Civil Infrastructure | ₹125.0 Cr | **58%** | 🔴 **NEEDS REVIEW** | 40% (2/5 items) |

---

## 🌐 Vercel & Cloud Deployment

ProcurePilot is configured for **1-click deployment on Vercel**:

1. Push your repository to GitHub.
2. In the [Vercel Dashboard](https://vercel.com/dashboard), click **"Add New Project"** and select `sihproject`.
3. Vercel automatically detects the [`vercel.json`](file:///c:/Users/praka/OneDrive/Desktop/sihproject/vercel.json) configuration:
   - **Framework**: `Vite`
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Output Directory**: `frontend/dist`
4. *(Optional)* Add `VITE_API_BASE_URL=https://your-backend-api.com/api` under Environment Variables.
5. Click **Deploy**!

> *Note: If deployed standalone without environment variables, the frontend includes a high-fidelity mock fallback layer so the live demo works instantly with all 5 tenders and interactive features.*

---

## 📁 Project Directory Structure

```text
sihproject/
├── frontend/                     # React 19 + TypeScript Vite application
│   ├── src/
│   │   ├── api/                  # API client & offline demo fallback dataset
│   │   ├── components/           # Navbar, Sidebar, Disclaimer banners
│   │   ├── context/              # AuthContext & session state
│   │   ├── pages/                # Dashboard, Discovery, Workspace, Kanban, Profile, Admin
│   │   └── types/                # Strict TypeScript interfaces
│   ├── vercel.json               # Frontend SPA rewrite rules
│   └── vite.config.ts            # Vite build & proxy configuration
├── backend/                      # Spring Boot 3 Java service
│   ├── src/main/java/com/procurepilot/
│   │   ├── auth/                 # JWT security & user authentication
│   │   ├── common/               # Database Initializer & exception handlers
│   │   ├── compliance/           # Evidence verification & checklist service
│   │   ├── eligibility/          # Deterministic GFR 161(iv) rules engine
│   │   ├── matching/             # 5-factor scoring engine & explainability
│   │   ├── opportunity/          # Kanban pipeline service & task toggle
│   │   ├── startup/              # Profile, documents & capabilities
│   │   └── tender/               # Search, faceted filters & requirements
│   └── pom.xml                   # Maven dependencies
├── ai-service/                   # FastAPI Python RAG microservice
│   ├── api/                      # Clause extraction & semantic query routes
│   └── main.py                   # FastAPI application entrypoint
├── docs/                         # Architecture, Security, API & Deployment guides
├── infrastructure/               # Dockerfiles & Nginx production configuration
├── scripts/                      # Automated verification and testing scripts
├── docker-compose.yml            # Multi-service local orchestrator
├── package.json                  # Root build script for Vercel
├── vercel.json                   # Root deployment configuration
└── README.md                     # Project documentation
```

---

## 📡 REST API Endpoints Reference

### Authentication & Profile
- `POST /api/auth/login` — Authenticate and receive JWT token.
- `POST /api/auth/register` — Register a new startup or administrator.
- `GET /api/startups/me` — Fetch registered startup profile, capabilities, and certificates.
- `POST /api/startups/me/documents` — Upload statutory PDF document to vault.

### Tender Intelligence
- `GET /api/tenders` — Search tenders with optional `query`, `category`, and `department`.
- `GET /api/tenders/{id}` — Get tender specifications, sections, and extracted clauses.
- `POST /api/tenders/{id}/analyse` — Trigger real-time deterministic eligibility & match score evaluation.
- `GET /api/tenders/{id}/match` — Get transparent 5-factor match score breakdown and narrative.
- `GET /api/tenders/{id}/eligibility` — Get clause-by-clause legal compliance results.

### Compliance & Opportunities
- `GET /api/compliance/tender/{id}` — Get dynamic compliance checklist for a tender.
- `POST /api/compliance/items/{id}/evidence` — Link uploaded certificate to compliance criterion.
- `GET /api/opportunities` — Fetch startup opportunity pipeline stages and tasks.
- `PATCH /api/opportunities/{id}/stage` — Update Kanban stage (`INTERESTED` $\rightarrow$ `OUTCOME`).
- `PATCH /api/opportunities/tasks/{id}/toggle` — Mark preparation subtask as complete.

### Grounded RAG Q&A
- `GET /api/tenders/{id}/questions` — Get Q&A conversation history for a tender.
- `POST /api/tenders/{id}/questions` — Ask document-grounded question with page citations.

---

## 📄 License & Attribution

Distributed under the **MIT License**. Built with ❤️ for the **Smart India Hackathon (SIH)**.
