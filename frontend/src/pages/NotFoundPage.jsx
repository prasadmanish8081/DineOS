import { Link } from 'react-router-dom';
import Button from '../components/common/Button';

export default function NotFoundPage() {
  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="glass max-w-lg rounded-3xl p-8 text-center shadow-glow">
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-accent-300">404</p>
        <h1 className="mt-3 text-3xl font-black text-white">Page not found</h1>
        <p className="mt-3 text-sm text-slate-400">The route you requested does not exist in this frontend.</p>
        <div className="mt-6">
          <Link to="/app">
            <Button>Back to dashboard</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
