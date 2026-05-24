export default function Select({ label, error, className = '', children, ...props }) {
  return (
    <label className={`block ${className}`}>
      {label ? <span className="mb-2 block text-sm font-medium text-slate-300">{label}</span> : null}
      <select
        className="w-full rounded-2xl border border-white/10 bg-slate-900/80 px-4 py-3 text-slate-100 outline-none transition focus:border-accent-400"
        {...props}
      >
        {children}
      </select>
      {error ? <p className="mt-2 text-xs text-red-300">{error}</p> : null}
    </label>
  );
}

