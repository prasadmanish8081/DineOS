import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function HomeRedirect() {
  const { isAuthenticated, homePath } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={homePath} replace />;
  }

  return <Navigate to="/login" replace />;
}
