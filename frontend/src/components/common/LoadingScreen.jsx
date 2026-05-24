export default function LoadingScreen() {
  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="glass flex items-center gap-3 rounded-3xl px-6 py-4 shadow-glow">
        <div className="h-3 w-3 animate-pulse rounded-full bg-accent-400" />
        <p className="text-sm font-medium text-slate-300">Loading DineOS...</p>
      </div>
    </div>
  );
}
