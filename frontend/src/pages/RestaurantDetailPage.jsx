import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import SectionHeader from '../components/common/SectionHeader';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import StatCard from '../components/common/StatCard';
import LoadingScreen from '../components/common/LoadingScreen';
import { restaurantService } from '../services/restaurantService';
import { analyticsService } from '../services/analyticsService';
import { extractApiErrors } from '../utils/forms';
import { formatCurrency, formatNumber } from '../utils/format';

function ChartTooltip({ active, payload, label, formatter }) {
  if (!active || !payload?.length) return null;

  return (
    <div className="glass rounded-2xl px-4 py-3">
      <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">{label}</p>
      <div className="mt-2 space-y-1">
        {payload.map((item) => (
          <div key={item.dataKey} className="flex items-center justify-between gap-6 text-sm">
            <span className="text-slate-300">{item.name || item.dataKey}</span>
            <span className="font-semibold text-white">{formatter ? formatter(item.value) : item.value}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function RestaurantDetailPage() {
  const { restaurantId } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [analyticsError, setAnalyticsError] = useState('');

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    setAnalyticsError('');
    setRestaurant(null);
    setAnalytics(null);

    Promise.allSettled([restaurantService.getById(restaurantId), analyticsService.getDashboard(restaurantId)])
      .then(([restaurantResult, analyticsResult]) => {
        if (!active) return;

        if (restaurantResult.status === 'fulfilled') {
          setRestaurant(restaurantResult.value.data);
        } else {
          setError(extractApiErrors(restaurantResult.reason).message);
        }

        if (analyticsResult.status === 'fulfilled') {
          setAnalytics(analyticsResult.value.data);
        } else {
          setAnalyticsError(extractApiErrors(analyticsResult.reason).message);
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [restaurantId]);

  if (loading) return <LoadingScreen />;

  const dailyRevenue = (analytics?.dailyRevenueTrend || []).map((point) => ({
    period: point.period,
    revenue: Number(point.revenue || 0)
  }));

  const topItems = (analytics?.topSellingItems || []).slice(0, 6).map((item) => ({
    name: item.menuItemName || `Item ${item.menuItemId}`,
    qty: Number(item.quantitySold || 0)
  }));

  const quickLinks = [
    { label: 'Update settings', to: `/app/restaurants/${restaurantId}/settings`, helper: 'Profile, contact, logo' },
    { label: 'Manage menu', to: `/app/restaurants/${restaurantId}/menu`, helper: 'Categories, pricing, availability' },
    { label: 'Review orders', to: `/app/restaurants/${restaurantId}/orders`, helper: 'Live queue and status changes' },
    { label: 'QR tables', to: `/app/restaurants/${restaurantId}/tables`, helper: 'Create table QR codes' }
  ];

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="Dashboard"
        title={restaurant ? restaurant.name : `Restaurant #${restaurantId}`}
        description={restaurant ? `${restaurant.address} | ${restaurant.phone}` : 'Restaurant overview and modules.'}
        action={
          <Link to={`/app/restaurants/${restaurantId}/settings`}>
            <Button>Open settings</Button>
          </Link>
        }
      />

      {error ? (
        <Card>
          <p className="text-sm text-red-300">{error}</p>
        </Card>
      ) : null}

      {restaurant ? (
        <>
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <StatCard label="Total sales" value={formatCurrency(analytics?.totalSales || 0)} hint="Paid order revenue" />
            <StatCard label="Orders" value={Number(analytics?.totalOrders || 0)} hint="All tracked orders" />
            <StatCard label="Today" value={formatCurrency(analytics?.dailyRevenue || 0)} hint="Revenue today" />
            <StatCard label="Cancelled" value={Number(analytics?.cancelledOrders || 0)} hint="Orders needing review" />
          </div>

          {analyticsError ? (
            <Card>
              <p className="text-sm text-amber-200">Analytics unavailable: {analyticsError}</p>
            </Card>
          ) : null}

          <div className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
            <Card>
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold text-white">Revenue trend</h2>
                  <p className="mt-2 text-sm text-slate-400">Daily paid revenue for recent service days.</p>
                </div>
                <Badge tone="blue">{formatNumber(dailyRevenue.length)} days</Badge>
              </div>
              <div className="mt-5 h-72">
                {dailyRevenue.length ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={dailyRevenue} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                      <defs>
                        <linearGradient id="dashboardRevenue" x1="0" x2="0" y1="0" y2="1">
                          <stop offset="5%" stopColor="#f97316" stopOpacity={0.55} />
                          <stop offset="95%" stopColor="#f97316" stopOpacity={0.02} />
                        </linearGradient>
                      </defs>
                      <CartesianGrid stroke="rgba(148,163,184,0.12)" strokeDasharray="4 4" />
                      <XAxis dataKey="period" tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }} />
                      <YAxis tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }} width={48} />
                      <Tooltip content={<ChartTooltip formatter={formatCurrency} />} />
                      <Area
                        type="monotone"
                        dataKey="revenue"
                        name="Revenue"
                        stroke="#fb923c"
                        strokeWidth={2}
                        fill="url(#dashboardRevenue)"
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                ) : (
                  <div className="flex h-full items-center justify-center rounded-3xl border border-dashed border-white/10 text-sm text-slate-400">
                    Revenue appears here once paid orders are recorded.
                  </div>
                )}
              </div>
            </Card>

            <Card>
              <h2 className="text-lg font-semibold text-white">Top sellers</h2>
              <p className="mt-2 text-sm text-slate-400">Menu items ranked by quantity sold.</p>
              <div className="mt-5 h-72">
                {topItems.length ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={topItems} layout="vertical" margin={{ top: 10, right: 10, left: 12, bottom: 0 }}>
                      <CartesianGrid stroke="rgba(148,163,184,0.12)" strokeDasharray="4 4" />
                      <XAxis type="number" tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }} />
                      <YAxis
                        type="category"
                        dataKey="name"
                        width={92}
                        tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }}
                      />
                      <Tooltip content={<ChartTooltip />} />
                      <Bar dataKey="qty" name="Quantity" fill="rgba(14,165,233,0.85)" radius={[0, 10, 10, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                ) : (
                  <div className="flex h-full items-center justify-center rounded-3xl border border-dashed border-white/10 text-sm text-slate-400">
                    Top sellers appear after orders are completed.
                  </div>
                )}
              </div>
            </Card>
          </div>

          <div className="grid gap-6 xl:grid-cols-[0.85fr_1.15fr]">
            <Card>
              <h2 className="text-lg font-semibold text-white">Restaurant profile</h2>
              <div className="mt-4 flex flex-wrap items-center gap-3">
                <Badge tone="blue">Slug: {restaurant.slug}</Badge>
                <Badge tone="slate">GST: {restaurant.gstNumber}</Badge>
              </div>
              <div className="mt-5 space-y-3 text-sm">
                <div className="flex items-center justify-between gap-4 rounded-2xl bg-white/5 px-4 py-3">
                  <span className="text-slate-400">Phone</span>
                  <span className="font-semibold text-white">{restaurant.phone}</span>
                </div>
                <div className="rounded-2xl bg-white/5 px-4 py-3">
                  <p className="text-slate-400">Address</p>
                  <p className="mt-1 font-semibold text-white">{restaurant.address}</p>
                </div>
              </div>
            </Card>

            <Card>
              <h2 className="text-lg font-semibold text-white">Owner workflows</h2>
              <div className="mt-5 grid gap-3 sm:grid-cols-2">
                {quickLinks.map((link) => (
                  <Link
                    key={link.to}
                    to={link.to}
                    className="rounded-3xl border border-white/10 bg-white/5 p-4 transition hover:border-accent-400/50 hover:bg-white/8"
                  >
                    <p className="font-semibold text-white">{link.label}</p>
                    <p className="mt-2 text-sm text-slate-400">{link.helper}</p>
                  </Link>
                ))}
              </div>
            </Card>
          </div>
        </>
      ) : null}
    </div>
  );
}
