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

  return (
    <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '1.5rem 1rem', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Opportunity Pipeline & Kanban</h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginTop: '0.25rem' }}>
          Track procurement bidding stages from initial discovery through technical proposal preparation and submission.
        </p>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem 0', color: 'var(--text-muted)' }}>
          Loading opportunity pipeline...
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(5, minmax(240px, 1fr))',
          gap: '1.25rem',
          alignItems: 'stretch',
          overflowX: 'auto',
          paddingBottom: '1.5rem',
          width: '100%'
        }}>
          {STAGES.map((stage) => {
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
                  minHeight: '520px',
                  minWidth: '240px',
                  background: 'rgba(15, 23, 42, 0.45)',
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-lg)',
                  boxShadow: 'var(--shadow-card)'
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
                  {oppsInStage.map((opp) => (
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
                        boxShadow: 'var(--shadow-card)'
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
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
                        style={{ fontSize: '0.875rem', fontWeight: 600, lineHeight: '1.4', cursor: 'pointer' }}
                      >
                        {opp.tender.title}
                      </h4>

                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
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
                              {task.completed ? <CheckSquare size={14} color="#10b981" /> : <Square size={14} color="var(--text-muted)" />}
                              <span>{task.title}</span>
                            </div>
                          ))}
                        </div>
                      )}

                      {/* Stage Selector */}
                      <div style={{ marginTop: '0.5rem', display: 'flex', gap: '0.25rem', alignItems: 'center' }}>
                        <select
                          className="form-input"
                          value={opp.stage}
                          onChange={(e) => handleStageChange(opp.id, e.target.value)}
                          style={{ fontSize: '0.7rem', padding: '0.3rem 0.5rem' }}
                        >
                          {STAGES.map(s => <option key={s} value={s}>{s}</option>)}
                        </select>

                        <button
                          onClick={() => navigate(`/tenders/${opp.tender.id}`)}
                          className="btn btn-secondary"
                          style={{ padding: '0.3rem 0.5rem', fontSize: '0.7rem' }}
                          title="Open Tender Workspace"
                        >
                          <ChevronRight size={14} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
