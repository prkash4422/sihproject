import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthResponse } from '../types';
import { api } from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (credentials: { email: string; password: string }) => Promise<void>;
  register: (data: any) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isStartup: boolean;
  isAdmin: boolean;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('procurepilot_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('procurepilot_token');
      if (storedToken) {
        try {
          const userData = await api.getMe();
          setUser(userData);
        } catch (err) {
          localStorage.removeItem('procurepilot_token');
          setToken(null);
          setUser(null);
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (credentials: { email: string; password: string }) => {
    const res: AuthResponse = await api.login(credentials);
    localStorage.setItem('procurepilot_token', res.token);
    setToken(res.token);
    setUser({
      id: res.userId,
      email: res.email,
      fullName: res.fullName,
      roles: res.roles,
      startupId: res.startupId,
    });
  };

  const register = async (data: any) => {
    const res: AuthResponse = await api.register(data);
    localStorage.setItem('procurepilot_token', res.token);
    setToken(res.token);
    setUser({
      id: res.userId,
      email: res.email,
      fullName: res.fullName,
      roles: res.roles,
      startupId: res.startupId,
    });
  };

  const logout = () => {
    localStorage.removeItem('procurepilot_token');
    setToken(null);
    setUser(null);
  };

  const isStartup = user?.roles.includes('ROLE_STARTUP') ?? false;
  const isAdmin = (user?.roles.includes('ROLE_PROCUREMENT_ADMIN') || user?.roles.includes('ROLE_SYSTEM_ADMIN')) ?? false;

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        register,
        logout,
        isAuthenticated: !!token && !!user,
        isStartup,
        isAdmin,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
