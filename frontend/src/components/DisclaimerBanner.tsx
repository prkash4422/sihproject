import React from 'react';
import { ShieldAlert } from 'lucide-react';

export const DisclaimerBanner: React.FC = () => {
  return (
    <div style={{
      background: 'rgba(99, 102, 241, 0.08)',
      borderBottom: '1px solid rgba(99, 102, 241, 0.2)',
      padding: '0.4rem 1rem',
      fontSize: '0.75rem',
      color: '#94a3b8',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: '0.5rem',
      zIndex: 40,
    }}>
      <ShieldAlert size={14} color="#818cf8" />
      <span>
        <strong>Official Compliance Notice:</strong> ProcurePilot provides decision support and does not replace official government procurement portals (GeM / CPPP) or constitute a binding legal or government eligibility decision.
      </span>
    </div>
  );
};
