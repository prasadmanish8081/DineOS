import SectionHeader from '../components/common/SectionHeader';
import EmptyState from '../components/common/EmptyState';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import { useState } from 'react';
import { restaurantService } from '../services/restaurantService';
import { useNavigate } from 'react-router-dom';
import { extractApiErrors } from '../utils/forms';

export default function RestaurantsPage() {
  const navigate = useNavigate();
  const [lookupId, setLookupId] = useState('');
  const [create, setCreate] = useState({
    name: '',
    address: '',
    phone: '',
    gstNumber: '',
    logoUrl: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const openRestaurant = () => {
    const id = String(lookupId || '').trim();
    if (!id) return;
    navigate(`/app/restaurants/${id}`);
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await restaurantService.create(create);
      navigate(`/app/restaurants/${response.data.id}`);
    } catch (err) {
      setError(extractApiErrors(err).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="Restaurants"
        title="Restaurant management"
        description="Create a restaurant or open an existing one by ID."
      />

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <h2 className="text-lg font-semibold text-white">Open restaurant</h2>
          <p className="mt-2 text-sm text-slate-400">Backend does not expose a list endpoint yet, so open by ID.</p>
          <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:items-end">
            <Input
              label="Restaurant ID"
              value={lookupId}
              onChange={(e) => setLookupId(e.target.value)}
              placeholder="e.g. 1"
            />
            <Button className="sm:mb-0 sm:h-[46px]" onClick={openRestaurant}>
              Open
            </Button>
          </div>
        </Card>

        <Card>
          <h2 className="text-lg font-semibold text-white">Create restaurant</h2>
          <p className="mt-2 text-sm text-slate-400">Owners can create one restaurant per account.</p>
          <form className="mt-5 space-y-4" onSubmit={handleCreate}>
            <Input label="Name" value={create.name} onChange={(e) => setCreate({ ...create, name: e.target.value })} required />
            <Input
              label="Address"
              value={create.address}
              onChange={(e) => setCreate({ ...create, address: e.target.value })}
              required
            />
            <div className="grid gap-4 sm:grid-cols-2">
              <Input label="Phone" value={create.phone} onChange={(e) => setCreate({ ...create, phone: e.target.value })} required />
              <Input
                label="GST Number"
                value={create.gstNumber}
                onChange={(e) => setCreate({ ...create, gstNumber: e.target.value })}
                required
              />
            </div>
            <Input
              label="Logo URL (optional)"
              value={create.logoUrl}
              onChange={(e) => setCreate({ ...create, logoUrl: e.target.value })}
              placeholder="https://..."
            />
            {error ? <p className="text-sm text-red-300">{error}</p> : null}
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? 'Creating...' : 'Create restaurant'}
            </Button>
          </form>
        </Card>
      </div>

      <EmptyState title="Next improvement" description="Add a backend endpoint to list restaurants for the current owner." />
    </div>
  );
}
