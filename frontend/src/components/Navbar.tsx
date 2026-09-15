import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { NotificationItem } from '../types';
import { Shield, Bell, LogOut, User, Sparkles, CheckCircle2, ChevronDown, Menu, X } from 'lucide-react';

interface NavbarProps {
  isMobileSidebarOpen?: boolean;
  onToggleMobileSidebar?: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ isMobileSidebarOpen, onToggleMobileSidebar }) => {
  const { user, isAuthenticated, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      api.getNotifications()
        .then(setNotifications)
        .catch(() => {});
    }
  }, [isAuthenticated]);

  const unreadCount = notifications.filter(n => !n.isRead).length;

  const handleNotificationClick = async (n: NotificationItem) => {
    if (!n.isRead) {
      await api.markNotificationRead(n.id);
      setNotifications(prev => prev.map(item => item.id === n.id ? { ...item, isRead: true } : item));
    }
    if (n.link) navigate(n.link);
    setShowNotifications(false);
  };

  return (
    <header className="glass-nav" style={{ position: 'sticky', top: 0, zIndex: 50, height: '4rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 1rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        {/* Mobile Hamburger Menu Toggle Button */}
        {isAuthenticated && (
          <button
            onClick={onToggleMobileSidebar}
            className="btn btn-secondary mobile-menu-btn"
            style={{ padding: '0.45rem', borderRadius: 'var(--radius-md)', display: 'none', alignItems: 'center', justifyContent: 'center' }}
            aria-label="Toggle navigation menu"
          >
            {isMobileSidebarOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        )}

        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', textDecoration: 'none' }}>
          <div style={{
            width: '2.25rem',
            height: '2.25rem',
            borderRadius: '0.625rem',
            background: 'linear-gradient(135deg, #6366f1 0%, #06b6d4 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 0 15px rgba(99, 102, 241, 0.4)',
            flexShrink: 0
          }}>
            <Shield size={20} color="#ffffff" />
          </div>
          <div>
            <span style={{ fontSize: '1.25rem', fontWeight: 800, letterSpacing: '-0.03em', color: '#ffffff', fontFamily: 'Outfit' }}>
              PROCURE<span style={{ color: '#06b6d4' }}>PILOT</span>
            </span>
            <span className="nav-logo-sub" style={{ display: 'block', fontSize: '0.65rem', color: '#94a3b8', letterSpacing: '0.08em', textTransform: 'uppercase', marginTop: '-0.2rem' }}>
              Procurement Intelligence
            </span>
          </div>
        </Link>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
        {isAuthenticated ? (
          <>
            {/* Notification Bell */}
            <div style={{ position: 'relative' }}>
              <button
                onClick={() => setShowNotifications(!showNotifications)}
                className="btn btn-secondary"
                style={{ padding: '0.5rem', borderRadius: '50%', position: 'relative' }}
                title="Notifications"
              >
                <Bell size={18} />
                {unreadCount > 0 && (
                  <span style={{
                    position: 'absolute',
                    top: '-2px',
                    right: '-2px',
                    background: '#f43f5e',
                    color: '#ffffff',
                    fontSize: '0.65rem',
                    fontWeight: 700,
                    width: '18px',
                    height: '18px',
                    borderRadius: '50%',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}>
                    {unreadCount}
                  </span>
                )}
              </button>

              {showNotifications && (
                <div className="glass-panel" style={{
                  position: 'absolute',
                  right: 0,
                  top: '2.75rem',
                  width: '320px',
                  maxWidth: 'calc(100vw - 2rem)',
                  maxHeight: '380px',
                  overflowY: 'auto',
                  padding: '1rem',
                  zIndex: 60,
                  background: '#0f172a',
                  border: '1px solid rgba(255, 255, 255, 0.15)',
                  boxShadow: '0 10px 30px rgba(0,0,0,0.7)'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '0.5rem' }}>
                    <h4 style={{ fontSize: '0.875rem' }}>Notifications</h4>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{unreadCount} unread</span>
                  </div>
                  {notifications.length === 0 ? (
                    <p style={{ fontSize: '0.8125rem', color: 'var(--text-muted)', textAlign: 'center', padding: '1rem 0' }}>
                      No notifications
                    </p>
                  ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                      {notifications.map(n => (
                        <div
                          key={n.id}
                          onClick={() => handleNotificationClick(n)}
                          style={{
                            padding: '0.625rem',
                            borderRadius: 'var(--radius-sm)',
                            background: n.isRead ? 'transparent' : 'rgba(99, 102, 241, 0.1)',
                            border: '1px solid ' + (n.isRead ? 'var(--border-subtle)' : 'rgba(99, 102, 241, 0.3)'),
                            cursor: 'pointer',
                          }}
                        >
                          <div style={{ fontSize: '0.8125rem', fontWeight: 600, color: n.isRead ? 'var(--text-secondary)' : '#ffffff' }}>
                            {n.title}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                            {n.message}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>

            {/* User Profile dropdown */}
            <div style={{ position: 'relative' }}>
              <button
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="btn btn-secondary"
                style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', padding: '0.35rem 0.65rem' }}
              >
                <div style={{
                  width: '1.75rem',
                  height: '1.75rem',
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '0.75rem',
                  fontWeight: 700,
                  flexShrink: 0
                }}>
                  {user?.fullName ? user.fullName.charAt(0) : 'U'}
                </div>
                <span className="nav-user-name" style={{ fontSize: '0.8125rem', maxWidth: '110px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {user?.fullName}
                </span>
                <ChevronDown size={14} color="var(--text-muted)" />
              </button>

              {showUserMenu && (
                <div className="glass-panel" style={{
                  position: 'absolute',
                  right: 0,
                  top: '2.75rem',
                  width: '220px',
                  maxWidth: 'calc(100vw - 2rem)',
                  padding: '0.5rem',
                  zIndex: 60,
                  background: '#0f172a',
                  border: '1px solid rgba(255, 255, 255, 0.15)',
                  boxShadow: '0 10px 30px rgba(0,0,0,0.7)'
                }}>
                  <div style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-subtle)', marginBottom: '0.5rem' }}>
                    <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Signed in as</p>
                    <p style={{ fontSize: '0.8125rem', fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis' }}>{user?.email}</p>
                    <span className="badge badge-info" style={{ marginTop: '0.25rem', fontSize: '0.65rem' }}>
                      {isAdmin ? 'ADMIN' : 'STARTUP'}
                    </span>
                  </div>

                  <Link
                    to="/profile"
                    onClick={() => setShowUserMenu(false)}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.5rem',
                      padding: '0.5rem',
                      borderRadius: 'var(--radius-sm)',
                      color: 'var(--text-secondary)',
                      textDecoration: 'none',
                      fontSize: '0.8125rem',
                    }}
                  >
                    <User size={16} /> Profile & Evidence
                  </Link>

                  <button
                    onClick={() => {
                      logout();
                      navigate('/login');
                      setShowUserMenu(false);
                    }}
                    style={{
                      width: '100%',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.5rem',
                      padding: '0.5rem',
                      borderRadius: 'var(--radius-sm)',
                      color: '#fb7185',
                      background: 'transparent',
                      border: 'none',
                      cursor: 'pointer',
                      fontSize: '0.8125rem',
                      textAlign: 'left',
                    }}
                  >
                    <LogOut size={16} /> Sign Out
                  </button>
                </div>
              )}
            </div>
          </>
        ) : (
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <Link to="/login" className="btn btn-secondary" style={{ fontSize: '0.8rem', padding: '0.4rem 0.8rem' }}>Log In</Link>
            <Link to="/register" className="btn btn-primary" style={{ fontSize: '0.8rem', padding: '0.4rem 0.8rem' }}>Get Started</Link>
          </div>
        )}
      </div>
    </header>
  );
};
