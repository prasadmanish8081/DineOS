import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { APP_NAME } from '../utils/constants';
import { useAuth } from '../hooks/useAuth';
import Button from '../components/common/Button';
import { getRoleLabel } from '../utils/auth';
import { useRestaurantScope } from '../hooks/useRestaurantScope';
import { buildRestaurantPath } from '../utils/routing';

const navItemsByRole = {
  ADMIN: [
    { label: 'Overview', to: '/app' },
    { label: 'Restaurants', to: '/app/restaurants' }
  ],
  OWNER: [
    { label: 'Overview', to: '/app' },
    { label: 'Restaurants', to: '/app/restaurants' }
  ],
  KITCHEN: [
    { label: 'Overview', to: '/app' },
    { label: 'Restaurants', to: '/app/restaurants' }
  ],
  CUSTOMER: [{ label: 'Overview', to: '/app' }]
};

export default function DashboardLayout() {
  const { user, logout, role } = useAuth();
  const navigate = useNavigate();
  const { restaurantId, inRestaurantScope } = useRestaurantScope();
  const [mobileOpen, setMobileOpen] = useState(false);
  const navItems = navItemsByRole[role] || navItemsByRole.CUSTOMER;
  const scopedItems =
    role === 'OWNER' || role === 'ADMIN'
      ? [
          { label: 'Dashboard', to: buildRestaurantPath(restaurantId) },
          { label: 'Settings', to: buildRestaurantPath(restaurantId, 'settings') },
          { label: 'Menu', to: buildRestaurantPath(restaurantId, 'menu') },
          { label: 'Orders', to: buildRestaurantPath(restaurantId, 'orders') },
          { label: 'Analytics', to: buildRestaurantPath(restaurantId, 'analytics') },
          { label: 'QR Tables', to: buildRestaurantPath(restaurantId, 'tables') }
        ]
      : role === 'KITCHEN'
        ? [
            { label: 'Orders', to: buildRestaurantPath(restaurantId, 'orders') },
            { label: 'Kitchen', to: buildRestaurantPath(restaurantId, 'kitchen') }
          ]
        : [];

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  const linkClass = (isActive, scoped = false) =>
    `block rounded-2xl px-4 py-3 text-sm font-semibold transition ${
      isActive
        ? scoped
          ? 'bg-white/10 text-white ring-1 ring-white/10'
          : 'bg-accent-500 text-white shadow-lg shadow-accent-950/20'
        : 'text-slate-300 hover:bg-white/5 hover:text-white'
    }`;

  const sidebar = (
    <div className="glass h-full overflow-y-auto rounded-none p-5 lg:rounded-3xl">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-accent-300">{APP_NAME}</p>
          <h2 className="mt-2 text-xl font-black text-white">Owner Console</h2>
        </div>
        <div className="flex items-center gap-2">
          <div className="rounded-2xl bg-white/5 px-3 py-2 text-xs text-slate-300">
            {getRoleLabel(user?.role)}
          </div>
          <button
            type="button"
            className="rounded-2xl border border-white/10 px-3 py-2 text-xs font-semibold text-slate-200 lg:hidden"
            onClick={() => setMobileOpen(false)}
          >
            Close
          </button>
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-white/10 bg-white/5 p-4">
        <p className="truncate text-sm font-semibold text-white">{user?.name || 'Restaurant team'}</p>
        <p className="mt-1 truncate text-xs text-slate-400">{user?.email || 'Signed in'}</p>
      </div>

      <nav className="mt-6 space-y-2">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/app'}
            onClick={() => setMobileOpen(false)}
            className={({ isActive }) => linkClass(isActive)}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>

      {inRestaurantScope && scopedItems.length ? (
        <div className="mt-7">
          <div className="px-3 pb-2 text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">
            Restaurant
          </div>
          <nav className="space-y-2">
            {scopedItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === buildRestaurantPath(restaurantId)}
                onClick={() => setMobileOpen(false)}
                className={({ isActive }) => linkClass(isActive, true)}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
      ) : null}

      <Button variant="secondary" className="mt-7 w-full" onClick={handleLogout}>
        Sign out
      </Button>
    </div>
  );

  return (
    <div className="min-h-screen lg:grid lg:grid-cols-[300px_1fr]">
      <header className="sticky top-0 z-30 border-b border-white/10 bg-slate-950/90 px-4 py-3 backdrop-blur lg:hidden">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-accent-300">{APP_NAME}</p>
            <p className="text-sm font-bold text-white">Owner Console</p>
          </div>
          <Button variant="secondary" onClick={() => setMobileOpen((open) => !open)}>
            {mobileOpen ? 'Close' : 'Menu'}
          </Button>
        </div>
      </header>

      {mobileOpen ? <aside className="fixed inset-0 z-40 bg-slate-950/95 lg:hidden">{sidebar}</aside> : null}

      <aside className="hidden border-r border-white/10 bg-slate-950/40 p-5 lg:sticky lg:top-0 lg:block lg:h-screen">
        {sidebar}
      </aside>

      <main className="min-w-0 p-4 sm:p-6 lg:p-8">
        <Outlet />
      </main>
    </div>
  );
}
