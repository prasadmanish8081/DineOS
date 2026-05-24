import SectionHeader from '../components/common/SectionHeader';
import StatCard from '../components/common/StatCard';
import Card from '../components/common/Card';
import { useAuth } from '../hooks/useAuth';

export default function DashboardPage() {
  const { user } = useAuth();

  return (
    <div className="space-y-8">
      <SectionHeader
        eyebrow="Dashboard"
        title={`Welcome back, ${user?.name || 'team'} `}
        description="Use this starter to navigate between restaurants, operational views, and analytics."
      />

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard label="Restaurants" value={0} hint="Connect your first restaurant" tone="from-slate-800 to-slate-900" />
        <StatCard label="Orders today" value={0} hint="Live order feed ready" tone="from-slate-800 to-slate-900" />
        <StatCard label="Sales today" value={0} hint="Payment integrated" tone="from-slate-800 to-slate-900" />
        <StatCard label="Kitchen status" value="Ready" hint="WebSocket dashboard wired" tone="from-slate-800 to-slate-900" />
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
        <Card>
          <h2 className="text-lg font-semibold text-white">Starter checklist</h2>
          <ul className="mt-4 space-y-3 text-sm text-slate-400">
            <li>• Wire the auth API to your backend.</li>
            <li>• Create restaurant, menu, table, and order screens.</li>
            <li>• Add WebSocket subscriptions for kitchen events.</li>
            <li>• Plug in Razorpay checkout once the payments UI is ready.</li>
          </ul>
        </Card>
        <Card>
          <h2 className="text-lg font-semibold text-white">Architecture</h2>
          <p className="mt-4 text-sm leading-7 text-slate-400">
            The frontend is organized around a shared Axios client, route-based layout composition,
            persistent JWT auth state, and service modules aligned to backend features.
          </p>
        </Card>
      </div>
    </div>
  );
}
