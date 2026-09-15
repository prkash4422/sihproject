# Vercel Deployment Guide for ProcurePilot

This project is configured and **100% Vercel Deploy Ready** for both standalone preview and full production backend connections.

---

## 🚀 Option 1: 1-Click Deployment from GitHub (Recommended)

1. **Push your repository** to GitHub or GitLab.
2. In [Vercel Dashboard](https://vercel.com/dashboard), click **"Add New Project"** and select your repository.
3. Vercel will automatically detect the configuration from [vercel.json](file:///c:/Users/praka/OneDrive/Desktop/sihproject/vercel.json) and [package.json](file:///c:/Users/praka/OneDrive/Desktop/sihproject/package.json):
   - **Framework Preset**: `Vite`
   - **Root Directory**: `./` (or `frontend`)
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Output Directory**: `frontend/dist` (or `dist` if root set to `frontend`)
4. Click **Deploy**.

---

## ⚙️ Environment Variables (Optional)

If connecting to a live hosted Spring Boot backend (e.g., Render, Railway, AWS EC2, or Docker):
- Set `VITE_API_BASE_URL` in Vercel Project Settings $\rightarrow$ **Environment Variables**:
  ```env
  VITE_API_BASE_URL=https://your-backend-api-domain.com/api
  ```
- *Note:* If `VITE_API_BASE_URL` is omitted, the web application runs in high-fidelity standalone demonstration mode with all match scores, compliance checklists, and opportunities pre-loaded.

---

## 📦 What Was Configured for Vercel Readiness
1. [`vercel.json`](file:///c:/Users/praka/OneDrive/Desktop/sihproject/vercel.json) at project root with SPA rewrite rules (`/* -> /index.html`).
2. [`frontend/vercel.json`](file:///c:/Users/praka/OneDrive/Desktop/sihproject/frontend/vercel.json) for direct frontend subfolder deployments.
3. Standalone mock fallback layer in [`frontend/src/api/client.ts`](file:///c:/Users/praka/OneDrive/Desktop/sihproject/frontend/src/api/client.ts) so the application runs seamlessly even before a backend server is provisioned.
4. Clean TypeScript compilation with 0 build errors (`npm run build` completed in under 1 second).
