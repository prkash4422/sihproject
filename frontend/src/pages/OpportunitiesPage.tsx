import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { Opportunity, OpportunityTask } from '../types';
import {
  Briefcase,
  Plus,
  CheckCircle2,
  Clock,
  ExternalLink,
  ChevronRight,
  CheckSquare,
  Square
} from 'lucide-react';

const STAGES: ('INTERESTED' | 'PREPARING' | 'SUBMITTED' | 'EVALUATION' | 'OUTCOME')[] = [
  'INTERESTED',
  'PREPARING',
  'SUBMITTED',
  'EVALUATION',
  'OUTCOME'
];

export const OpportunitiesPage: React.FC = () => {
  const navigate = useNavigate();
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeMobileStage, setActiveMobileStage] = useState<string>('ALL');

  useEffect(() => {
    loadOpportunities();
  }, []);

  const loadOpportunities = async () => {
    setLoading(true);
    try {
      const data = await api.getOpportunities();
      setOpportunities(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleStageChange = async (oppId: number, newStage: string) => {
    try {
      await api.updateOpportunityStage(oppId, newStage);
      await loadOpportunities();
    } catch (err: any) {
      alert('Failed to update stage: ' + err.message);
    }
  };

  const handleToggleTask = async (taskId: number) => {
    try {
      await api.toggleOpportunityTask(taskId);
      await loadOpportunities();
    } catch (err: any) {
      alert('Error updating task: ' + err.message);
    }
  };

  const visibleStages = activeMobileStage === 'ALL' 
    ? STAGES 
    : STAGES.filter(s => s === activeMobileStage);

  return (
    <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '0.5rem 0', display: 'flex', flexDirection: 'column', gap: '1.25rem', width: '100%' }}>
      <div>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Opportunity Pipeline & Kanban</h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginTop: '0.25rem' }}>
          Track procurement bidding stages from initial discovery through technical proposal preparation and submission.
        </p>

        {/* Mobile Stage Selector Filter */}
        <div className="tabs-header" style={{ marginTop: '1rem' }}>
          <button
            className={`tab-btn ${activeMobileStage === 'ALL' ? 'active' : ''}`}
            onClick={() => setActiveMobileStage('ALL')}
          >
            All Stages ({opportunities.length})
          </button>
          {STAGES.map(s => {
            const count = opportunities.filter(o => o.stage === s).length;
            return (
              <button
                key={s}
                className={`tab-btn ${activeMobileStage === s ? 'active' : ''}`}
                onClick={() => setActiveMobileStage(s)}
              >
                {s} ({count})
              </button>
            );
          })}
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem 0', color: 'var(--text-muted)' }}>
          Loading opportunity pipeline...
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: visibleStages.length === 1 
            ? '1fr' 
            : 'repeat(auto-fit, minmax(min(100%, 260px), 1fr))',
          gap: '1.25rem',
          alignItems: 'stretch',
          width: '100%'
        }}>
          {visibleStages.map((stage) => {
            const oppsInStage = opportunities.filter((o) => o.stage === stage);

            return (
              <div
                key={stage}
                className="glass-panel"
                style={{
                  padding: '1.25rem',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '0.875rem',
                  minHeight: '400px',
                  background: 'rgba(15, 23, 42, 0.45)',
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-lg)',
                  boxShadow: 'var(--shadow-card)',
                  width: '100%'
                }}
              >
                <div style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  borderBottom: '1px solid var(--border-subtle)',
                  paddingBottom: '0.5rem'
                }}>
                  <strong style={{ fontSize: '0.8125rem', color: '#06b6d4', letterSpacing: '0.04em' }}>
                    {stage}
                  </strong>
                  <span className="badge" style={{ background: 'rgba(255, 255, 255, 0.05)', fontSize: '0.7rem' }}>
                    {oppsInStage.length}
                  </span>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {oppsInStage.length === 0 ? (
                    <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.75rem', padding: '2rem 0' }}>
                      No opportunities in this stage.
                    </div>
                  ) : (
                    oppsInStage.map((opp) => (
                      <div
                        key={opp.id}
                        style={{
                          padding: '1rem',
                          borderRadius: 'var(--radius-md)',
                          background: 'rgba(30, 41, 59, 0.8)',
                          border: '1px solid var(--border-subtle)',
                          display: 'flex',
                          flexDirection: 'column',
                          gap: '0.5rem',
                          boxShadow: 'var(--shadow-card)',
                          minWidth: 0
                        }}
                      >
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '0.35rem' }}>
                          <span className="badge badge-info" style={{ fontSize: '0.65rem' }}>
                            {opp.tender.category}
                          </span>
                          {(opp.readinessPercent || 0) >= 75 ? (
                            <span className="badge badge-pass" style={{ fontSize: '0.65rem' }}>
                              {opp.readinessPercent}% Ready
                            </span>
                          ) : (opp.readinessPercent || 0) >= 50 ? (
                            <span className="badge badge-review" style={{ fontSize: '0.65rem' }}>
                              {opp.readinessPercent}% Ready
                            </span>
                          ) : (
                            <span className="badge badge-fail" style={{ fontSize: '0.65rem' }}>
                              {opp.readinessPercent}% Ready
                            </span>
                          )}
                        </div>

                        <h4
                          onClick={() => navigate(`/tenders/${opp.tender.id}`)}
                          style={{ fontSize: '0.875rem', fontWeight: 600, lineHeight: '1.4', cursor: 'pointer', wordBreak: 'break-word' }}
                        >
                          {opp.tender.title}
                        </h4>

                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }} className="text-break">
                          {opp.tender.department}
                        </div>

                        {/* Subtasks */}
                        {opp.tasks && opp.tasks.length > 0 && (
                          <div style={{ marginTop: '0.5rem', borderTop: '1px solid var(--border-subtle)', paddingTop: '0.5rem' }}>
                            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginBottom: '0.25rem', fontWeight: 600 }}>
                              Preparation Tasks ({opp.tasks.filter(t => t.completed).length}/{opp.tasks.length}):
                            </div>
                            {opp.tasks.map((task) => (
                              <div
                                key={task.id}
                                onClick={() => handleToggleTask(task.id)}
                                style={{
                                  display: 'flex',
                                  alignItems: 'center',
                                  gap: '0.35rem',
                                  fontSize: '0.75rem',
                                  color: task.completed ? 'var(--text-muted)' : 'var(--text-primary)',
                                  textDecoration: task.completed ? 'line-through' : 'none',
                                  cursor: 'pointer',
                                  padding: '0.2rem 0'
                                }}
                              >
                                {task.completed ? <CheckSquare size={14} color="#10b981" style={{ flexShrink: 0 }} /> : <Square size={14} color="var(--text-muted)" style={{ flexShrink: 0 }} />}
                                <span className="text-break">{task.title}</span>
                              </div>
                            ))}
                          </div>
                        )}

                        {/* Stage Selector */}
                        <div style={{ marginTop: '0.5rem', display: 'flex', gap: '0.35rem', alignItems: 'center' }}>
                          <select
                            className="form-input"
                            value={opp.stage}
                            onChange={(e) => handleStageChange(opp.id, e.target.value)}
                            style={{ fontSize: '0.75rem', padding: '0.35rem 0.5rem', flex: 1 }}
                          >
                            {STAGES.map(s => <option key={s} value={s}>{s}</option>)}
                          </select>

                          <button
                            onClick={() => navigate(`/tenders/${opp.tender.id}`)}
                            className="btn btn-secondary"
                            style={{ padding: '0.35rem 0.55rem', fontSize: '0.75rem', flexShrink: 0 }}
                            title="Open Tender Workspace"
                          >
                            <ChevronRight size={14} />
                          </button>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
