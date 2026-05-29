import { Route, Routes } from 'react-router-dom';
import AuthLayout from './layouts/AuthLayout';
import DashboardLayout from './layouts/DashboardLayout';
import PublicLayout from './layouts/PublicLayout';
import ProtectedRoute from './components/ProtectedRoute';
import PublicRoute from './components/PublicRoute';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import RestaurantsPage from './pages/RestaurantsPage';
import RestaurantDetailPage from './pages/RestaurantDetailPage';
import RestaurantSettingsPage from './pages/RestaurantSettingsPage';
import MenuPage from './pages/MenuPage';
import OrdersPage from './pages/OrdersPage';
import AnalyticsPage from './pages/AnalyticsPage';
import KitchenDashboardPage from './pages/KitchenDashboardPage';
import QrTablesPage from './pages/QrTablesPage';
import CustomerMenuPage from './pages/CustomerMenuPage';
import NotFoundPage from './pages/NotFoundPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route
        path="/login"
        element={
          <PublicRoute>
            <AuthLayout>
              <LoginPage />
            </AuthLayout>
          </PublicRoute>
        }
      />
      <Route
        path="/register"
        element={
          <PublicRoute>
            <AuthLayout>
              <RegisterPage />
            </AuthLayout>
          </PublicRoute>
        }
      />
      <Route
        path="/menu/:slug/table/:tableNumber"
        element={
          <PublicLayout>
            <CustomerMenuPage />
          </PublicLayout>
        }
      />

      <Route
        path="/app"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="restaurants" element={<RestaurantsPage />} />
        <Route path="restaurants/:restaurantId" element={<RestaurantDetailPage />} />
        <Route
          path="restaurants/:restaurantId/settings"
          element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <RestaurantSettingsPage />
            </ProtectedRoute>
          }
        />
        <Route path="restaurants/:restaurantId/menu" element={<MenuPage />} />
        <Route path="restaurants/:restaurantId/orders" element={<OrdersPage />} />
        <Route
          path="restaurants/:restaurantId/analytics"
          element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <AnalyticsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="restaurants/:restaurantId/tables"
          element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
              <QrTablesPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="restaurants/:restaurantId/kitchen"
          element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OWNER', 'KITCHEN']}>
              <KitchenDashboardPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<NotFoundPage />} />
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
