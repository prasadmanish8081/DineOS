import { formatNumber } from '../../utils/format';

export default function StatCard({ label, value, hint, tone = 'from-slate-800 to-slate-900' }) {
  return (
    <div className={`rounded-3xl border border-white/10 bg-gradient-to-br ${tone} p-5 shadow-glow`}>
      <p className="text-sm text-slate-400">{label}</p>
      <div className="mt-2 text-3xl font-bold tracking-tight text-white">
        {typeof value === 'number' ? formatNumber(value) : value}
      </div>
      {hint ? <p className="mt-2 text-xs text-slate-500">{hint}</p> : null}
    </div>
  );
}
