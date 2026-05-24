import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function PublicRoute({ children }) {
  const { isAuthenticated, homePath } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={homePath} replace />;
  }

  return children;
}
