import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import { useAuth } from '../hooks/useAuth';
import { extractApiErrors, validateRegisterForm } from '../utils/forms';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    const nextFieldErrors = validateRegisterForm(form);
    setFieldErrors(nextFieldErrors);
    if (Object.keys(nextFieldErrors).length > 0) {
      return;
    }

    setLoading(true);
    try {
      const response = await register(form);
      navigate(response.homePath || '/app', { replace: true });
    } catch (err) {
      const parsed = extractApiErrors(err);
      setError(parsed.message || 'Unable to register');
      setFieldErrors((current) => ({ ...current, ...parsed.fieldErrors }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="border-white/10">
      <div>
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-accent-300">Get started</p>
        <h1 className="mt-2 text-3xl font-black text-white">Create your account</h1>
        <p className="mt-2 text-sm text-slate-400">
          Create a restaurant account to manage your restaurant and QR ordering.
        </p>
      </div>

      <form className="mt-8 space-y-4" onSubmit={handleSubmit} noValidate>
        <Input
          label="Name"
          value={form.name}
          onChange={(e) => {
            setForm({ ...form, name: e.target.value });
            setFieldErrors((current) => ({ ...current, name: undefined }));
          }}
          placeholder="DineOS Owner"
          error={fieldErrors.name}
        />

        <Input
          label="Email"
          type="email"
          value={form.email}
          onChange={(e) => {
            setForm({ ...form, email: e.target.value });
            setFieldErrors((current) => ({ ...current, email: undefined }));
          }}
          placeholder="owner@dineos.com"
          error={fieldErrors.email}
        />

        <div className="relative">
          <Input
            label="Password"
            type={showPassword ? 'text' : 'password'}
            value={form.password}
            onChange={(e) => {
              setForm({ ...form, password: e.target.value });
              setFieldErrors((current) => ({ ...current, password: undefined }));
            }}
            placeholder="At least 8 characters"
            error={fieldErrors.password}
          />
          <button
            type="button"
            onClick={() => setShowPassword((value) => !value)}
            className="absolute right-4 top-[2.45rem] text-xs font-semibold text-accent-300"
          >
            {showPassword ? 'Hide' : 'Show'}
          </button>
        </div>

        {error ? <p className="text-sm text-red-300">{error}</p> : null}
        <Button type="submit" className="w-full" disabled={loading}>
          {loading ? 'Creating account...' : 'Create account'}
        </Button>
      </form>

      <p className="mt-6 text-sm text-slate-400">
        Already have an account?{' '}
        <Link className="font-semibold text-accent-300 hover:text-accent-200" to="/login">
          Sign in
        </Link>
      </p>
    </Card>
  );
}
