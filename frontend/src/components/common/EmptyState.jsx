export default function EmptyState({ title, description, action }) {
  return (
    <div className="glass rounded-3xl border border-dashed border-white/10 p-8 text-center">
      <h3 className="text-lg font-semibold text-white">{title}</h3>
      {description ? <p className="mt-2 text-sm text-slate-400">{description}</p> : null}
      {action ? <div className="mt-5">{action}</div> : null}
    </div>
  );
}
