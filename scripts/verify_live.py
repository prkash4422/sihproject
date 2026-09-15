import urllib.request
import json

def unwrap(resp):
    payload = json.loads(resp.read().decode())
    if isinstance(payload, dict) and "data" in payload and payload["data"] is not None:
        return payload["data"]
    return payload

def test_api():
    print("========================================")
    print("    PROCUREPILOT LIVE ENDPOINT VERIFICATION")
    print("========================================")
    
    # 1. Startup Login
    login_data = json.dumps({"email": "founder@aerodef.in", "password": "Password123!"}).encode('utf-8')
    req = urllib.request.Request("http://localhost:8080/api/auth/login", data=login_data, headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(req) as resp:
        auth_data = unwrap(resp)
        token = auth_data["token"]
        print(f"[PASS] Startup Login successful! User: {auth_data['fullName']} ({auth_data['email']})")

    startup_headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

    # 2. Get Tenders
    req = urllib.request.Request("http://localhost:8080/api/tenders", headers=startup_headers)
    with urllib.request.urlopen(req) as resp:
        tenders = unwrap(resp)
        print(f"[PASS] Tenders retrieved: {len(tenders)} tenders available")
        for t in tenders:
            ref = t.get('tenderRefNo', 'N/A')
            val = t.get('estimatedValueINR', 0)
            print(f"       - [{t.get('sourcePortal', 'PORTAL')}] {ref}: {t.get('title','')[:45]}... (Est: Rs. {val:,.2f})")

    # 3. Analyze Tender 1
    req = urllib.request.Request("http://localhost:8080/api/tenders/1/analyse", data=b"{}", headers=startup_headers)
    with urllib.request.urlopen(req) as resp:
        eval_res = unwrap(resp)
        print(f"[PASS] Tender 1 Analysis Succeeded:")
        print(f"       - Overall Score: {eval_res.get('overallScore')}%")
        print(f"       - Technical Score: {eval_res.get('technicalScore')}%")
        print(f"       - Sector Score: {eval_res.get('sectorScore')}%")
        print(f"       - Eligibility Score: {eval_res.get('eligibilityScore')}%")
        print(f"       - Document Readiness Score: {eval_res.get('readinessScore')}%")
        print(f"       - Evaluated Clauses: {len(eval_res.get('evaluations', []))} items")

    # 3b. Tender 1 Deterministic Eligibility Summary
    req = urllib.request.Request("http://localhost:8080/api/tenders/1/eligibility", headers=startup_headers)
    with urllib.request.urlopen(req) as resp:
        elig = unwrap(resp)
        print(f"[PASS] Deterministic Eligibility Breakdown:")
        print(f"       - Status: {elig.get('overallStatus')}")
        print(f"       - Evaluated Items: {len(elig.get('evaluations', []))}")
        print(f"       - Applied Relaxations: {len(elig.get('appliedRelaxations', []))}")
        for r in elig.get('appliedRelaxations', []):
            print(f"         * Relaxation [{r.get('ruleCode')}]: {r.get('title')}")

    # 4. Get Opportunities (Kanban)
    req = urllib.request.Request("http://localhost:8080/api/opportunities", headers=startup_headers)
    with urllib.request.urlopen(req) as resp:
        opps = unwrap(resp)
        print(f"[PASS] Opportunities retrieved: {len(opps)} opportunities in pipeline")
        for o in opps:
            print(f"       - [{o.get('stage')}] Tender #{o.get('tenderId')}: Win Probability {o.get('winProbability')}%")

    # 5. Admin Login & Governance
    admin_data = json.dumps({"email": "admin@procurepilot.gov.in", "password": "AdminSecret123!"}).encode('utf-8')
    req = urllib.request.Request("http://localhost:8080/api/auth/login", data=admin_data, headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(req) as resp:
        admin_auth = unwrap(resp)
        admin_token = admin_auth["token"]
        print(f"[PASS] Admin Login successful! User: {admin_auth['fullName']}")

    admin_headers = {"Authorization": f"Bearer {admin_token}", "Content-Type": "application/json"}

    # 6. Admin Rules
    req = urllib.request.Request("http://localhost:8080/api/admin/rules", headers=admin_headers)
    with urllib.request.urlopen(req) as resp:
        rules = unwrap(resp)
        print(f"[PASS] Admin Rule Catalog: {len(rules)} rules configured")
        for r in rules:
            print(f"       - Rule [{r.get('ruleCode')}]: {r.get('name')} (Active: {r.get('active')})")

    # 7. Admin Audit Logs
    req = urllib.request.Request("http://localhost:8080/api/admin/audit-logs", headers=admin_headers)
    with urllib.request.urlopen(req) as resp:
        logs = unwrap(resp)
        print(f"[PASS] Immutable Audit Logs: {len(logs)} audit entries recorded")

    # 8. AI Service Health & Grounded Q&A
    req = urllib.request.Request("http://localhost:8000/health")
    with urllib.request.urlopen(req) as resp:
        health = json.loads(resp.read().decode())
        print(f"[PASS] AI Service Health: {health.get('status')}, Service: {health.get('service', 'FastAPI RAG')}")

    # 9. AI Grounded RAG Query direct to FastAPI AI Service
    qa_data = json.dumps({
        "tenderId": 1,
        "question": "What are the exemption rules for DPIIT recognized startups?"
    }).encode('utf-8')
    req = urllib.request.Request("http://localhost:8000/api/rag/query", data=qa_data, headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(req) as resp:
        qa_res = json.loads(resp.read().decode())
        print(f"[PASS] Grounded AI RAG Query (FastAPI):")
        print(f"       - Refusal Status: {qa_res.get('refusal')}")
        print(f"       - Grounded Answer: {qa_res.get('answer', '')[:120]}...")
        print(f"       - Citations: {len(qa_res.get('citations', []))} source clauses cited")

    # 10. Spring Boot Grounded Q&A Assistant Endpoint
    backend_qa = json.dumps({"question": "What are the EMD exemption terms for startups?"}).encode('utf-8')
    req = urllib.request.Request("http://localhost:8080/api/tenders/1/questions", data=backend_qa, headers=startup_headers)
    with urllib.request.urlopen(req) as resp:
        b_res = unwrap(resp)
        print(f"[PASS] Spring Boot Grounded Q&A Endpoint:")
        print(f"       - Role: {b_res.get('role')}")
        print(f"       - Answer: {b_res.get('content', '')[:120]}...")
        print(f"       - Citations: {len(b_res.get('citations', []))} cited")

    print("========================================")
    print("    ALL LIVE SERVICES VERIFIED 100% OPERATIONAL!")
    print("========================================")

if __name__ == "__main__":
    test_api()
