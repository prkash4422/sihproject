import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Search,
  CheckSquare,
  Briefcase,
  UserCheck,
  ShieldCheck,
  FileText,
  Settings,
  Sparkles,
  Shield,
  X
} from 'lucide-react';

interface SidebarProps {
  isMobileOpen?: boolean;
  onClose?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isMobileOpen, onClose }) => {
  const { isStartup, isAdmin } = useAuth();

  const handleLinkClick = () => {
    if (onClose) onClose();
  };

  const linkStyle = ({ isActive }: { isActive: boolean }) => ({
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    padding: '0.65rem 1rem',
    borderRadius: 'var(--radius-md)',
    color: isActive ? '#ffffff' : 'var(--text-secondary)',
    background: isActive ? 'rgba(99, 102, 241, 0.15)' : 'transparent',
    border: isActive ? '1px solid rgba(99, 102, 241, 0.3)' : '1px solid transparent',
    textDecoration: 'none',
    fontSize: '0.875rem',
    fontWeight: isActive ? 600 : 500,
    transition: 'var(--transition)',
  });

  return (
    <aside className={`app-sidebar ${isMobileOpen ? 'mobile-open' : ''}`}>
      {/* Mobile Drawer Header */}
      <div className="sidebar-mobile-header" style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0.5rem 0.25rem 1rem',
        borderBottom: '1px solid var(--border-subtle)',
        marginBottom: '0.5rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <div style={{
            width: '1.75rem',
            height: '1.75rem',
            borderRadius: '0.5rem',
            background: 'linear-gradient(135deg, #6366f1 0%, #06b6d4 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            <Shield size={16} color="#ffffff" />
          </div>
          <span style={{ fontSize: '1.1rem', fontWeight: 800, fontFamily: 'Outfit', color: '#ffffff' }}>
            PROCURE<span style={{ color: '#06b6d4' }}>PILOT</span>
          </span>
        </div>
        <button
          onClick={onClose}
          className="btn btn-secondary"
          style={{ padding: '0.4rem', borderRadius: '50%' }}
          aria-label="Close navigation"
        >
          <X size={18} />
        </button>
      </div>

      <div style={{ padding: '0 0.5rem 0.5rem', fontSize: '0.7rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
        Startup Portal
      </div>

      <NavLink to="/dashboard" style={linkStyle} onClick={handleLinkClick}>
        <LayoutDashboard size={18} />
        Dashboard
      </NavLink>

      <NavLink to="/tenders" style={linkStyle} onClick={handleLinkClick}>
        <Search size={18} />
        Tender Discovery
      </NavLink>

      <NavLink to="/opportunities" style={linkStyle} onClick={handleLinkClick}>
        <Briefcase size={18} />
        Opportunity Pipeline
      </NavLink>

      <NavLink to="/onboarding" style={linkStyle} onClick={handleLinkClick}>
        <UserCheck size={18} />
        Onboarding Wizard
      </NavLink>

      <NavLink to="/profile" style={linkStyle} onClick={handleLinkClick}>
        <FileText size={18} />
        Profile & Evidence
      </NavLink>

      {isAdmin && (
        <>
          <div style={{ padding: '1.25rem 0.5rem 0.5rem', fontSize: '0.7rem', fontWeight: 700, color: '#06b6d4', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            Governance & Admin
          </div>
          <NavLink to="/admin" style={linkStyle} onClick={handleLinkClick}>
            <ShieldCheck size={18} />
            Admin Dashboard
          </NavLink>
        </>
      )}

      {/* SIH Demo Helper Badge */}
      <div style={{
        marginTop: 'auto',
        padding: '0.875rem',
        borderRadius: 'var(--radius-md)',
        background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(6, 182, 212, 0.1) 100%)',
        border: '1px solid rgba(99, 102, 241, 0.25)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#06b6d4', fontSize: '0.75rem', fontWeight: 700 }}>
          <Sparkles size={14} />
          SIH 2026 DEMO MODE
        </div>
        <p style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.25rem', lineHeight: '1.3' }}>
          Simulated live evaluation of GFR 161(iv) and DPIIT public procurement relaxation.
        </p>
      </div>
    </aside>
  );
};
