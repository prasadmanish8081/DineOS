import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import SectionHeader from '../components/common/SectionHeader';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Select from '../components/common/Select';
import Button from '../components/common/Button';
import Pagination from '../components/common/Pagination';
import LoadingScreen from '../components/common/LoadingScreen';
import { orderService } from '../services/orderService';
import { extractApiErrors } from '../utils/forms';
import { formatCurrency, formatDateTime, formatNumber } from '../utils/format';

const statusOptions = [
  'PLACED',
  'ACCEPTED',
  'PREPARING',
  'READY',
  'SERVED',
  'COMPLETED',
  'CANCELLED'
];

function toneForStatus(status) {
  switch (status) {
    case 'PLACED':
      return 'blue';
    case 'ACCEPTED':
      return 'amber';
    case 'PREPARING':
      return 'amber';
    case 'READY':
      return 'green';
    case 'SERVED':
      return 'green';
    case 'COMPLETED':
      return 'slate';
    case 'CANCELLED':
      return 'red';
    default:
      return 'slate';
  }
}

function toneForPayment(status) {
  switch (status) {
    case 'PAID':
      return 'green';
    case 'FAILED':
      return 'red';
    case 'PENDING':
      return 'amber';
    default:
      return 'slate';
  }
}

export default function OrdersPage() {
  const { restaurantId } = useParams();
  const [mode, setMode] = useState('pending'); // pending | all
  const [page, setPage] = useState(0);
  const [ordersPage, setOrdersPage] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updatingId, setUpdatingId] = useState(null);

  const loadOrders = async (nextPage = page, nextMode = mode) => {
    setLoading(true);
    setError('');
    try {
      const params = { page: nextPage, size: 10 };
      const res =
        nextMode === 'pending'
          ? await orderService.listPending(restaurantId, params)
          : await orderService.listByRestaurant(restaurantId, params);
      setOrdersPage(res.data);
    } catch (err) {
      setError(extractApiErrors(err).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders(0, mode);
  }, [restaurantId]);

  const orders = ordersPage?.content || [];
  const total = ordersPage?.totalElements ?? orders.length;

  const grouped = useMemo(() => {
    const pending = [];
    const done = [];
    for (const order of orders) {
      if (order.status === 'COMPLETED' || order.status === 'CANCELLED') done.push(order);
      else pending.push(order);
    }
    return { pending, done };
  }, [orders]);

  const updateStatus = async (orderId, status) => {
    setUpdatingId(orderId);
    setError('');
    try {
      await orderService.updateStatus(restaurantId, orderId, { status });
      await loadOrders(page, mode);
    } catch (err) {
      setError(extractApiErrors(err).message);
    } finally {
      setUpdatingId(null);
    }
  };

  if (loading && !ordersPage) return <LoadingScreen />;

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="Orders"
        title="Order operations"
        description="Review incoming orders and update their statuses as the kitchen progresses."
      />

      <Card>
        <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h2 className="text-lg font-semibold text-white">Orders</h2>
            <p className="mt-2 text-sm text-slate-400">{formatNumber(total)} total</p>
          </div>
          <Select
            label="View"
            value={mode}
            onChange={(e) => {
              const next = e.target.value;
              setMode(next);
              setPage(0);
              loadOrders(0, next);
            }}
            className="sm:w-56"
          >
            <option value="pending">Live pending orders</option>
            <option value="all">All orders</option>
          </Select>
        </div>

        {error ? <p className="mt-4 text-sm text-red-300">{error}</p> : null}

        <div className="mt-6 space-y-4">
          {orders.length === 0 ? (
            <div className="rounded-3xl border border-dashed border-white/10 p-8 text-center text-sm text-slate-400">
              No orders found for this view.
            </div>
          ) : null}

          {orders.map((order) => (
            <div key={order.id} className="rounded-3xl border border-white/10 bg-white/5 p-4">
              <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                <div className="min-w-0">
                  <div className="flex flex-wrap items-center gap-2">
                    <h3 className="text-base font-semibold text-white">{order.orderNumber}</h3>
                    <Badge tone={toneForStatus(order.status)}>{order.status}</Badge>
                    <Badge tone={toneForPayment(order.paymentStatus)}>{order.paymentStatus}</Badge>
                    <Badge tone="slate">Table {order.tableNumber}</Badge>
                  </div>
                  <p className="mt-2 text-sm text-slate-400">
                    {formatDateTime(order.createdAt)} • Total {formatCurrency(order.totalAmount)}
                  </p>

                  <div className="mt-4 space-y-2">
                    {(order.items || []).slice(0, 5).map((it) => (
                      <div key={it.id} className="flex flex-wrap items-center justify-between gap-3 text-sm">
                        <div className="text-slate-200">
                          {it.menuItemName} <span className="text-slate-500">x</span> {it.quantity}
                        </div>
                        <div className="text-slate-300">{formatCurrency(it.subtotal)}</div>
                      </div>
                    ))}
                    {(order.items || []).length > 5 ? (
                      <div className="text-xs text-slate-500">+ {order.items.length - 5} more items</div>
                    ) : null}
                  </div>
                </div>

                <div className="w-full max-w-sm">
                  <Select
                    label="Update status"
                    value={order.status}
                    onChange={(e) => updateStatus(order.id, e.target.value)}
                    disabled={updatingId === order.id}
                  >
                    {statusOptions.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </Select>
                  <div className="mt-3 flex gap-2">
                    <Button
                      variant="secondary"
                      className="flex-1"
                      disabled={updatingId === order.id}
                      onClick={() => updateStatus(order.id, order.status)}
                    >
                      {updatingId === order.id ? 'Updating...' : 'Refresh'}
                    </Button>
                    <Button
                      variant="ghost"
                      className="flex-1 text-slate-200 hover:bg-white/10"
                      onClick={() => navigator.clipboard?.writeText(order.orderNumber)}
                    >
                      Copy #
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="mt-6">
          <Pagination
            page={ordersPage?.page ?? page}
            totalPages={ordersPage?.totalPages ?? 1}
            onPrev={() => {
              const next = Math.max(0, page - 1);
              setPage(next);
              loadOrders(next, mode);
            }}
            onNext={() => {
              const next = page + 1;
              setPage(next);
              loadOrders(next, mode);
            }}
          />
        </div>
      </Card>

      {mode === 'all' ? (
        <Card>
          <h3 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-400">Quick glance</h3>
          <div className="mt-4 grid gap-4 md:grid-cols-2">
            <div className="rounded-3xl border border-white/10 bg-white/5 p-5">
              <p className="text-sm text-slate-400">Active orders in this page</p>
              <p className="mt-2 text-2xl font-black text-white">{formatNumber(grouped.pending.length)}</p>
            </div>
            <div className="rounded-3xl border border-white/10 bg-white/5 p-5">
              <p className="text-sm text-slate-400">Completed/cancelled in this page</p>
              <p className="mt-2 text-2xl font-black text-white">{formatNumber(grouped.done.length)}</p>
            </div>
          </div>
        </Card>
      ) : null}
    </div>
  );
}

