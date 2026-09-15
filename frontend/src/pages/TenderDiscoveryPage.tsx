import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { TenderSummary } from '../types';
import {
  Search,
  Filter,
  Building,
  Calendar,
  IndianRupee,
  ExternalLink,
  ShieldCheck,
  Sparkles,
  ArrowRight,
  Layers,
  CheckCircle2,
  AlertTriangle,
  XCircle
} from 'lucide-react';

export const TenderDiscoveryPage: React.FC = () => {
  const navigate = useNavigate();
  const [tenders, setTenders] = useState<TenderSummary[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTenders();
  }, [selectedCategory]);

  const fetchTenders = () => {
    setLoading(true);
    api.getTenders({
      query: searchQuery || undefined,
      category: selectedCategory || undefined
    }).then(data => {
      setTenders(data);
      setLoading(false);
    }).catch(() => setLoading(false));
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchTenders();
  };

  const formatInr = (amount?: number) => {
    if (!amount) return 'N/A';
    if (amount >= 10000000) return `₹${(amount / 10000000).toFixed(2)} Cr`;
    if (amount >= 100000) return `₹${(amount / 100000).toFixed(2)} Lakh`;
    return `₹${amount.toLocaleString('en-IN')}`;
  };

  const getTenderScore = (t: TenderSummary): number => {
    return t.matchScore != null ? t.matchScore : 0;
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '1.5rem 1rem', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Header & Search Bar */}
      <div>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Tender Discovery & Intelligence Feed</h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginTop: '0.25rem' }}>
          Discover public procurement opportunities matched to your startup's technical capabilities and legal exemptions.
        </p>

        {/* Search & Filter Controls */}
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '0.75rem', marginTop: '1.25rem', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 240px', position: 'relative' }}>
            <input
              type="text"
              className="form-input"
              placeholder="Search by keywords, tender ID, department..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ paddingLeft: '2.5rem' }}
            />
            <Search size={18} color="var(--text-muted)" style={{ position: 'absolute', left: '0.875rem', top: '50%', transform: 'translateY(-50%)' }} />
          </div>

          <select
            className="form-input"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            style={{ flex: '1 1 200px', minWidth: '180px' }}
          >
            <option value="">All Categories</option>
            <option value="Defence & Surveillance AI">Defence & Surveillance AI</option>
            <option value="Healthcare & Medical IoT">Healthcare & Medical IoT</option>
            <option value="Heavy Civil Infrastructure">Heavy Civil Infrastructure</option>
            <option value="Space & Embedded Telemetry">Space & Embedded Telemetry</option>
            <option value="Defence Electronics & Rugged AI">Defence Electronics & Rugged AI</option>
          </select>

          <button type="submit" className="btn btn-primary">
            <Search size={16} /> Search
          </button>
        </form>
      </div>

      {/* Tender Results List */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem 0', color: 'var(--text-muted)' }}>
          Loading procurement tenders...
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {tenders.map((tender) => {
            const score = getTenderScore(tender);
            const isPass = score >= 75;
            const isReview = score >= 50 && score < 75;

            return (
              <div
                key={tender.id}
                className="glass-panel"
                style={{
                  padding: '1.5rem',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '1rem',
                  borderLeft: isPass ? '4px solid #10b981' : (isReview ? '4px solid #f59e0b' : '4px solid #ef4444'),
                  transition: 'var(--transition)',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '0.75rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
                    <span className="badge badge-info">{tender.category}</span>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontFamily: 'monospace' }}>
                      {tender.tenderRefNo}
                    </span>
                    <span className="badge" style={{ background: 'rgba(255, 255, 255, 0.05)', color: 'var(--text-secondary)' }}>
                      {tender.sourcePortal}
                    </span>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    {isPass && (
                      <span className="badge badge-pass">
                        <CheckCircle2 size={12} /> {score}% Match • PASS (GFR 161-IV)
                      </span>
                    )}
                    {isReview && (
                      <span className="badge badge-review">
                        <AlertTriangle size={12} /> {score}% Match • NEEDS REVIEW
                      </span>
                    )}
                    {!isPass && !isReview && (
                      <span className="badge badge-fail">
                        <XCircle size={12} /> {score}% Match • FAIL
                      </span>
                    )}
                  </div>
                </div>

                <div>
                  <h3 style={{ fontSize: '1.15rem', fontWeight: 700, lineHeight: '1.4' }}>
                    {tender.title}
                  </h3>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.35rem', color: 'var(--text-secondary)', fontSize: '0.8125rem' }}>
                    <Building size={14} color="var(--text-muted)" />
                    <span>{tender.department}</span>
                  </div>
                </div>

                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
                  gap: '0.75rem',
                  padding: '0.875rem',
                  background: 'rgba(0, 0, 0, 0.2)',
                  borderRadius: 'var(--radius-md)',
                  fontSize: '0.8125rem'
                }}>
                  <div>
                    <span style={{ color: 'var(--text-muted)', display: 'block', fontSize: '0.7rem' }}>Estimated Value</span>
                    <strong>{formatInr(tender.estimatedValueInr)}</strong>
                  </div>
                  <div>
                    <span style={{ color: 'var(--text-muted)', display: 'block', fontSize: '0.7rem' }}>EMD Deposit</span>
                    <strong style={{ color: '#34d399' }}>
                      {formatInr(tender.emdInr)} (Exempt for Startups)
                    </strong>
                  </div>
                  <div>
                    <span style={{ color: 'var(--text-muted)', display: 'block', fontSize: '0.7rem' }}>Closing Deadline</span>
                    <strong style={{ color: tender.daysRemaining && tender.daysRemaining <= 14 ? '#fbbf24' : 'var(--text-primary)' }}>
                      {tender.closingDate ? new Date(tender.closingDate).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }) : 'N/A'} 
                      {tender.daysRemaining ? ` (${tender.daysRemaining} days left)` : ''}
                    </strong>
                  </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '0.5rem', borderTop: '1px solid var(--border-subtle)', flexWrap: 'wrap', gap: '0.75rem' }}>
                  {tender.sourceUrl ? (
                    <a
                      href={tender.sourceUrl}
                      target="_blank"
                      rel="noreferrer"
                      style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: 'var(--text-muted)', fontSize: '0.75rem', textDecoration: 'none' }}
                    >
                      <ExternalLink size={14} /> Official Portal Link
                    </a>
                  ) : <div />}

                  <button
                    onClick={() => navigate(`/tenders/${tender.id}`)}
                    className="btn btn-primary"
                    style={{ fontSize: '0.8125rem' }}
                  >
                    Open Tender Workspace <ArrowRight size={16} />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
