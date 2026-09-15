# ProcurePilot Grounded RAG & Q&A Engine

## 1. Principles of Tender RAG
1. **Zero Hallucination Tolerance**: General world knowledge cannot substitute for explicit clauses in a tender document.
2. **Mandatory Citations**: Every generated answer must include Page Number, Section Header, and the direct Supporting Passage.
3. **Graceful Refusal**: If the requested information is absent or ambiguous, the model responds:
   *"I couldn't find sufficient evidence for this in the tender document."*
4. **Contradiction Detection**: If two clauses provide conflicting guidelines (e.g. general terms vs specific schedule), both are cited with a recommendation for formal Pre-Bid Clarification.

## 2. Query Pipeline
```
[ User Query ] ──► [ Query Embedding & Lexical Search ]
                            │
                            ▼
          [ Retrieve Top-K Relevant Tender Chunks ]
                            │
                            ▼
          [ Format Grounded Context with Page Markers ]
                            │
                            ▼
          [ LLM Inference with Strict Evidence Prompt ]
                            │
                            ▼
     [ Parse Response + Validate Page & Section Citations ]
                            │
                            ▼
              [ Return Grounded Answer to User ]
```
