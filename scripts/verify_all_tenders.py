import requests
import json
import time

base_url = "http://localhost:8080/api"

# Wait for backend to be ready
for i in range(25):
    try:
        res = requests.get(f"{base_url}/auth/me")
        print(f"Backend ping responded: {res.status_code}")
        break
    except Exception as e:
        print(f"Waiting for backend... ({i+1}/25)")
        time.sleep(2)

# Login as Founder
login_res = requests.post(f"{base_url}/auth/login", json={
    "email": "founder@aerodef.in",
    "password": "Password123!"
})
print("Login Raw Response:", login_res.text)
login_json = login_res.json()
login_data = login_json.get("data") or login_json
token = login_data.get("token")
headers = {"Authorization": f"Bearer {token}"}
print(f"Login status: {login_res.status_code}, User: {login_data.get('email')}")

# Check all tenders
tenders_res = requests.get(f"{base_url}/tenders", headers=headers)
tenders = tenders_res.json().get("data", [])
print(f"Tenders found: {len(tenders)}")

for t in tenders:
    t_id = t["id"]
    detail = requests.get(f"{base_url}/tenders/{t_id}", headers=headers).json().get("data", {})
    req_count = len(detail.get("requirements", []))
    comp_res = requests.get(f"{base_url}/compliance/tender/{t_id}", headers=headers).json().get("data", [])
    comp_count = len(comp_res)
    print(f"Tender #{t_id} [{t.get('tenderRefNo')}]: {req_count} requirements, {comp_count} compliance items")

# Check opportunities pipeline
opps = requests.get(f"{base_url}/opportunities", headers=headers).json().get("data", [])
print(f"Opportunities count: {len(opps)}")
stages = {}
for o in opps:
    st = o.get("stage")
    stages[st] = stages.get(st, 0) + 1
    print(f"  - Opp #{o.get('id')}: Tender #{o.get('tender', {}).get('id')} ({o.get('tender', {}).get('tenderRefNo')}) -> Stage: {st}, Readiness: {o.get('readinessPercent')}%")

print("Stages summary:", stages)
