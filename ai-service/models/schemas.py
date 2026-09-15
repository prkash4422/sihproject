from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

class Citation(BaseModel):
    page: int
    section: str
    snippet: str

class QARequest(BaseModel):
    tenderId: int
    question: str
    startupId: Optional[int] = None

class QAResponse(BaseModel):
    answer: str
    citations: List[Citation] = Field(default_factory=list)
    confidence: str = "HIGH"
    grounded: bool = True

class ExtractionRequest(BaseModel):
    tenderId: int
    pdfPath: str

class ExtractedRequirement(BaseModel):
    type: str
    text: str
    normalizedValue: Optional[str] = None
    unit: Optional[str] = None
    operator: str = "GTE"
    mandatory: bool = True
    preferred: bool = False
    sourcePage: int
    sourceSection: str
    sourceSnippet: str
    confidence: str = "HIGH"
    reviewStatus: str = "APPROVED"

class ExtractionResponse(BaseModel):
    tenderId: int
    requirements: List[ExtractedRequirement] = Field(default_factory=list)
    status: str = "COMPLETED"
