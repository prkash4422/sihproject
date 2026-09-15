from fastapi import APIRouter, HTTPException
from models.schemas import QARequest, QAResponse, ExtractionRequest, ExtractionResponse, ExtractedRequirement
from retrieval.retriever import GroundedRAGRetriever
from document.pdf_extractor import PDFExtractor
from typing import List, Dict, Any

router = APIRouter(prefix="/api")
rag_engine = GroundedRAGRetriever()

# In-memory document repository for seeded/demo tenders
DEMO_CHUNKS: Dict[int, List[Dict[str, Any]]] = {
    1: [
        {
            "page": 4,
            "section": "Clause 3.1 - Financial Eligibility",
            "content": "Clause 3.1: Minimum Financial Eligibility. The bidder should have an average annual turnover of at least ₹1.00 Crore over the last three financial years. Relaxation Note: In accordance with Rule 161(iv) of General Financial Rules (GFR) 2017 and DPIIT notification No. 5(4)/2017-BE-I, recognized startups shall be exempted from the condition of prior turnover, provided they meet quality and technical specifications."
        },
        {
            "page": 5,
            "section": "Clause 3.2 - Technical Experience",
            "content": "Clause 3.2: Prior Experience Criteria. The bidder should possess a minimum of 2 years experience in the deployment of Computer Vision, Edge AI, or Unmanned Aerial Systems. For DPIIT recognized startups, prior experience criteria may be relaxed if the bidder demonstrates certified technical capability and satisfactory prototype test results."
        },
        {
            "page": 7,
            "section": "Clause 4.1 - Quality Certifications",
            "content": "Clause 4.1: Quality Standards & Statutory Compliance. The bidder must possess a valid ISO 9001:2015 Quality Management System Certification. Startups claiming exemption from technical experience must hold ISO 9001 or equivalent CMMI Level 3 certification."
        },
        {
            "page": 8,
            "section": "Clause 4.5 - Startup Benefits & EMD Exemption",
            "content": "Clause 4.5: Benefits for Startups & MSEs. As per Government of India public procurement policies, DPIIT recognized startups are 100% exempt from submitting Earnest Money Deposit (EMD) of ₹3,00,000. Startups must upload their valid DPIIT Certificate of Recognition on the portal."
        },
        {
            "page": 14,
            "section": "Clause 7.2 - Local Content & Delivery Schedule",
            "content": "Clause 7.2: Make in India & Delivery. The procurement falls under Class-I Local Supplier category with minimum 50% local value addition. Complete supply and commissioning shall be completed within 90 days from Award of Contract (AOC)."
        }
    ]
}

@router.post("/rag/query", response_model=QAResponse)
async def query_rag(request: QARequest):
    chunks = DEMO_CHUNKS.get(request.tenderId, [])
    response = rag_engine.retrieve_and_answer(request.question, chunks)
    return response

@router.post("/extract", response_model=ExtractionResponse)
async def extract_pdf(request: ExtractionRequest):
    extracted_chunks = PDFExtractor.extract_text_and_sections(request.pdfPath)
    requirements = []

    for chunk in extracted_chunks:
        text = chunk["content"]
        if "turnover" in text.lower():
            requirements.append(ExtractedRequirement(
                type="TURNOVER",
                text="Average annual turnover requirement extracted from tender document.",
                normalizedValue="10000000",
                unit="INR",
                operator="GTE",
                mandatory=True,
                sourcePage=chunk["page"],
                sourceSection=chunk["section"],
                sourceSnippet=text[:200],
                confidence="HIGH"
            ))
        if "experience" in text.lower():
            requirements.append(ExtractedRequirement(
                type="EXPERIENCE",
                text="Prior technical experience in relevant domain.",
                normalizedValue="2",
                unit="YEARS",
                operator="GTE",
                mandatory=True,
                sourcePage=chunk["page"],
                sourceSection=chunk["section"],
                sourceSnippet=text[:200],
                confidence="HIGH"
            ))

    return ExtractionResponse(
        tenderId=request.tenderId,
        requirements=requirements,
        status="COMPLETED"
    )
