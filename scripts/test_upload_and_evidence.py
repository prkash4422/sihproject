import requests
import json
import io

base_url = "http://localhost:8080/api"

# Login as Founder
login_res = requests.post(f"{base_url}/auth/login", json={
    "email": "founder@aerodef.in",
    "password": "Password123!"
})
login_data = login_res.json().get("data", {})
token = login_data.get("token")
headers = {"Authorization": f"Bearer {token}"}

print(f"Logged in as: {login_data.get('email')}")

# 1. Test Document Upload
pdf_content = b"%PDF-1.4 test document content for procurepilot test"
files = {
    'file': ('test_iso_cert.pdf', io.BytesIO(pdf_content), 'application/pdf')
}
data = {
    'docType': 'ISO_CERTIFICATE'
}
upload_headers = {"Authorization": f"Bearer {token}"}

up_res = requests.post(f"{base_url}/startups/me/documents", headers=upload_headers, data=data, files=files)
print(f"Upload Status: {up_res.status_code}")
print(f"Upload Response: {up_res.text}")

# 2. Test Link Evidence
# Get compliance items for tender 1
comp_res = requests.get(f"{base_url}/compliance/tender/1", headers=headers).json().get("data", [])
print(f"Found {len(comp_res)} compliance items for tender 1")

if comp_res:
    item_id = comp_res[0]["id"]
    # Get user documents
    docs_res = requests.get(f"{base_url}/startups/me/documents", headers=headers).json().get("data", [])
    print(f"Found {len(docs_res)} startup documents")
    if docs_res:
        doc_id = docs_res[0]["id"]
        print(f"Linking compliance item #{item_id} with document #{doc_id}...")
        link_res = requests.post(f"{base_url}/compliance/items/{item_id}/evidence", headers=headers, json={"documentId": doc_id})
        print(f"Link Status: {link_res.status_code}")
        print(f"Link Response: {link_res.text}")
