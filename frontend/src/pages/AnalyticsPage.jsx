import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import SectionHeader from '../components/common/SectionHeader';
import Card from '../components/common/Card';
import StatCard from '../components/common/StatCard';
import Badge from '../components/common/Badge';
import LoadingScreen from '../components/common/LoadingScreen';
import { analyticsService } from '../services/analyticsService';
import { extractApiErrors } from '../utils/forms';
import { formatCurrency, formatNumber } from '../utils/format';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';

function CurrencyTick({ x, y, payload }) {
  return (
    <text x={x} y={y} dy={4} textAnchor="end" fill="rgba(226,232,240,0.7)" fontSize={11}>
      {payload?.value ? formatNumber(payload.value) : 0}
    </text>
  );
}

function ChartTooltip({ active, payload, label, formatter }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="glass rounded-2xl px-4 py-3 shadow-glow">
      <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">{label}</div>
      <div className="mt-2 space-y-1">
        {payload.map((p) => (
          <div key={p.dataKey} className="flex items-center justify-between gap-6 text-sm">
            <span className="text-slate-300">{p.name || p.dataKey}</span>
            <span className="font-semibold text-white">{formatter ? formatter(p.value) : p.value}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function AnalyticsPage() {
  const { restaurantId } = useParams();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [data, setData] = useState(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    analyticsService
      .getDashboard(restaurantId)
      .then((res) => {
        if (!active) return;
        setData(res.data);
      })
      .catch((err) => {
        if (!active) return;
        setError(extractApiErrors(err).message);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [restaurantId]);

  const daily = useMemo(() => {
    return (data?.dailyRevenueTrend || []).map((p) => ({
      period: p.period,
      revenue: Number(p.revenue || 0)
    }));
  }, [data]);

  const monthly = useMemo(() => {
    return (data?.monthlyRevenueTrend || []).map((p) => ({
      period: p.period,
      revenue: Number(p.revenue || 0)
    }));
  }, [data]);

  const peakHours = useMemo(() => {
    return (data?.peakOrderHours || []).map((p) => ({
      hour: p.hour ?? 0,
      orders: Number(p.orderCount || 0),
      label: `${String(p.hour ?? 0).padStart(2, '0')}:00`
    }));
  }, [data]);

  const topItems = useMemo(() => {
    return (data?.topSellingItems || []).map((it) => ({
      name: it.menuItemName || `#${it.menuItemId}`,
      qty: Number(it.quantitySold || 0),
      orders: Number(it.orderCount || 0)
    }));
  }, [data]);

  if (loading) return <LoadingScreen />;

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="Analytics"
        title="Owner analytics dashboard"
        description="Revenue, order volume, top items, and operational hotspots."
      />

      {error ? (
        <Card>
          <p className="text-sm text-red-300">{error}</p>
        </Card>
      ) : null}

      {data ? (
        <>
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <StatCard label="Total sales" value={formatCurrency(data.totalSales)} hint="Paid orders lifetime" />
            <StatCard label="Daily revenue" value={formatCurrency(data.dailyRevenue)} hint="Today (UTC window)" />
            <StatCard label="Monthly revenue" value={formatCurrency(data.monthlyRevenue)} hint="This month (UTC window)" />
            <StatCard label="Total orders" value={Number(data.totalOrders || 0)} hint="All statuses" />
          </div>

          <div className="grid gap-6 xl:grid-cols-[1.15fr_0.85fr]">
            <Card>
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold text-white">Daily revenue (last 30 days)</h2>
                  <p className="mt-2 text-sm text-slate-400">Paid orders grouped by day.</p>
                </div>
                <Badge tone="slate">{daily.length} points</Badge>
              </div>
              <div className="mt-5 h-72">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={daily} margin={{ top: 10, right: 10, left: 10, bottom: 0 }}>
                    <CartesianGrid stroke="rgba(148,163,184,0.12)" strokeDasharray="4 4" />
                    <XAxis dataKey="period" tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }} />
                    <YAxis tick={<CurrencyTick />} width={44} />
                    <Tooltip content={<ChartTooltip formatter={formatCurrency} />} />
                    <Line type="monotone" dataKey="revenue" name="Revenue" stroke="#fb923c" strokeWidth={2} dot={false} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </Card>

            <Card>
              <h2 className="text-lg font-semibold text-white">Top selling items</h2>
              <p className="mt-2 text-sm text-slate-400">By quantity sold (paid orders).</p>
              <div className="mt-5 h-72">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={topItems.slice(0, 8)} margin={{ top: 10, right: 10, left: 10, bottom: 0 }}>
                    <CartesianGrid stroke="rgba(148,163,184,0.12)" strokeDasharray="4 4" />
                    <XAxis dataKey="name" tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }} interval={0} height={64} />
                    <YAxis tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }} width={44} />
                    <Tooltip content={<ChartTooltip />} />
                    <Bar dataKey="qty" name="Quantity" fill="rgba(251,146,60,0.9)" radius={[10, 10, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </div>

          <div className="grid gap-6 xl:grid-cols-3">
            <Card className="xl:col-span-2">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold text-white">Monthly revenue (year to date)</h2>
                  <p className="mt-2 text-sm text-slate-400">Paid orders grouped by month.</p>
                </div>
                <Badge tone="slate">{monthly.length} points</Badge>
              </div>
              <div className="mt-5 h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={monthly} margin={{ top: 10, right: 10, left: 10, bottom: 0 }}>
                    <CartesianGrid stroke="rgba(148,163,184,0.12)" strokeDasharray="4 4" />
                    <XAxis dataKey="period" tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 11 }} />
                    <YAxis tick={<CurrencyTick />} width={44} />
                    <Tooltip content={<ChartTooltip formatter={formatCurrency} />} />
                    <Bar dataKey="revenue" name="Revenue" fill="rgba(56,189,248,0.85)" radius={[10, 10, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </Card>

            <Card>
              <h2 className="text-lg font-semibold text-white">Operational stats</h2>
              <p className="mt-2 text-sm text-slate-400">Quick indicators for the current dataset.</p>
              <div className="mt-5 space-y-3">
                <div className="flex items-center justify-between rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
                  <span className="text-sm text-slate-300">Cancelled orders</span>
                  <span className="text-sm font-semibold text-white">{formatNumber(Number(data.cancelledOrders || 0))}</span>
                </div>
                <div className="rounded-3xl border border-white/10 bg-white/5 p-4">
                  <p className="text-sm font-semibold text-white">Peak order hours</p>
                  <p className="mt-1 text-xs text-slate-500">Most orders placed per hour bucket.</p>
                  <div className="mt-4 h-36">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={peakHours} margin={{ top: 0, right: 10, left: 10, bottom: 0 }}>
                        <XAxis dataKey="label" tick={{ fill: 'rgba(226,232,240,0.7)', fontSize: 10 }} />
                        <YAxis hide />
                        <Tooltip content={<ChartTooltip />} />
                        <Bar dataKey="orders" name="Orders" fill="rgba(16,185,129,0.85)" radius={[10, 10, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </div>
                </div>
              </div>
            </Card>
          </div>
        </>
      ) : null}
    </div>
  );
}

