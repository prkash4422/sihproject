import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { AuditLogItem, TenderRequirement } from '../types';
import {
  ShieldCheck,
  AlertTriangle,
  FileCheck2,
  Users,
  Building,
  CheckCircle2,
  XCircle,
  ExternalLink,
  History
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const [stats, setStats] = useState<any>({ totalStartups: 1, totalTenders: 3, pendingReviews: 1, totalRequirements: 6 });
  const [reviews, setReviews] = useState<any[]>([]);
  const [auditLogs, setAuditLogs] = useState<AuditLogItem[]>([]);
  const [rules, setRules] = useState<any[]>([]);
  const [activeAdminTab, setActiveAdminTab] = useState<'reviews' | 'rules' | 'audit'>('reviews');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAdminData();
  }, []);

  const loadAdminData = async () => {
    setLoading(true);
    try {
      const [sData, rData, aData, ruData] = await Promise.all([
        api.getAdminStats().catch(() => ({ totalStartups: 1, totalTenders: 3, pendingReviews: 1, totalRequirements: 6 })),
        api.getAdminReviews().catch(() => []),
        api.getAdminAuditLogs().catch(() => []),
        api.getAdminRules().catch(() => []),
      ]);
      setStats(sData);
      setReviews(rData);
      setAuditLogs(aData);
      setRules(ruData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveRequirement = async (id: number) => {
    try {
      await api.updateAdminRequirement(id, { reviewStatus: 'APPROVED' });
      await loadAdminData();
      alert('Requirement marked as APPROVED.');
    } catch (err: any) {
      alert('Error: ' + err.message);
    }
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '1.5rem 1rem', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#06b6d4', fontSize: '0.8125rem', fontWeight: 700, textTransform: 'uppercase' }}>
          <ShieldCheck size={16} />
          Procurement Authority & Governance
        </div>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem' }}>
          Procurement Admin & Rules Engine Portal
        </h1>
      </div>

      {/* KPI Stats Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
        <div className="glass-panel" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Registered Startups</div>
          <div style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem', color: '#818cf8' }}>
            {stats.totalStartups}
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Active Tenders</div>
          <div style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem', color: '#06b6d4' }}>
            {stats.totalTenders}
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Requirements Ingested</div>
          <div style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem', color: '#34d399' }}>
            {stats.totalRequirements}
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Review Queue Items</div>
          <div style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem', color: '#f59e0b' }}>
            {stats.pendingReviews}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="tabs-header">
        <button className={`tab-btn ${activeAdminTab === 'reviews' ? 'active' : ''}`} onClick={() => setActiveAdminTab('reviews')}>
          ⚠️ Review Queue ({reviews.length})
        </button>
        <button className={`tab-btn ${activeAdminTab === 'rules' ? 'active' : ''}`} onClick={() => setActiveAdminTab('rules')}>
          📜 Public Procurement Rules ({rules.length || 4})
        </button>
        <button className={`tab-btn ${activeAdminTab === 'audit' ? 'active' : ''}`} onClick={() => setActiveAdminTab('audit')}>
          🛡️ Immutable Audit Logs ({auditLogs.length})
        </button>
      </div>

      {/* Tab: Reviews */}
      {activeAdminTab === 'reviews' && (
        <div className="glass-panel animate-fade-in" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Ambiguous / Low-Confidence Requirement Queue</h3>

          {reviews.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
              All extracted tender clauses have been approved or normalized with high confidence.
            </p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {reviews.map((req) => (
                <div key={req.id} style={{ padding: '1rem', borderRadius: 'var(--radius-md)', background: 'rgba(245, 158, 11, 0.05)', border: '1px solid rgba(245, 158, 11, 0.25)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.75rem' }}>
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <span className="badge badge-review">{req.type}</span>
                      <span className="badge badge-info">Page {req.sourcePage || 1}</span>
                    </div>
                    <div style={{ fontSize: '0.875rem', fontWeight: 600, marginTop: '0.35rem' }}>
                      {req.requirementText}
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button onClick={() => handleApproveRequirement(req.id)} className="btn btn-success" style={{ fontSize: '0.75rem', padding: '0.4rem 0.75rem' }}>
                      <CheckCircle2 size={14} /> Approve Clause
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab: Rules */}
      {activeAdminTab === 'rules' && (
        <div className="glass-panel animate-fade-in" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Master Public Procurement Relaxation Catalogue</h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {rules.map((rule) => (
              <div key={rule.id} style={{ padding: '1.1rem', borderRadius: 'var(--radius-md)', background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--border-subtle)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span className="badge badge-info">{rule.ruleCode}</span>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Effective: {rule.effectiveDate || '2017-03-08'}</span>
                </div>
                <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginTop: '0.35rem' }}>{rule.name}</h4>
                <div style={{ fontSize: '0.75rem', color: '#06b6d4', marginTop: '0.15rem' }}>Authority: {rule.authority}</div>
                <p style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>{rule.description}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tab: Audit */}
      {activeAdminTab === 'audit' && (
        <div className="glass-panel animate-fade-in" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <History size={18} color="#06b6d4" />
            Immutable Audit Trail
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {auditLogs.map((log) => (
              <div key={log.id} style={{ padding: '0.75rem', borderRadius: 'var(--radius-sm)', background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--border-subtle)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.8125rem' }}>
                <div>
                  <strong style={{ color: '#818cf8' }}>{log.action}</strong>
                  <span style={{ color: 'var(--text-muted)', marginLeft: '0.5rem' }}>by {log.actorEmail || 'System'}</span>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '0.2rem' }}>
                    Target: {log.entityType} #{log.entityId} {log.metadataJson && `• ${log.metadataJson}`}
                  </div>
                </div>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
                  {new Date(log.createdAt).toLocaleString('en-IN')}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
