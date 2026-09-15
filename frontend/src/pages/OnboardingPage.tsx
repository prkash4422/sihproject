import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { StartupProfile, Capability, Certification } from '../types';
import confetti from 'canvas-confetti';
import {
  Building2,
  Award,
  Layers,
  Cpu,
  Calendar,
  IndianRupee,
  ShieldCheck,
  MapPin,
  UploadCloud,
  FileCheck,
  CheckCircle2,
  ArrowRight,
  ArrowLeft,
  Plus,
  Trash2,
  Sparkles
} from 'lucide-react';

const STEPS = [
  { id: 1, title: 'Company Basics', icon: Building2 },
  { id: 2, title: 'Startup Recognition', icon: Award },
  { id: 3, title: 'Industry & Sector', icon: Layers },
  { id: 4, title: 'Core Products', icon: Layers },
  { id: 5, title: 'Technical Capabilities', icon: Cpu },
  { id: 6, title: 'Operational Experience', icon: Calendar },
  { id: 7, title: 'Financial Profile', icon: IndianRupee },
  { id: 8, title: 'Quality Certifications', icon: ShieldCheck },
  { id: 9, title: 'Geography & State', icon: MapPin },
  { id: 10, title: 'Upload Evidence', icon: UploadCloud },
  { id: 11, title: 'Profile Review', icon: FileCheck },
  { id: 12, title: 'Finish & Launch', icon: CheckCircle2 },
];

