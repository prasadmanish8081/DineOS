import { APP_NAME } from '../utils/constants';

export default function PublicLayout({ children }) {
  return (
    <div className="min-h-screen px-3 py-4 sm:px-6">
      <header className="mx-auto flex max-w-7xl items-center justify-between rounded-3xl border border-white/10 bg-slate-950/80 px-4 py-3 shadow-glow backdrop-blur">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-accent-300">{APP_NAME}</p>
          <p className="text-sm font-bold text-white">QR ordering</p>
        </div>
        <div className="rounded-full bg-emerald-500/10 px-3 py-1 text-xs font-semibold text-emerald-200">
          Live
        </div>
      </header>
      <main className="mx-auto mt-4 max-w-7xl">{children}</main>
    </div>
  );
}
