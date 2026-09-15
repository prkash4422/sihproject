import os
import re
from typing import List, Dict, Any
from models.schemas import ExtractedRequirement

class PDFExtractor:
    """Page-aware PDF text & table extractor with section segmentation."""

    @staticmethod
    def extract_text_and_sections(pdf_path: str) -> List[Dict[str, Any]]:
        chunks = []
        if not os.path.exists(pdf_path):
            return chunks

        try:
            import pdfplumber
            with pdfplumber.open(pdf_path) as pdf:
                for page_idx, page in enumerate(pdf.pages, start=1):
                    text = page.extract_text() or ""
                    tables = page.extract_tables() or []

                    table_text = ""
                    for table in tables:
                        for row in table:
                            clean_row = [str(cell).strip() for cell in row if cell is not None]
                            if clean_row:
                                table_text += " | ".join(clean_row) + "\n"

                    combined_text = (text + "\n" + table_text).strip()
                    if combined_text:
                        chunks.append({
                            "page": page_idx,
                            "section": PDFExtractor._detect_section_title(combined_text, page_idx),
                            "content": combined_text
                        })
        except Exception as e:
            # Fallback to PyPDF
            try:
                from pypdf import PdfReader
                reader = PdfReader(pdf_path)
                for page_idx, page in enumerate(reader.pages, start=1):
                    text = page.extract_text() or ""
                    if text.strip():
                        chunks.append({
                            "page": page_idx,
                            "section": PDFExtractor._detect_section_title(text, page_idx),
                            "content": text.strip()
                        })
            except Exception as e2:
                print(f"Error extracting PDF: {e2}")

        return chunks

    @staticmethod
    def _detect_section_title(text: str, page_num: int) -> str:
        lines = [line.strip() for line in text.split("\n") if line.strip()]
        for line in lines[:5]:
            if re.match(r'^(clause|section|part|schedule|annexure|chapter)\s+\d+', line, re.IGNORECASE):
                return line[:100]
            if any(keyword in line.lower() for keyword in ["eligibility", "turnover", "experience", "technical specifications", "scope of work"]):
                return line[:100]
        return f"Section (Page {page_num})"
