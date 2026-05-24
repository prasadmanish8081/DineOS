import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import SectionHeader from '../components/common/SectionHeader';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import LoadingScreen from '../components/common/LoadingScreen';
import { restaurantService } from '../services/restaurantService';
import { extractApiErrors } from '../utils/forms';

export default function RestaurantSettingsPage() {
  const { restaurantId } = useParams();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [form, setForm] = useState({
    name: '',
    address: '',
    phone: '',
    gstNumber: '',
    logoUrl: ''
  });

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    restaurantService
      .getById(restaurantId)
      .then((res) => {
        if (!active) return;
        setForm({
          name: res.data.name || '',
          address: res.data.address || '',
          phone: res.data.phone || '',
          gstNumber: res.data.gstNumber || '',
          logoUrl: res.data.logoUrl || ''
        });
      })
      .catch((err) => {
        if (!active) return;
        setError(extractApiErrors(err).message);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [restaurantId]);

  const handleSave = async (e) => {
    e.preventDefault();
    setSuccess('');
    setError('');
    setSaving(true);
    try {
      await restaurantService.update(restaurantId, form);
      setSuccess('Settings saved.');
    } catch (err) {
      setError(extractApiErrors(err).message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="Settings"
        title="Restaurant settings"
        description="Update core details. Slug remains stable to protect QR menu links."
      />

      <Card>
        <form className="grid gap-4 sm:grid-cols-2" onSubmit={handleSave}>
          <Input label="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          <Input label="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} required />
          <div className="sm:col-span-2">
            <Input
              label="Address"
              value={form.address}
              onChange={(e) => setForm({ ...form, address: e.target.value })}
              required
            />
          </div>
          <Input
            label="GST Number"
            value={form.gstNumber}
            onChange={(e) => setForm({ ...form, gstNumber: e.target.value })}
            required
          />
          <Input
            label="Logo URL (optional)"
            value={form.logoUrl}
            onChange={(e) => setForm({ ...form, logoUrl: e.target.value })}
            placeholder="https://..."
          />

          <div className="sm:col-span-2 flex flex-col gap-2">
            {error ? <p className="text-sm text-red-300">{error}</p> : null}
            {success ? <p className="text-sm text-emerald-300">{success}</p> : null}
          </div>

          <div className="sm:col-span-2">
            <Button type="submit" disabled={saving} className="w-full sm:w-auto">
              {saving ? 'Saving...' : 'Save changes'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}

