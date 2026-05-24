/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: {
          50: '#f5f7fb',
          100: '#edf2f7',
          200: '#d9e2ec',
          300: '#bcccdc',
          400: '#9fb3c8',
          500: '#829ab1',
          600: '#627d98',
          700: '#486581',
          800: '#334e68',
          900: '#243b53'
        },
        accent: {
          50: '#fff7ed',
          100: '#ffedd5',
          200: '#fed7aa',
          300: '#fdba74',
          400: '#fb923c',
          500: '#f97316',
          600: '#ea580c',
          700: '#c2410c',
          800: '#9a3412',
          900: '#7c2d12'
        }
      },
      boxShadow: {
        glow: '0 20px 60px rgba(15, 23, 42, 0.16)'
      },
      backgroundImage: {
        'hero-grid': 'radial-gradient(circle at 1px 1px, rgba(148,163,184,0.18) 1px, transparent 0)',
        'soft-radial': 'radial-gradient(circle at top, rgba(249,115,22,0.18), transparent 55%)'
      }
    }
  },
  plugins: []
};
