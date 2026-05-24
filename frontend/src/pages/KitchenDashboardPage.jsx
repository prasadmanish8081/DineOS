import { useParams } from 'react-router-dom';
import SectionHeader from '../components/common/SectionHeader';
import Card from '../components/common/Card';

export default function KitchenDashboardPage() {
  const { restaurantId } = useParams();

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="Kitchen"
        title={`Kitchen live board #${restaurantId}`}
        description="This page is intended for live order streams from the backend WebSocket topics."
      />
      <Card>
        <p className="text-sm text-slate-400">Subscribe here to NEW_ORDER, ORDER_UPDATED, and ORDER_READY.</p>
      </Card>
    </div>
  );
}
