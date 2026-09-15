import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { StartupProfile, StartupDocument } from '../types';
import {
  Building2,
  Award,
  ShieldCheck,
  Upload,
  FileText,
  CheckCircle2,
  Calendar,
  IndianRupee,
  MapPin,
  Sparkles
} from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const [profile, setProfile] = useState<StartupProfile | null>(null);
  const [documents, setDocuments] = useState<StartupDocument[]>([]);
  const [uploadType, setUploadType] = useState('PAN_CARD');
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [loading, setLoading] = useState(true);

  const [statusMessage, setStatusMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const p = await api.getProfile();
      setProfile(p);
      setDocuments(p.documents || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!uploadFile) {
      setStatusMessage({ type: 'error', text: 'Please select a PDF file to upload.' });
      return;
    }

    setUploading(true);
    setStatusMessage(null);
    try {
      await api.uploadDocument(uploadType, uploadFile);
      setUploadFile(null);
      setStatusMessage({ type: 'success', text: `Document "${uploadFile.name}" successfully uploaded and verified in vault!` });
      await loadData();
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: 'Upload failed: ' + err.message });
    } finally {
      setUploading(false);
    }
  };

  const handleUseSampleFile = (docTypeName: string, sampleFileName: string) => {
    // Generate a valid mock PDF blob
    const sampleBlob = new Blob(["%PDF-1.4 Mock Government Certificate for " + sampleFileName], { type: 'application/pdf' });
    const file = new File([sampleBlob], sampleFileName, { type: 'application/pdf' });
    setUploadFile(file);
    setUploadType(docTypeName);
    setStatusMessage(null);
  };

  if (loading || !profile) {
    return (
      <div style={{ maxWidth: '1000px', margin: '2rem auto', textAlign: 'center', color: 'var(--text-muted)' }}>
        Loading Startup Profile...
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '1.5rem 1rem', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Header Profile Card */}
      <div className="glass-panel" style={{ padding: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span className="badge badge-info">{profile.primarySector}</span>
            {profile.dpiitRecognized && (
              <span className="badge badge-pass">
                <Sparkles size={12} /> DPIIT RECOGNIZED STARTUP ({profile.dpiitNumber})
              </span>
            )}
          </div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.5rem' }}>
            {profile.companyName}
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginTop: '0.2rem' }}>
            {profile.legalName || profile.companyName} • {profile.city}, {profile.state}
          </p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem' }}>
        {/* Capability Fingerprint */}
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Capability & Sector Fingerprint</h3>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
            Fingerprint keywords extracted for automated procurement matchmaking.
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {profile.capabilities?.map((cap, idx) => (
              <div key={idx} style={{ padding: '0.75rem', borderRadius: 'var(--radius-sm)', background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--border-subtle)' }}>
                <strong style={{ fontSize: '0.875rem' }}>{cap.name}</strong>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  {cap.category} • <span style={{ color: '#06b6d4' }}>{cap.proficiencyLevel}</span>
                </div>
              </div>
            ))}
          </div>

          <h4 style={{ fontSize: '0.9rem', fontWeight: 700, marginTop: '0.5rem' }}>Quality Certifications</h4>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {profile.certifications?.map((cert, idx) => (
              <div key={idx} style={{ padding: '0.75rem', borderRadius: 'var(--radius-sm)', background: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.2)' }}>
                <strong style={{ fontSize: '0.875rem', color: '#34d399' }}>{cert.certType}</strong>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Issued by: {cert.issuingBody || 'Accredited Body'} • No: {cert.certNumber || 'Active'}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Document Vault */}
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Encrypted Evidence Vault</h3>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
            Uploaded files used for compliance checklist verification and GFR 161(iv) exemptions.
          </p>

          {statusMessage && (
            <div style={{
              padding: '0.75rem 1rem',
              borderRadius: 'var(--radius-sm)',
              fontSize: '0.8125rem',
              background: statusMessage.type === 'success' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(244, 63, 94, 0.1)',
              border: '1px solid ' + (statusMessage.type === 'success' ? 'rgba(16, 185, 129, 0.3)' : 'rgba(244, 63, 94, 0.3)'),
              color: statusMessage.type === 'success' ? '#34d399' : '#fb7185',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <span>{statusMessage.text}</span>
              <button onClick={() => setStatusMessage(null)} style={{ background: 'transparent', border: 'none', color: 'inherit', cursor: 'pointer' }}>✕</button>
            </div>
          )}

          {/* Upload Form */}
          <form onSubmit={handleFileUpload} style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', padding: '1rem', background: 'rgba(0, 0, 0, 0.2)', borderRadius: 'var(--radius-md)' }}>
            <div>
              <label className="form-label">Document Classification</label>
              <select className="form-input" value={uploadType} onChange={(e) => setUploadType(e.target.value)}>
                <option value="PAN_CARD">Company PAN Card</option>
                <option value="GST_CERTIFICATE">GST Registration Certificate</option>
                <option value="DPIIT_CERTIFICATE">DPIIT Startup Recognition Certificate</option>
                <option value="AUDITED_FINANCIALS">Audited Balance Sheet / P&L</option>
                <option value="ISO_CERTIFICATE">ISO 9001 / Quality Certification</option>
                <option value="TECHNICAL_SPEC">Technical Specification / Test Report</option>
              </select>
            </div>

            <div>
              <label className="form-label">Select File (PDF)</label>
              <input
                id="file-upload-input"
                type="file"
                accept=".pdf,application/pdf"
                className="form-input"
                onChange={(e) => {
                  if (e.target.files && e.target.files[0]) {
                    setUploadFile(e.target.files[0]);
                    setStatusMessage(null);
                  }
                }}
              />
              {uploadFile && (
                <div style={{ marginTop: '0.35rem', fontSize: '0.75rem', color: '#34d399', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                  <CheckCircle2 size={13} /> Selected: <strong>{uploadFile.name}</strong> ({(uploadFile.size / 1024).toFixed(1)} KB)
                </div>
              )}
            </div>

            {/* Quick Demo Templates */}
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginTop: '0.25rem' }}>
              <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)', alignSelf: 'center' }}>Quick demo files:</span>
              <button
                type="button"
                onClick={() => handleUseSampleFile('ISO_CERTIFICATE', 'ISO_9001_QMS_Certificate.pdf')}
                className="btn btn-secondary"
                style={{ fontSize: '0.7rem', padding: '0.25rem 0.5rem' }}
              >
                + Sample ISO Certificate
              </button>
              <button
                type="button"
                onClick={() => handleUseSampleFile('TECHNICAL_SPEC', 'Edge_AI_Benchmark_Report.pdf')}
                className="btn btn-secondary"
                style={{ fontSize: '0.7rem', padding: '0.25rem 0.5rem' }}
              >
                + Sample Benchmark PDF
              </button>
            </div>

            <button
              type="submit"
              disabled={!uploadFile || uploading}
              className="btn btn-primary animate-glow"
              style={{ fontSize: '0.8125rem', marginTop: '0.5rem' }}
            >
              <Upload size={14} /> {uploading ? 'Uploading...' : 'Upload to Secure Vault'}
            </button>
          </form>

          {/* Uploaded List */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <h4 style={{ fontSize: '0.8125rem', color: 'var(--text-muted)', textTransform: 'uppercase', marginTop: '0.5rem' }}>
              Current Verified Documents ({documents.length})
            </h4>
            {documents.map((doc) => (
              <div key={doc.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', borderRadius: 'var(--radius-sm)', background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--border-subtle)' }}>
                <div>
                  <strong style={{ fontSize: '0.8125rem' }}>{doc.fileName}</strong>
                  <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>{doc.docType}</div>
                </div>
                <span className="badge badge-pass" style={{ fontSize: '0.65rem' }}>Verified</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
