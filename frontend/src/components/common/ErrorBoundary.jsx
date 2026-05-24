import { Component } from 'react';

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { error: null };
  }

  static getDerivedStateFromError(error) {
    return { error };
  }

  componentDidCatch(error, info) {
    console.error('UI crashed', error, info);
  }

  render() {
    if (this.state.error) {
      return (
        <div className="min-h-screen bg-slate-950 px-4 py-10 text-slate-100">
          <div className="mx-auto max-w-2xl rounded-3xl border border-red-500/20 bg-red-500/10 p-6">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-red-200">Screen error</p>
            <h1 className="mt-3 text-2xl font-black text-white">App render nahi ho pa raha</h1>
            <p className="mt-3 text-sm leading-6 text-red-100">
              {this.state.error?.message || 'Unknown frontend error'}
            </p>
            <button
              type="button"
              className="mt-6 rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-slate-950"
              onClick={() => window.location.reload()}
            >
              Reload
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
