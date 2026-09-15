import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { TenderSummary, Opportunity } from '../types';
import {
  Sparkles,
  TrendingUp,
  Clock,
  AlertCircle,
  FileCheck2,
  ArrowRight,
  ShieldCheck,
  CheckCircle2,
  AlertTriangle,
  XCircle,
  ExternalLink,
  ChevronRight
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tenders, setTenders] = useState<TenderSummary[]>([]);
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.getTenders(),
      api.getOpportunities().catch(() => [])
    ]).then(([tData, oppData]) => {
      setTenders(tData);
      setOpportunities(oppData);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  const urgentDeadlines = tenders.filter(t => (t.daysRemaining || 30) <= 14);

  const getTenderScore = (t: TenderSummary): number => {
    return t.matchScore != null ? t.matchScore : 0;
  };

  const sortedTenders = tenders.slice().sort((a, b) => getTenderScore(b) - getTenderScore(a));
  const maxMatchScore = tenders.length > 0 ? Math.max(...tenders.map(t => t.matchScore || 0)) : 0;
  const avgReadiness = opportunities.length > 0
    ? Math.round(opportunities.reduce((acc, o) => acc + (o.readinessPercent || 0), 0) / opportunities.length)
    : 0;

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '1.5rem 1rem', display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
      {/* Hero Welcome Banner */}
      <div className="glass-panel" style={{
        padding: '2rem',
        background: 'linear-gradient(135deg, rgba(30, 41, 59, 0.9) 0%, rgba(15, 23, 42, 0.95) 100%)',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div style={{
          position: 'absolute',
          right: '-40px',
          top: '-40px',
          width: '200px',
          height: '200px',
          background: 'radial-gradient(circle, rgba(99, 102, 241, 0.25) 0%, transparent 70%)',
          borderRadius: '50%',
          pointerEvents: 'none'
        }} />

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#06b6d4', fontSize: '0.8125rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              <Sparkles size={16} />
              DPIIT Startup Intelligence Portal
            </div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem' }}>
              Welcome back, {user?.fullName || 'Founder'}
            </h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginTop: '0.25rem', maxWidth: '600px' }}>
              You have <strong style={{ color: '#ffffff' }}>{tenders.length} active opportunities</strong> aligned with your registered capability fingerprint. 
              GFR Rule 161(iv) turnover & experience relaxations are active.
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <Link to="/tenders" className="btn btn-primary">
              Discover Tenders <ArrowRight size={16} />
            </Link>
          </div>
        </div>

        {/* Readiness Metric Ribbon */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '1rem',
          marginTop: '1.75rem',
          paddingTop: '1.5rem',
          borderTop: '1px solid var(--border-subtle)'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: 'var(--radius-md)', background: 'rgba(99, 102, 241, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#818cf8' }}>
              <TrendingUp size={22} />
            </div>
            <div>
              <div style={{ fontSize: '1.25rem', fontWeight: 800, color: maxMatchScore >= 75 ? '#34d399' : (maxMatchScore >= 50 ? '#fbbf24' : '#fb7185') }}>
                {maxMatchScore}% Match
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Top Opportunity Fit</div>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: 'var(--radius-md)', background: 'rgba(245, 158, 11, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fbbf24' }}>
              <Clock size={22} />
            </div>
            <div>
              <div style={{ fontSize: '1.25rem', fontWeight: 800 }}>{urgentDeadlines.length} Tenders</div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Closing in &lt;14 Days</div>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: 'var(--radius-md)', background: 'rgba(16, 185, 129, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#34d399' }}>
              <ShieldCheck size={22} />
            </div>
            <div>
              <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#34d399' }}>GFR 161(iv)</div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Exemption Status Active</div>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: 'var(--radius-md)', background: 'rgba(6, 182, 212, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#38bdf8' }}>
              <FileCheck2 size={22} />
            </div>
            <div>
              <div style={{ fontSize: '1.25rem', fontWeight: 800, color: avgReadiness >= 75 ? '#34d399' : (avgReadiness >= 50 ? '#fbbf24' : '#fb7185') }}>
                {avgReadiness}% Readiness
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Compliance Vault Avg</div>
            </div>
          </div>
        </div>
      </div>

      {/* Grid: High-Fit Recommended Opportunities & Urgent Actions */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '1.5rem' }}>
        {/* Recommended High-Fit Tenders */}
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Sparkles size={18} color="#06b6d4" />
              High-Fit Opportunities
            </h3>
            <Link to="/tenders" style={{ fontSize: '0.75rem', color: '#06b6d4', textDecoration: 'none', fontWeight: 600 }}>
              View all ({tenders.length})
            </Link>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem' }}>
            {sortedTenders.slice(0, 3).map((tender) => {
              const score = getTenderScore(tender);
              const isPass = score >= 75;
              const isReview = score >= 50 && score < 75;

              return (
                <div
                  key={tender.id}
                  onClick={() => navigate(`/tenders/${tender.id}`)}
                  style={{
                    padding: '1rem',
                    borderRadius: 'var(--radius-md)',
                    background: 'rgba(255, 255, 255, 0.03)',
                    border: '1px solid var(--border-subtle)',
                    borderLeft: isPass ? '4px solid #10b981' : (isReview ? '4px solid #f59e0b' : '4px solid #ef4444'),
                    cursor: 'pointer',
                    transition: 'var(--transition)',
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.borderColor = 'rgba(99, 102, 241, 0.4)')}
                  onMouseLeave={(e) => (e.currentTarget.style.borderColor = 'var(--border-subtle)')}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '0.5rem' }}>
                    <span className="badge badge-info" style={{ fontSize: '0.65rem' }}>{tender.category}</span>
                    {isPass && (
                      <span className="badge badge-pass" style={{ fontSize: '0.7rem' }}>
                        <CheckCircle2 size={11} /> {score}% MATCH (PASS)
                      </span>
                    )}
                    {isReview && (
                      <span className="badge badge-review" style={{ fontSize: '0.7rem' }}>
                        <AlertTriangle size={11} /> {score}% MATCH (REVIEW)
                      </span>
                    )}
                    {!isPass && !isReview && (
                      <span className="badge badge-fail" style={{ fontSize: '0.7rem' }}>
                        <XCircle size={11} /> {score}% MATCH (FAIL)
                      </span>
                    )}
                  </div>

                  <h4 style={{ fontSize: '0.875rem', fontWeight: 600, marginTop: '0.5rem', lineHeight: '1.4' }}>
                    {tender.title}
                  </h4>

                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.75rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    <span>{tender.department}</span>
                    <span style={{ color: '#f59e0b', fontWeight: 600 }}>
                      Closes in {tender.daysRemaining || 14} days
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Action Center / Recommended Next Actions */}
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <AlertCircle size={18} color="#f59e0b" />
            Recommended Next Actions
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem' }}>
            <div style={{
              padding: '1rem',
              borderRadius: 'var(--radius-md)',
              background: 'rgba(245, 158, 11, 0.05)',
              border: '1px solid rgba(245, 158, 11, 0.2)',
              display: 'flex',
              gap: '0.75rem'
            }}>
              <div style={{ color: '#f59e0b', marginTop: '2px' }}>
                <AlertCircle size={18} />
              </div>
              <div>
                <strong style={{ fontSize: '0.8125rem', color: '#ffffff' }}>Upload Make-in-India Self Declaration</strong>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                  Tender GEM/2026/B/892301 requires Class-I local content (&gt;=50%) certificate to complete compliance.
                </p>
                <Link to="/tenders/1" style={{ fontSize: '0.75rem', color: '#06b6d4', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: '0.25rem', marginTop: '0.5rem', textDecoration: 'none' }}>
                  Open Compliance Workspace <ChevronRight size={14} />
                </Link>
              </div>
            </div>

            <div style={{
              padding: '1rem',
              borderRadius: 'var(--radius-md)',
              background: 'rgba(16, 185, 129, 0.05)',
              border: '1px solid rgba(16, 185, 129, 0.2)',
              display: 'flex',
              gap: '0.75rem'
            }}>
              <div style={{ color: '#10b981', marginTop: '2px' }}>
                <CheckCircle2 size={18} />
              </div>
              <div>
                <strong style={{ fontSize: '0.8125rem', color: '#ffffff' }}>DPIIT Exemption Verified</strong>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                  GFR 161(iv) exemption applied for Defence Surveillance AI tender. ₹3,00,000 EMD saved.
                </p>
              </div>
            </div>

            <div style={{
              padding: '1rem',
              borderRadius: 'var(--radius-md)',
              background: 'rgba(99, 102, 241, 0.05)',
              border: '1px solid rgba(99, 102, 241, 0.2)',
              display: 'flex',
              gap: '0.75rem'
            }}>
              <div style={{ color: '#818cf8', marginTop: '2px' }}>
                <ShieldCheck size={18} />
              </div>
              <div>
                <strong style={{ fontSize: '0.8125rem', color: '#ffffff' }}>Grounded AI Tender Q&A Ready</strong>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                  Ask natural language questions regarding contract clauses with verified page citations.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
