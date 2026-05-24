export default function Button({
  children,
  variant = 'primary',
  className = '',
  type = 'button',
  ...props
}) {
  const styles = {
    primary: 'bg-accent-500 text-white hover:bg-accent-400',
    secondary: 'bg-white/8 text-slate-100 hover:bg-white/12 border border-white/10',
    ghost: 'bg-transparent text-slate-300 hover:bg-white/8'
  };

  return (
    <button
      type={type}
      className={`inline-flex items-center justify-center rounded-2xl px-4 py-2.5 text-sm font-semibold transition ${styles[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
