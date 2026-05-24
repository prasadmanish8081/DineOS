import { createContext, useEffect, useMemo, useState } from 'react';
import { authService } from '../services/authService';
import { storage } from '../utils/storage';
import { TOKEN_KEY, USER_KEY } from '../utils/constants';
import { getHomePath } from '../utils/auth';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const raw = storage.get(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  });
  const [token, setToken] = useState(() => storage.get(TOKEN_KEY));
  const [loading, setLoading] = useState(Boolean(token));

  useEffect(() => {
    if (!token) {
      setLoading(false);
      return;
    }

    let active = true;
    authService
      .me()
      .then((response) => {
        if (!active) return;
        setUser(response.data);
        storage.set(USER_KEY, JSON.stringify(response.data));
      })
      .catch(() => {
        logout();
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [token]);

  const applyAuthResponse = (payload) => {
    const authToken = payload.token ?? payload.accessToken;
    const nextUser = payload.user ?? payload;
    if (authToken) {
      setToken(authToken);
      storage.set(TOKEN_KEY, authToken);
    }
    if (nextUser) {
      setUser(nextUser);
      storage.set(USER_KEY, JSON.stringify(nextUser));
    }
    return {
      ...payload,
      user: payload.user ?? nextUser,
      homePath: getHomePath(nextUser?.role)
    };
  };

  const login = async (credentials) => {
    const response = await authService.login(credentials);
    return applyAuthResponse(response.data);
  };

  const register = async (values) => {
    const response = await authService.register(values);
    return applyAuthResponse(response.data);
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    storage.remove(TOKEN_KEY);
    storage.remove(USER_KEY);
  };

  const value = useMemo(
    () => ({
      user,
      token,
      loading,
      role: user?.role ?? null,
      homePath: getHomePath(user?.role),
      isAuthenticated: Boolean(token && user),
      login,
      register,
      logout,
      setUser
    }),
    [user, token, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
