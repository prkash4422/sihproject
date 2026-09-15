# ProcurePilot Deployment & DevOps Specification

## 1. Environments
- **Local Development**:
  - Frontend: Vite Dev Server (`http://localhost:5173`)
  - Backend: Spring Boot Application with Dev profile (`http://localhost:8080`)
  - AI Service: FastAPI with Uvicorn (`http://localhost:8000`)
- **Containerized / Production (Docker Compose)**:
  - `postgres`: PostgreSQL 15 database container (`5432`)
  - `backend`: Java 17 Alpine multi-stage container (`8080`)
  - `ai-service`: Python 3.11 Slim container (`8000`)
  - `frontend`: Nginx Alpine serving optimized React production bundle (`80` / `5173`)

## 2. Orchestration (`docker-compose.yml`)
All services are pre-configured with healthchecks, environment injection, and volume persistence.
