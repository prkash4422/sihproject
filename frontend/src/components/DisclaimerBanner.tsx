import React from 'react';
import { ShieldAlert } from 'lucide-react';

export const DisclaimerBanner: React.FC = () => {
  return (
    <div style={{
      background: 'rgba(99, 102, 241, 0.08)',
      borderBottom: '1px solid rgba(99, 102, 241, 0.2)',
      padding: '0.35rem 0.75rem',
      fontSize: '0.7rem',
      lineHeight: '1.35',
      color: '#94a3b8',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: '0.4rem',
      textAlign: 'center',
      zIndex: 40,
    }}>
      <ShieldAlert size={13} color="#818cf8" style={{ flexShrink: 0 }} />
      <span>
        <strong style={{ color: '#cbd5e1' }}>Compliance Notice:</strong> ProcurePilot provides decision support & does not replace official GeM/CPPP portals or constitute binding legal eligibility.
      </span>
    </div>
  );
};
