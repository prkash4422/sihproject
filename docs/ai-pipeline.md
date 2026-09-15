# ProcurePilot AI & Document Processing Pipeline

## 1. Document Extraction Architecture
The document processing pipeline operates asynchronously:

```
[ Upload PDF ]
      │
      ▼
[ Validate File & Storage (UUID) ]
      │
      ▼
[ Page-Aware Text & Table Extraction (PyPDF / pdfplumber) ]
      │
      ▼
[ Section & Header Segmentation ]
      │
      ▼
[ Requirement Extraction & Normalization ]
      │ (Turnover, Experience, Certs, Tech specs, Delivery, Statutory)
      ▼
[ Confidence Scoring & Review Flagging ]
      │ (High -> Auto Approved, Low/Ambiguous -> NEEDS_REVIEW Queue)
      ▼
[ Database Persistence (tender_requirements, document_chunks) ]
```

## 2. Requirement Extraction Schema
Each requirement record extracted retains full auditability:
- `type`: `TURNOVER` | `EXPERIENCE` | `CERTIFICATION` | `TECHNICAL` | `LEGAL` | `DOCUMENT` | `FINANCIAL` | `LOCATION`
- `text`: Raw clause extracted from tender text.
- `normalizedValue`: Numeric or categorical parsed value (e.g. `15000000` for ₹1.5 Cr).
- `unit`: `INR`, `YEARS`, `COUNT`, `PERCENT`.
- `operator`: `GTE`, `LTE`, `EQUALS`, `CONTAINS`.
- `mandatory`: `true` | `false`.
- `sourcePage`: Integer page number.
- `sourceSection`: Section identifier (e.g., "Clause 4.2 - Minimum Eligibility").
- `confidence`: `HIGH` (>=0.85), `MEDIUM` (0.60-0.84), `LOW` (<0.60).
- `reviewStatus`: `APPROVED` | `NEEDS_REVIEW`.