export const OnboardingPage: React.FC = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [saveMessage, setSaveMessage] = useState<string | null>(null);

  const [profile, setProfile] = useState<StartupProfile>({
    companyName: 'AeroDef AI Technologies Pvt Ltd',
    legalName: 'AeroDef Artificial Intelligence Technologies Private Limited',
    dpiitRecognized: true,
    dpiitNumber: 'DIPP98741',
    udyamNumber: 'UDYAM-DL-01-0023412',
    incorporationDate: '2023-03-15',
    annualTurnoverInr: 18000000, // 1.8 Cr
    netWorthInr: 25000000,
    primarySector: 'Defense, AI & Computer Vision',
    state: 'Delhi',
    city: 'New Delhi',
    website: 'https://aerodef.in',
    capabilities: [
      { category: 'Artificial Intelligence', name: 'Edge AI Video Analytics & Object Detection', proficiencyLevel: 'EXPERT', description: 'Real-time YOLO/TensorRT inference' },
      { category: 'Unmanned Systems', name: 'Autonomous Drone Navigation & Telemetry', proficiencyLevel: 'ADVANCED', description: 'MAVLink & ROS2 autopilot controllers' },
    ],
    certifications: [
      { certType: 'ISO 9001:2015', certNumber: 'ISO-QMS-2023-9912', issuingBody: 'Bureau Veritas', issueDate: '2023-06-01', expiryDate: '2026-05-31' },
      { certType: 'DPIIT Startup Certificate', certNumber: 'DIPP98741', issuingBody: 'DPIIT', issueDate: '2023-04-10', expiryDate: '2033-04-09' }
    ],
  });

  const [newCap, setNewCap] = useState<Capability>({ category: 'Software', name: '', proficiencyLevel: 'ADVANCED', description: '' });
  const [newCert, setNewCert] = useState<Certification>({ certType: 'ISO 9001:2015', certNumber: '', issuingBody: '', issueDate: '', expiryDate: '' });

  useEffect(() => {
    api.getProfile()
      .then((data) => {
        if (data && data.companyName) {
          setProfile(data);
        }
      })
      .catch(() => {});
  }, []);

  const handleSaveAndResume = async () => {
    setLoading(true);
    try {
      await api.updateProfile(profile);
      setSaveMessage('Progress saved successfully!');
      setTimeout(() => setSaveMessage(null), 3000);
    } catch (err: any) {
      setSaveMessage('Failed to save: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleNext = async () => {
    if (currentStep < 12) {
      setCurrentStep(currentStep + 1);
    } else {
      // Final step -> Save and redirect
      setLoading(true);
      await api.updateProfile(profile);
      confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });
      setTimeout(() => navigate('/dashboard'), 1000);
    }
  };

  const handleAddCapability = () => {
    if (newCap.name.trim()) {
      setProfile({ ...profile, capabilities: [...profile.capabilities, newCap] });
      setNewCap({ category: 'Software', name: '', proficiencyLevel: 'ADVANCED', description: '' });
    }
  };

  const handleRemoveCapability = (index: number) => {
    const updated = [...profile.capabilities];
    updated.splice(index, 1);
    setProfile({ ...profile, capabilities: updated });
  };

  const handleAddCert = () => {
    if (newCert.certType.trim()) {
      setProfile({ ...profile, certifications: [...profile.certifications, newCert] });
      setNewCert({ certType: 'ISO 9001:2015', certNumber: '', issuingBody: '', issueDate: '', expiryDate: '' });
    }
  };

  const handleRemoveCert = (index: number) => {
    const updated = [...profile.certifications];
    updated.splice(index, 1);
    setProfile({ ...profile, certifications: updated });
  };

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '0.5rem 0', width: '100%' }}>
      {/* Step Indicator Header */}
      <div className="glass-panel" style={{ padding: '1.25rem', marginBottom: '1.25rem', width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '0.5rem' }}>
          <div>
            <span style={{ fontSize: '0.75rem', fontWeight: 700, color: '#06b6d4', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              STEP {currentStep} OF 12
            </span>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 800 }}>{STEPS[currentStep - 1].title}</h2>
          </div>
          <button onClick={handleSaveAndResume} className="btn btn-secondary" style={{ fontSize: '0.75rem', padding: '0.4rem 0.75rem' }}>
            Save & Resume Later
          </button>
        </div>

        {/* Progress Bar */}
        <div style={{ width: '100%', height: '6px', background: 'rgba(255, 255, 255, 0.1)', borderRadius: '3px', overflow: 'hidden' }}>
          <div style={{
            width: `${(currentStep / 12) * 100}%`,
            height: '100%',
            background: 'linear-gradient(90deg, #6366f1 0%, #06b6d4 100%)',
            transition: 'width 0.3s ease',
          }} />
        </div>

        {saveMessage && (
          <div style={{ fontSize: '0.75rem', color: '#34d399', marginTop: '0.5rem', fontWeight: 600 }}>
            {saveMessage}
          </div>
        )}
      </div>

      {/* Main Form Content Card */}
      <div className="glass-panel" style={{ padding: '1.25rem', minHeight: '380px', display: 'flex', flexDirection: 'column', width: '100%' }}>
        {/* Step 1: Basics */}
        {currentStep === 1 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Enter your registered business identity as recognized on official records.
            </p>
            <div>
              <label className="form-label">Brand / Operating Name</label>
              <input
                type="text"
                className="form-input"
                value={profile.companyName}
                onChange={(e) => setProfile({ ...profile, companyName: e.target.value })}
              />
            </div>
            <div>
              <label className="form-label">Full Legal Entity Name (as per MCA / GST)</label>
              <input
                type="text"
                className="form-input"
                value={profile.legalName || ''}
                onChange={(e) => setProfile({ ...profile, legalName: e.target.value })}
              />
            </div>
            <div>
              <label className="form-label">Company Website</label>
              <input
                type="url"
                className="form-input"
                value={profile.website || ''}
                onChange={(e) => setProfile({ ...profile, website: e.target.value })}
              />
            </div>
          </div>
        )}

        {/* Step 2: Recognition */}
        {currentStep === 2 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div style={{
              padding: '1rem',
              borderRadius: 'var(--radius-md)',
              background: 'rgba(6, 182, 212, 0.08)',
              border: '1px solid rgba(6, 182, 212, 0.25)',
            }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#06b6d4', fontWeight: 700 }}>
                <Sparkles size={16} />
                DPIIT Startup Recognition Benefits
              </div>
              <p style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)', marginTop: '0.35rem' }}>
                Recognition under DPIIT entitles your startup to 100% exemption from Prior Turnover & Prior Experience clauses per GFR 161(iv) and EMD exemptions.
              </p>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <input
                type="checkbox"
                id="dpiitCheck"
                checked={profile.dpiitRecognized}
                onChange={(e) => setProfile({ ...profile, dpiitRecognized: e.target.checked })}
                style={{ width: '18px', height: '18px', accentColor: '#6366f1', cursor: 'pointer' }}
              />
              <label htmlFor="dpiitCheck" style={{ fontWeight: 600, fontSize: '0.875rem', cursor: 'pointer' }}>
                My company is recognized as a Startup by DPIIT (Startup India)
              </label>
            </div>

            {profile.dpiitRecognized && (
              <div>
                <label className="form-label">DPIIT Recognition Number (DIPP Number)</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. DIPP98741"
                  value={profile.dpiitNumber || ''}
                  onChange={(e) => setProfile({ ...profile, dpiitNumber: e.target.value })}
                />
              </div>
            )}

            <div>
              <label className="form-label">Udyam MSME Registration Number (Optional)</label>
              <input
                type="text"
                className="form-input"
                placeholder="e.g. UDYAM-DL-01-0023412"
                value={profile.udyamNumber || ''}
                onChange={(e) => setProfile({ ...profile, udyamNumber: e.target.value })}
              />
            </div>
          </div>
        )}

        {/* Step 3: Industry & Sector */}
        {currentStep === 3 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Select your primary sector to help our matching engine align domain-specific tenders.
            </p>
            <div>
              <label className="form-label">Primary Sector / Domain</label>
              <select
                className="form-input"
                value={profile.primarySector}
                onChange={(e) => setProfile({ ...profile, primarySector: e.target.value })}
              >
                <option value="Defense, AI & Computer Vision">Defense, AI & Computer Vision</option>
                <option value="Healthcare & Medical Devices">Healthcare & Medical Devices</option>
                <option value="Smart Cities & IoT Hardware">Smart Cities & IoT Hardware</option>
                <option value="Renewable Energy & Cleantech">Renewable Energy & Cleantech</option>
                <option value="Cybersecurity & Cloud Infrastructure">Cybersecurity & Cloud Infrastructure</option>
                <option value="Civil Infrastructure & Heavy Engineering">Civil Infrastructure & Heavy Engineering</option>
              </select>
            </div>
          </div>
        )}

        {/* Step 4: Core Products */}
        {currentStep === 4 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Describe your core product offerings for public procurement tenders.
            </p>
            <div>
              <label className="form-label">Product Portfolio Summary</label>
              <textarea
                className="form-input"
                rows={4}
                placeholder="e.g. Autonomous AI-powered surveillance drones, edge compute video analytics gateway, command & control software suite."
                defaultValue="Autonomous AI-powered perimeter surveillance drones with edge computing video inference units for real-time target recognition."
              />
            </div>
          </div>
        )}

        {/* Step 5: Capabilities */}
        {currentStep === 5 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Add specific technical capabilities. The matching engine compares these against tender technical specifications.
            </p>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(min(100%, 150px), 1fr))', gap: '0.75rem', alignItems: 'flex-end' }}>
              <div>
                <label className="form-label">Category</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="AI / Hardware"
                  value={newCap.category}
                  onChange={(e) => setNewCap({ ...newCap, category: e.target.value })}
                />
              </div>
              <div>
                <label className="form-label">Capability Name</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. Edge Video Analytics"
                  value={newCap.name}
                  onChange={(e) => setNewCap({ ...newCap, name: e.target.value })}
                />
              </div>
              <div>
                <label className="form-label">Proficiency</label>
                <select
                  className="form-input"
                  value={newCap.proficiencyLevel}
                  onChange={(e) => setNewCap({ ...newCap, proficiencyLevel: e.target.value })}
                >
                  <option value="INTERMEDIATE">INTERMEDIATE</option>
                  <option value="ADVANCED">ADVANCED</option>
                  <option value="EXPERT">EXPERT</option>
                </select>
              </div>
              <button type="button" onClick={handleAddCapability} className="btn btn-primary" style={{ padding: '0.625rem 0.875rem', height: '2.5rem' }}>
                <Plus size={16} /> Add
              </button>
            </div>

            <div style={{ marginTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {profile.capabilities.map((cap, idx) => (
                <div key={idx} style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '0.75rem',
                  borderRadius: 'var(--radius-sm)',
                  background: 'rgba(255, 255, 255, 0.03)',
                  border: '1px solid var(--border-subtle)',
                  gap: '0.5rem'
                }}>
                  <div style={{ minWidth: 0, flex: 1 }}>
                    <strong style={{ fontSize: '0.875rem', display: 'block' }} className="text-break">{cap.name}</strong>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      {cap.category} • <span style={{ color: '#06b6d4' }}>{cap.proficiencyLevel}</span>
                    </div>
                  </div>
                  <button onClick={() => handleRemoveCapability(idx)} className="btn btn-secondary" style={{ padding: '0.35rem', color: '#fb7185', flexShrink: 0 }}>
                    <Trash2 size={14} />
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Step 6: Experience */}
        {currentStep === 6 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Enter your date of incorporation to calculate operational years.
            </p>
            <div>
              <label className="form-label">Date of Incorporation</label>
              <input
                type="date"
                className="form-input"
                value={profile.incorporationDate || ''}
                onChange={(e) => setProfile({ ...profile, incorporationDate: e.target.value })}
              />
            </div>
          </div>
        )}

        {/* Step 7: Financials */}
        {currentStep === 7 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Enter your average annual turnover and net worth in Indian Rupees (INR).
            </p>
            <div>
              <label className="form-label">Average Annual Turnover (INR)</label>
              <input
                type="number"
                className="form-input"
                value={profile.annualTurnoverInr}
                onChange={(e) => setProfile({ ...profile, annualTurnoverInr: parseFloat(e.target.value) || 0 })}
              />
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block', marginTop: '0.25rem' }}>
                ₹{(profile.annualTurnoverInr / 10000000).toFixed(2)} Crore (₹{(profile.annualTurnoverInr / 100000).toFixed(2)} Lakh)
              </span>
            </div>
            <div>
              <label className="form-label">Positive Net Worth (INR)</label>
              <input
                type="number"
                className="form-input"
                value={profile.netWorthInr || 0}
                onChange={(e) => setProfile({ ...profile, netWorthInr: parseFloat(e.target.value) || 0 })}
              />
            </div>
          </div>
        )}

        {/* Step 8: Certifications */}
        {currentStep === 8 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Add ISO, CMMI, BIS or statutory quality certifications.
            </p>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(min(100%, 150px), 1fr))', gap: '0.75rem', alignItems: 'flex-end' }}>
              <div>
                <label className="form-label">Certification Type</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. ISO 9001:2015"
                  value={newCert.certType}
                  onChange={(e) => setNewCert({ ...newCert, certType: e.target.value })}
                />
              </div>
              <div>
                <label className="form-label">Certificate No.</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. ISO-QMS-9912"
                  value={newCert.certNumber || ''}
                  onChange={(e) => setNewCert({ ...newCert, certNumber: e.target.value })}
                />
              </div>
              <div>
                <label className="form-label">Issuing Body</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. Bureau Veritas"
                  value={newCert.issuingBody || ''}
                  onChange={(e) => setNewCert({ ...newCert, issuingBody: e.target.value })}
                />
              </div>
              <button type="button" onClick={handleAddCert} className="btn btn-primary" style={{ padding: '0.625rem 0.875rem', height: '2.5rem' }}>
                <Plus size={16} /> Add
              </button>
            </div>

            <div style={{ marginTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {profile.certifications.map((cert, idx) => (
                <div key={idx} style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '0.75rem',
                  borderRadius: 'var(--radius-sm)',
                  background: 'rgba(255, 255, 255, 0.03)',
                  border: '1px solid var(--border-subtle)',
                  gap: '0.5rem'
                }}>
                  <div style={{ minWidth: 0, flex: 1 }}>
                    <strong style={{ fontSize: '0.875rem', display: 'block' }} className="text-break">{cert.certType}</strong>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }} className="text-break">
                      Issued by: {cert.issuingBody || 'Accredited Registrar'} • No: {cert.certNumber || 'N/A'}
                    </div>
                  </div>
                  <button onClick={() => handleRemoveCert(idx)} className="btn btn-secondary" style={{ padding: '0.35rem', color: '#fb7185', flexShrink: 0 }}>
                    <Trash2 size={14} />
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Step 9: Geography */}
        {currentStep === 9 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Registered location for state/local public procurement quota evaluation.
            </p>
            <div>
              <label className="form-label">State / Union Territory</label>
              <input
                type="text"
                className="form-input"
                value={profile.state || ''}
                onChange={(e) => setProfile({ ...profile, state: e.target.value })}
              />
            </div>
            <div>
              <label className="form-label">City / District</label>
              <input
                type="text"
                className="form-input"
                value={profile.city || ''}
                onChange={(e) => setProfile({ ...profile, city: e.target.value })}
              />
            </div>
          </div>
        )}

        {/* Step 10: Evidence Upload */}
        {currentStep === 10 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Upload statutory and technical evidence files. All documents are stored in private encrypted storage.
            </p>

            <div style={{
              border: '2px dashed var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
              padding: '2rem',
              textAlign: 'center',
              background: 'rgba(255, 255, 255, 0.02)'
            }}>
              <UploadCloud size={36} color="#6366f1" style={{ margin: '0 auto 0.5rem' }} />
              <div style={{ fontSize: '0.875rem', fontWeight: 600 }}>DPIIT Certificate, PAN, GST & Audited Financials</div>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                PDF format up to 10MB per document
              </p>
              <div style={{ marginTop: '1rem', display: 'flex', justifyContent: 'center', gap: '0.5rem' }}>
                <span className="badge badge-pass">✓ DPIIT_Cert.pdf (Verified)</span>
                <span className="badge badge-pass">✓ GST_07AABCA1234F.pdf</span>
                <span className="badge badge-pass">✓ ISO_9001.pdf</span>
              </div>
            </div>
          </div>
        )}

        {/* Step 11: Review Profile */}
        {currentStep === 11 && (
          <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <h3 style={{ fontSize: '1.1rem' }}>Capability Fingerprint Summary</h3>
            <div style={{
              padding: '1rem',
              borderRadius: 'var(--radius-md)',
              background: 'rgba(99, 102, 241, 0.08)',
              border: '1px solid rgba(99, 102, 241, 0.2)',
              fontSize: '0.8125rem',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.5rem'
            }}>
              <div><strong>Company:</strong> {profile.companyName}</div>
              <div><strong>DPIIT Recognized:</strong> {profile.dpiitRecognized ? `YES (${profile.dpiitNumber}) - GFR 161(iv) Active` : 'NO'}</div>
              <div><strong>Annual Turnover:</strong> ₹{(profile.annualTurnoverInr / 10000000).toFixed(2)} Cr</div>
              <div><strong>Registered Capabilities:</strong> {profile.capabilities.length} items</div>
              <div><strong>Quality Certifications:</strong> {profile.certifications.length} certificates</div>
            </div>
          </div>
        )}

        {/* Step 12: Finish */}
        {currentStep === 12 && (
          <div className="animate-fade-in" style={{ textAlign: 'center', padding: '2rem 1rem' }}>
            <div style={{
              width: '4rem',
              height: '4rem',
              borderRadius: '50%',
              background: 'rgba(16, 185, 129, 0.2)',
              color: '#10b981',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: '1rem',
            }}>
              <CheckCircle2 size={36} />
            </div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Profile Onboarding Complete!</h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', maxWidth: '480px', margin: '0.5rem auto 1.5rem' }}>
              Your startup capability fingerprint has been established. You are ready to discover and analyse high-match public tenders with GFR 161(iv) relaxation protection.
            </p>
          </div>
        )}

        {/* Footer Navigation Controls */}
        <div style={{
          marginTop: 'auto',
          paddingTop: '1.5rem',
          borderTop: '1px solid var(--border-subtle)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          {currentStep > 1 ? (
            <button
              type="button"
              onClick={() => setCurrentStep(currentStep - 1)}
              className="btn btn-secondary"
            >
              <ArrowLeft size={16} /> Back
            </button>
          ) : <div />}

          <button
            type="button"
            onClick={handleNext}
            className="btn btn-primary"
            disabled={loading}
          >
            {currentStep === 12 ? 'Launch Procurement Dashboard' : 'Save & Continue'}
            <ArrowRight size={16} />
          </button>
        </div>
      </div>
    </div>
  );
};
