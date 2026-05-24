export default function Divider({ label }) {
  return (
    <div className="flex items-center gap-4">
      <div className="h-px flex-1 bg-white/10" />
      {label ? <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">{label}</div> : null}
      <div className="h-px flex-1 bg-white/10" />
    </div>
  );
}

