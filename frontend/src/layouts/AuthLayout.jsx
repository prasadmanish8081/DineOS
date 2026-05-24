import { APP_NAME } from '../utils/constants';

export default function AuthLayout({ children }) {
  return (
    <div className="grid min-h-screen lg:grid-cols-[1.15fr_0.85fr]">
      <aside className="hidden overflow-hidden border-r border-white/10 bg-[radial-gradient(circle_at_top,_rgba(249,115,22,0.18),_transparent_45%),linear-gradient(180deg,#020617_0%,#0f172a_100%)] p-10 lg:flex lg:flex-col lg:justify-between">
        <div>
          <div className="inline-flex rounded-full border border-white/10 bg-white/5 px-4 py-2 text-xs font-semibold uppercase tracking-[0.24em] text-accent-200">
            {APP_NAME}
          </div>
          <h1 className="mt-8 max-w-xl text-5xl font-black leading-tight text-white">
            Run your restaurant operations from one calm, connected dashboard.
          </h1>
          <p className="mt-6 max-w-lg text-sm leading-7 text-slate-300">
            Manage restaurants, tables, menus, orders, analytics, and payments with a frontend
            built for speed and scale.
          </p>
        </div>
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div className="rounded-3xl border border-white/10 bg-white/5 p-4">
            <p className="text-slate-400">JWT auth</p>
            <p className="mt-2 font-semibold text-white">Secure</p>
          </div>
          <div className="rounded-3xl border border-white/10 bg-white/5 p-4">
            <p className="text-slate-400">Routing</p>
            <p className="mt-2 font-semibold text-white">Nested</p>
          </div>
          <div className="rounded-3xl border border-white/10 bg-white/5 p-4">
            <p className="text-slate-400">API</p>
            <p className="mt-2 font-semibold text-white">Axios</p>
          </div>
        </div>
      </aside>
      <main className="flex items-center justify-center px-4 py-10 sm:px-8">
        <div className="w-full max-w-md">{children}</div>
      </main>
    </div>
  );
}
