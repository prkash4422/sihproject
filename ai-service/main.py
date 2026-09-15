from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from api.rag_routes import router as rag_router

app = FastAPI(
    title="ProcurePilot AI Service",
    description="Document Extraction, NLP Chunking, and Grounded RAG with strict citations",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(rag_router)

@app.get("/health")
def health_check():
    return {"status": "HEALTHY", "service": "procurepilot-ai-service"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
