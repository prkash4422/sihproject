@echo off
echo ===================================================
echo Running Complete ProcurePilot Autonomous Test Suite
echo ===================================================

echo [1/3] Running Spring Boot Backend JUnit 5 Tests...
cd backend
call c:\Users\praka\tools\apache-maven-3.9.6\bin\mvn.cmd test
if %errorlevel% neq 0 (
    echo [ERROR] Backend tests failed!
    exit /b %errorlevel%
)
cd ..

echo [2/3] Running Python AI Service Grounded RAG Tests...
cd ai-service
python -m pytest tests/
if %errorlevel% neq 0 (
    echo [ERROR] AI Service tests failed!
    exit /b %errorlevel%
)
cd ..

echo [3/3] Running Frontend TypeScript Typecheck and Production Build...
cd frontend
call npm run build
if %errorlevel% neq 0 (
    echo [ERROR] Frontend build failed!
    exit /b %errorlevel%
)
cd ..

echo.
echo ===================================================
echo ALL PROCUREPILOT TEST SUITES PASSED (100% GREEN)
echo ===================================================
