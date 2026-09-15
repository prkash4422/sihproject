# ProcurePilot — Intelligent Public Procurement Intelligence for Startups

ProcurePilot is a production-grade procurement-assistance platform designed to empower startups navigating Indian public procurement (GeM, CPPP, State Portals, PSUs).

> **Disclaimer**: ProcurePilot provides decision support and does not replace official government procurement portals or constitute a binding legal or government eligibility decision.

---

## 🌟 Key Features

1. **12-Step Guided Startup Onboarding**: Comprehensive capture of DPIIT recognition, Udyam registration, financial metrics, certifications (ISO, CMMI, BIS), and capability fingerprinting.
2. **Tender Discovery & Search**: Faceted search with real-time eligibility status, closing date alerts, and match badges.
3. **Deterministic Eligibility Engine**: Zero AI hallucination. Evaluates turnover, experience, and certifications against GFR 161(iv) and DPIIT public procurement policy exemptions, outputting `PASS`, `FAIL`, or `NEEDS_REVIEW` with exact legal citations.
4. **Transparent Weighted Matching Engine**: Explainable 5-factor scoring model (40% Capability, 25% Sector, 20% Eligibility, 10% Documents, 5% Fit) highlighting both strengths and missing gaps.
5. **Dynamic Compliance Workspace**: Interactive checklist with evidence uploading, document mapping, and readiness progress.
6. **Grounded AI Tender Q&A**: Page-aware document RAG with mandatory citations (page, section, exact quote) and refusal guards against ungrounded queries.
7. **Opportunity Kanban Tracker**: Preparation pipeline tracking stages from `INTERESTED` to `OUTCOME`.
8. **Admin Governance & Review Queue**: Curation of low-confidence extractions, rule catalog management, and immutable audit logs.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+ & npm
- Python 3.10+
- Maven 3.9+ (portable runner included)

### Running Locally

```bash
# 1. Start Spring Boot Backend
cd backend
mvn spring-boot:run

# 2. Start FastAPI AI Service
cd ai-service
pip install -r requirements.txt
uvicorn main:app --reload --port 8000

# 3. Start React Frontend
cd frontend
npm install
npm run dev
```

Visit: `http://localhost:5173`

---

## 🔑 SIH Demo Credentials

- **Startup User**: `founder@aerodef.in` | `Password123!`
- **Procurement Admin**: `admin@procurepilot.gov.in` | `AdminSecret123!`
