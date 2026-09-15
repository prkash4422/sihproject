import requests
import json

base_url = "http://localhost:8080/api"

# Login as Founder
login_res = requests.post(f"{base_url}/auth/login", json={
    "email": "founder@aerodef.in",
    "password": "Password123!"
})
login_data = login_res.json().get("data", {})
token = login_data.get("token")
headers = {"Authorization": f"Bearer {token}"}

print("=== TENDERS LIST & MATCH SCORES ===")
tenders = requests.get(f"{base_url}/tenders", headers=headers).json().get("data", [])
for t in tenders:
    t_id = t["id"]
    detail = requests.get(f"{base_url}/tenders/{t_id}", headers=headers).json().get("data", {})
    match = requests.get(f"{base_url}/tenders/{t_id}/match", headers=headers).json().get("data", {})
    comp = requests.get(f"{base_url}/compliance/tender/{t_id}", headers=headers).json().get("data", [])
    ready_count = sum(1 for c in comp if c.get("status") == "READY")
    comp_pct = round((ready_count / max(1, len(comp))) * 100) if comp else 0
    print(f"Tender #{t_id} [{t.get('tenderRefNo')}]:")
    print(f"  Summary MatchScore: {t.get('matchScore')}%")
    print(f"  Detail MatchScore:  {detail.get('matchScore')}%")
    print(f"  Direct Match API:   {match.get('overallScore') if match else 'None'}% (Status: {detail.get('eligibilityStatus')})")
    print(f"  Compliance Items:   {ready_count}/{len(comp)} ready ({comp_pct}%)")

print("\n=== OPPORTUNITIES ===")
opps = requests.get(f"{base_url}/opportunities", headers=headers).json().get("data", [])
for o in opps:
    print(f"Opp #{o.get('id')}: Tender #{o.get('tender', {}).get('id')} -> Stage: {o.get('stage')}, Readiness: {o.get('readinessPercent')}%")
