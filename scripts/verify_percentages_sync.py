import requests

base_url = "http://localhost:8080/api"
login = requests.post(f"{base_url}/auth/login", json={"email": "founder@aerodef.in", "password": "Password123!"}).json()
token = login["data"]["token"]
headers = {"Authorization": f"Bearer {token}"}

print("1. TENDERS SUMMARY & MATCHES:")
tenders = requests.get(f"{base_url}/tenders", headers=headers).json()["data"]
for t in tenders:
    print(f"   Tender #{t['id']} [{t['tenderRefNo']}]: matchScore={t.get('matchScore')}% status={t.get('eligibilityStatus')}")

print("\n2. TENDER #1 DETAIL BEFORE ANALYSE:")
t1_before = requests.get(f"{base_url}/tenders/1", headers=headers).json()["data"]
print(f"   matchScore={t1_before.get('matchScore')}% status={t1_before.get('eligibilityStatus')}")

print("\n3. RUN ANALYSE TENDER #1:")
analyse_res = requests.post(f"{base_url}/tenders/1/analyse", headers=headers).json()["data"]
print(f"   overallScore={analyse_res.get('overallScore')}% technicalScore={analyse_res.get('technicalScore')}%")

print("\n4. TENDER #1 DETAIL AFTER ANALYSE:")
t1_after = requests.get(f"{base_url}/tenders/1", headers=headers).json()["data"]
print(f"   matchScore={t1_after.get('matchScore')}% status={t1_after.get('eligibilityStatus')}")

print("\n5. COMPLIANCE & OPPORTUNITIES READINESS SYNC:")
opps = requests.get(f"{base_url}/opportunities", headers=headers).json()["data"]
all_synced = True
for o in opps:
    t_id = o["tender"]["id"]
    comp = requests.get(f"{base_url}/compliance/tender/{t_id}", headers=headers).json()["data"]
    ready_count = sum(1 for c in comp if c.get("status") in ["READY", "VERIFIED"])
    expected_pct = round((ready_count / max(1, len(comp))) * 100)
    is_match = (o.get("readinessPercent") == expected_pct)
    if not is_match:
        all_synced = False
    print(f"   Opp #{o['id']} (Tender #{t_id}): Readiness={o.get('readinessPercent')}%, Checklist ({ready_count}/{len(comp)})={expected_pct}% -> MATCH: {is_match}")

print(f"\nALL READINESS PERCENTAGES PERFECTLY SYNCHRONIZED: {all_synced}")
