@echo off
echo ===================================================
echo Starting ProcurePilot Multi-Service Development Hub
echo ===================================================

echo [1/3] Launching Spring Boot Backend on port 8080...
start "ProcurePilot Backend" cmd /k "cd backend && c:\Users\praka\tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run"

echo [2/3] Launching FastAPI AI Service on port 8000...
start "ProcurePilot AI Service" cmd /k "cd ai-service && uvicorn main:app --reload --port 8000"

echo [3/3] Launching React Vite Frontend on port 5173...
start "ProcurePilot Frontend" cmd /k "cd frontend && npm run dev"

echo.
echo All services launched!
echo Open your browser at: http://localhost:5173
