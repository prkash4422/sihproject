import pytest
from retrieval.retriever import GroundedRAGRetriever

CHUNKS = [
    {
        "page": 4,
        "section": "Clause 3.1 - Financial Eligibility",
        "content": "Clause 3.1: Minimum Financial Eligibility. The bidder should have an average annual turnover of at least ₹1.00 Crore. DPIIT recognized startups shall be exempted from the condition of prior turnover."
    },
    {
        "page": 5,
        "section": "Clause 3.2 - Technical Experience",
        "content": "Clause 3.2: Prior Experience Criteria. The bidder should possess a minimum of 2 years experience in Computer Vision."
    },
    {
        "page": 8,
        "section": "Clause 4.5 - Startup Benefits",
        "content": "Clause 4.5: Benefits for Startups. DPIIT recognized startups are 100% exempt from submitting Earnest Money Deposit (EMD) of ₹3,00,000."
    }
]

def test_grounded_answer_with_citation():
    retriever = GroundedRAGRetriever()
    response = retriever.retrieve_and_answer("Is prior experience mandatory for startups?", CHUNKS)

    assert response.grounded is True
    assert len(response.citations) > 0
    assert response.citations[0].page in [4, 5]
    assert "Clause" in response.citations[0].section

def test_unmentioned_query_refuses_to_hallucinate():
    retriever = GroundedRAGRetriever()
    response = retriever.retrieve_and_answer("What is the nuclear submarine warranty duration?", CHUNKS)

    assert response.grounded is False
    assert len(response.citations) == 0
    assert "couldn't find sufficient evidence" in response.answer
