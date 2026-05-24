export default function Badge({ children, tone = 'slate' }) {
  const tones = {
    slate: 'bg-white/5 text-slate-200 border-white/10',
    green: 'bg-emerald-500/10 text-emerald-200 border-emerald-500/20',
    amber: 'bg-amber-500/10 text-amber-200 border-amber-500/20',
    red: 'bg-red-500/10 text-red-200 border-red-500/20',
    blue: 'bg-sky-500/10 text-sky-200 border-sky-500/20'
  };

  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-semibold ${tones[tone] || tones.slate}`}>
      {children}
    </span>
  );
}

