import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { publicMenuService } from '../services/publicMenuService';
import { orderService } from '../services/orderService';
import { paymentService } from '../services/paymentService';
import LoadingScreen from '../components/common/LoadingScreen';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import { extractApiErrors } from '../utils/forms';
import { formatCurrency } from '../utils/format';

const ALL_CATEGORIES = 'all';
const VEG_FILTERS = [
  { label: 'All', value: '' },
  { label: 'Veg', value: 'true' },
  { label: 'Non-veg', value: 'false' }
];
const FALLBACK_IMAGES = [
  'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=720&q=80',
  'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=720&q=80',
  'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=720&q=80',
  'https://images.unsplash.com/photo-1600891964599-f61ba0e24092?auto=format&fit=crop&w=720&q=80'
];
const DEMO_CATEGORIES = [
  { id: 'demo-starters', name: 'Starters' },
  { id: 'demo-mains', name: 'Mains' },
  { id: 'demo-desserts', name: 'Desserts' }
];
const DEMO_ITEMS = [
  {
    id: 'demo-1',
    name: 'Paneer Tikka',
    description: 'Smoky cottage cheese with peppers and mint chutney.',
    price: 249,
    isVeg: true,
    isAvailable: true,
    preparationTime: 12,
    categoryId: 'demo-starters',
    categoryName: 'Starters'
  },
  {
    id: 'demo-2',
    name: 'Butter Chicken Bowl',
    description: 'Creamy tomato gravy, tender chicken, rice and salad.',
    price: 329,
    isVeg: false,
    isAvailable: true,
    preparationTime: 18,
    categoryId: 'demo-mains',
    categoryName: 'Mains'
  },
  {
    id: 'demo-3',
    name: 'Masala Dosa',
    description: 'Crisp dosa with potato masala, sambar and chutneys.',
    price: 179,
    isVeg: true,
    isAvailable: true,
    preparationTime: 10,
    categoryId: 'demo-mains',
    categoryName: 'Mains'
  },
  {
    id: 'demo-4',
    name: 'Chocolate Lava Cake',
    description: 'Warm cake with molten chocolate and vanilla cream.',
    price: 159,
    isVeg: true,
    isAvailable: true,
    preparationTime: 8,
    categoryId: 'demo-desserts',
    categoryName: 'Desserts'
  }
];

function loadRazorpayScript() {
  if (window.Razorpay) return Promise.resolve(true);

  return new Promise((resolve) => {
    const existing = document.querySelector('script[src="https://checkout.razorpay.com/v1/checkout.js"]');
    if (existing) {
      existing.addEventListener('load', () => resolve(true), { once: true });
      existing.addEventListener('error', () => resolve(false), { once: true });
      return;
    }

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
}

function getItemImage(item) {
  return item.imageUrl || '';
}

function getItemInitials(name = '') {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('');
}

export default function CustomerMenuPage() {
  const { slug, tableNumber } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [table, setTable] = useState(null);
  const [categories, setCategories] = useState([]);
  const [items, setItems] = useState([]);
  const [cart, setCart] = useState({});
  const [category, setCategory] = useState(ALL_CATEGORIES);
  const [veg, setVeg] = useState('');
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [placing, setPlacing] = useState(false);
  const [order, setOrder] = useState(null);
  const [paymentState, setPaymentState] = useState('');
  const [demoMode, setDemoMode] = useState(false);

  useEffect(() => {
    let active = true;

    async function loadMenu() {
      setLoading(true);
      setError('');
      setDemoMode(false);

      try {
        const response = await publicMenuService.getBySlugAndTable(slug, tableNumber);
        if (!active) return;

        const data = response.data;
        setRestaurant(data.restaurant);
        setTable(data.table);
        setCategories(data.categories || []);
        setItems(
          (data.categories || []).flatMap((category) =>
            (category.items || []).map((item) => ({
              ...item,
              categoryName: item.categoryName || category.name
            }))
          )
        );
      } catch (err) {
        if (!active) return;
        setDemoMode(true);
        setRestaurant({
          id: null,
          name: 'DineOS Demo Cafe',
          slug,
          address: 'Demo service counter',
          phone: ''
        });
        setTable({ id: null, tableNumber });
        setCategories(DEMO_CATEGORIES);
        setItems(DEMO_ITEMS);
        setError('');
        setPaymentState('Demo menu shown because live restaurant data is not available for this QR.');
      } finally {
        if (active) setLoading(false);
      }
    }

    loadMenu();

    return () => {
      active = false;
    };
  }, [slug, tableNumber]);

  const availableItems = useMemo(() => items.filter((item) => item.isAvailable), [items]);

  const visibleItems = useMemo(() => {
    const needle = query.trim().toLowerCase();

    return availableItems.filter((item) => {
      const matchesCategory = category === ALL_CATEGORIES || String(item.categoryId) === String(category);
      const matchesVeg = veg === '' || String(Boolean(item.isVeg)) === veg;
      const matchesQuery =
        !needle ||
        item.name?.toLowerCase().includes(needle) ||
        item.description?.toLowerCase().includes(needle) ||
        item.categoryName?.toLowerCase().includes(needle);

      return matchesCategory && matchesVeg && matchesQuery;
    });
  }, [availableItems, category, query, veg]);

  const cartLines = useMemo(() => {
    return Object.entries(cart)
      .map(([id, quantity]) => {
        const item = items.find((candidate) => String(candidate.id) === String(id));
        if (!item) return null;
        return {
          item,
          quantity,
          subtotal: Number(item.price || 0) * quantity
        };
      })
      .filter(Boolean);
  }, [cart, items]);

  const cartCount = cartLines.reduce((sum, line) => sum + line.quantity, 0);
  const cartTotal = cartLines.reduce((sum, line) => sum + line.subtotal, 0);

  const setQuantity = (item, quantity) => {
    const nextQuantity = Math.max(0, Number(quantity || 0));
    setOrder(null);
    setPaymentState('');
    setCart((current) => {
      const next = { ...current };
      if (nextQuantity === 0) delete next[item.id];
      else next[item.id] = nextQuantity;
      return next;
    });
  };

  const placeOrder = async () => {
    if (demoMode) {
      setOrder({
        orderNumber: `DEMO-${Date.now().toString().slice(-5)}`,
        totalAmount: cartTotal
      });
      setPaymentState('Demo order placed. Connect a valid restaurant QR to enable live payment.');
      setCart({});
      return;
    }

    if (!restaurant?.id || !table?.id) {
      setError('Order failed: restaurant or table information is missing. Please refresh and try again.');
      return;
    }
    if (cartLines.length === 0) {
      setError('Add at least one item to the cart before placing the order.');
      return;
    }

    setPlacing(true);
    setError('');
    setPaymentState('Creating order...');

    try {
      const payload = {
        tableId: table.id,
        items: cartLines.map((line) => ({
          menuItemId: line.item.id,
          quantity: line.quantity
        }))
      };
      const orderRes = await orderService.placePublicOrder(payload);
      setOrder(orderRes.data);
      setPaymentState('Preparing payment...');

      try {
        const paymentRes = await paymentService.createPublicRazorpayOrder(restaurant.id, orderRes.data.id);
        const payment = paymentRes.data;
        const razorpayReady = await loadRazorpayScript();

        if (!razorpayReady || !window.Razorpay) {
          setPaymentState(`Order created. Payment order ready: ${payment.razorpayOrderId}`);
          return;
        }

        const checkout = new window.Razorpay({
          key: payment.keyId,
          amount: Math.round(Number(payment.amount || orderRes.data.totalAmount || 0) * 100),
          currency: payment.currency || 'INR',
          name: restaurant.name,
          description: `Table ${table.tableNumber} | ${payment.orderNumber}`,
          order_id: payment.razorpayOrderId,
          handler: async (response) => {
            await paymentService.verifyPublicRazorpayPayment(restaurant.id, orderRes.data.id, {
              razorpayPaymentId: response.razorpay_payment_id,
              razorpayOrderId: response.razorpay_order_id,
              razorpaySignature: response.razorpay_signature
            });
            setPaymentState('Payment confirmed. Your order is with the kitchen.');
            setCart({});
          },
          modal: {
            ondismiss: () => setPaymentState('Payment was closed. You can retry from this cart.')
          },
          prefill: {},
          theme: { color: '#f97316' }
        });

        checkout.on('payment.failed', async (response) => {
          const errorInfo = response?.error || {};
          await paymentService.markPublicFailed(restaurant.id, orderRes.data.id, {
            razorpayPaymentId: errorInfo.metadata?.payment_id || 'unknown',
            razorpayOrderId: errorInfo.metadata?.order_id || payment.razorpayOrderId,
            errorCode: errorInfo.code,
            errorDescription: errorInfo.description
          });
          setPaymentState(errorInfo.description || 'Payment failed. Please try again.');
        });

        checkout.open();
      } catch (paymentError) {
        console.error('Payment initialization failed:', paymentError);
        const paymentMessage =
          extractApiErrors(paymentError).message ||
          paymentError?.message ||
          'Order created, but payment could not be started. Please ask staff or try again.';
        setPaymentState(paymentMessage);
      }
    } catch (err) {
      console.error('Place order error:', err);
      const message = extractApiErrors(err).message || err.message || 'Could not place this order.';
      setError(message);
      setPaymentState('');
    } finally {
      setPlacing(false);
    }
  };

  if (loading) return <LoadingScreen />;

  if (error && !restaurant) {
    return (
      <div className="mx-auto max-w-xl rounded-3xl border border-white/10 bg-slate-950/75 p-6 text-center shadow-glow">
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-accent-300">Menu unavailable</p>
        <h1 className="mt-3 text-2xl font-black leading-tight text-white">We could not load this QR menu</h1>
        <p className="mt-3 text-sm text-slate-400">{error}</p>
        <Button className="mt-6" onClick={() => window.location.reload()}>
          Try again
        </Button>
      </div>
    );
  }

  return (
    <div className="pb-32 lg:pb-8">
      <section className="overflow-hidden rounded-[2rem] border border-white/10 bg-slate-950/80 shadow-glow">
        <div className="grid gap-0 lg:grid-cols-[1fr_360px]">
          <div className="p-5 sm:p-7">
            <div className="flex flex-wrap items-start justify-between gap-4">
              <div>
                <p className="text-xs font-semibold uppercase tracking-[0.2em] text-accent-300">Scan to order</p>
                <h1 className="mt-2 text-3xl font-black tracking-tight text-white sm:text-4xl">
                  {restaurant?.name || 'Menu'}
                </h1>
                <div className="mt-3 space-y-2 text-sm leading-6 text-slate-400">
                  {restaurant?.address ? <p>{restaurant.address}</p> : null}
                  {restaurant?.phone ? <p>Phone: {restaurant.phone}</p> : null}
                  <p>Table {table?.tableNumber || tableNumber}. Fresh from the kitchen, ordered from your phone.</p>
                </div>
              </div>
              <div className="flex flex-wrap gap-2">
                {demoMode ? <Badge tone="amber">Demo mode</Badge> : null}
                <Badge tone="green">{cartCount} in cart</Badge>
              </div>
            </div>

            <div className="mt-6 grid gap-3 md:grid-cols-[1fr_auto] md:items-end">
              <label className="block">
                <span className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">Search menu</span>
                <input
                  value={query}
                  onChange={(event) => setQuery(event.target.value)}
                  placeholder="Search biryani, dosa, dessert..."
                  className="mt-2 w-full rounded-2xl border border-white/10 bg-white/8 px-4 py-3 text-sm text-white outline-none transition placeholder:text-slate-500 focus:border-accent-400"
                />
              </label>

              <div className="flex rounded-2xl border border-white/10 bg-white/5 p-1">
                {VEG_FILTERS.map((filter) => (
                  <button
                    key={filter.label}
                    type="button"
                    onClick={() => setVeg(filter.value)}
                    className={`rounded-xl px-3 py-2 text-xs font-semibold transition ${
                      veg === filter.value ? 'bg-accent-500 text-white' : 'text-slate-400 hover:text-white'
                    }`}
                  >
                    {filter.label}
                  </button>
                ))}
              </div>
            </div>

            <div className="mt-5 flex gap-2 overflow-x-auto pb-2">
              <button
                type="button"
                onClick={() => setCategory(ALL_CATEGORIES)}
                className={`shrink-0 rounded-full px-4 py-2 text-sm font-semibold transition ${
                  category === ALL_CATEGORIES ? 'bg-white text-slate-950' : 'bg-white/8 text-slate-300'
                }`}
              >
                All
              </button>
              {categories.map((cat) => (
                <button
                  key={cat.id}
                  type="button"
                  onClick={() => setCategory(String(cat.id))}
                  className={`shrink-0 rounded-full px-4 py-2 text-sm font-semibold transition ${
                    String(category) === String(cat.id) ? 'bg-white text-slate-950' : 'bg-white/8 text-slate-300'
                  }`}
                >
                  {cat.name}
                </button>
              ))}
            </div>

            <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              {visibleItems.map((item) => {
                const quantity = cart[item.id] || 0;
                return (
                  <article key={item.id} className="overflow-hidden rounded-3xl border border-white/10 bg-white/5">
                    <div className="aspect-[4/3] overflow-hidden bg-slate-900">
                      {getItemImage(item) ? (
                        <img
                          src={getItemImage(item)}
                          alt={item.name}
                          className="h-full w-full object-cover transition duration-300 hover:scale-105"
                          loading="lazy"
                        />
                      ) : (
                        <div className="flex h-full w-full items-center justify-center bg-[radial-gradient(circle_at_top_left,rgba(249,115,22,0.45),transparent_42%),linear-gradient(135deg,#111827,#0f766e)]">
                          <div className="rounded-3xl border border-white/20 bg-white/10 px-5 py-4 text-2xl font-black text-white">
                            {getItemInitials(item.name)}
                          </div>
                        </div>
                      )}
                    </div>
                    <div className="p-4">
                      <div className="flex items-start justify-between gap-3">
                        <div className="min-w-0">
                          <h2 className="truncate text-base font-bold text-white">{item.name}</h2>
                          <p className="mt-1 h-10 overflow-hidden text-sm leading-5 text-slate-400">
                            {item.description || item.categoryName || 'Chef recommended'}
                          </p>
                        </div>
                        <Badge tone={item.isVeg ? 'green' : 'amber'}>{item.isVeg ? 'Veg' : 'Non-veg'}</Badge>
                      </div>
                      <div className="mt-4 flex items-center justify-between gap-3">
                        <div>
                          <p className="text-lg font-black text-white">{formatCurrency(item.price)}</p>
                          <p className="text-xs text-slate-500">{item.preparationTime || 10} min</p>
                        </div>
                        {quantity ? (
                          <div className="flex items-center rounded-2xl border border-white/10 bg-slate-950/60">
                            <button
                              type="button"
                              className="px-3 py-2 text-lg font-bold text-white"
                              onClick={() => setQuantity(item, quantity - 1)}
                            >
                              -
                            </button>
                            <span className="min-w-8 text-center text-sm font-bold text-white">{quantity}</span>
                            <button
                              type="button"
                              className="px-3 py-2 text-lg font-bold text-white"
                              onClick={() => setQuantity(item, quantity + 1)}
                            >
                              +
                            </button>
                          </div>
                        ) : (
                          <Button onClick={() => setQuantity(item, 1)}>Add</Button>
                        )}
                      </div>
                    </div>
                  </article>
                );
              })}
            </div>

            {!visibleItems.length ? (
              <div className="mt-8 rounded-3xl border border-dashed border-white/10 p-8 text-center">
                <p className="font-semibold text-white">No dishes found</p>
                <p className="mt-2 text-sm text-slate-400">Try another category or clear your search.</p>
              </div>
            ) : null}
          </div>

          <aside className="hidden border-l border-white/10 bg-slate-950/70 p-5 lg:block">
            <CartPanel
              cartLines={cartLines}
              cartTotal={cartTotal}
              error={error}
              order={order}
              paymentState={paymentState}
              placing={placing}
              placeOrder={placeOrder}
              setQuantity={setQuantity}
              tableNumber={table?.tableNumber || tableNumber}
            />
          </aside>
        </div>
      </section>

      <div className="fixed inset-x-0 bottom-0 z-30 border-t border-white/10 bg-slate-950/95 p-4 backdrop-blur lg:hidden">
        <CartPanel
          compact
          cartLines={cartLines}
          cartTotal={cartTotal}
          error={error}
          order={order}
          paymentState={paymentState}
          placing={placing}
          placeOrder={placeOrder}
          setQuantity={setQuantity}
          tableNumber={table?.tableNumber || tableNumber}
        />
      </div>
    </div>
  );
}

function CartPanel({
  cartLines,
  cartTotal,
  compact = false,
  error,
  order,
  paymentState,
  placing,
  placeOrder,
  setQuantity,
  tableNumber
}) {
  return (
    <div className={compact ? '' : 'sticky top-6'}>
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.18em] text-accent-300">Your cart</p>
          <h2 className="mt-1 text-xl font-black text-white">Table {tableNumber}</h2>
        </div>
        <p className="text-right text-lg font-black text-white">{formatCurrency(cartTotal)}</p>
      </div>

      {!compact ? (
        <div className="mt-5 max-h-[44vh] space-y-3 overflow-y-auto pr-1">
          {cartLines.length ? (
            cartLines.map((line) => (
              <div key={line.item.id} className="rounded-2xl border border-white/10 bg-white/5 p-3">
                <div className="flex items-start justify-between gap-3">
                  <div className="min-w-0">
                    <p className="truncate text-sm font-semibold text-white">{line.item.name}</p>
                    <p className="mt-1 text-xs text-slate-500">{formatCurrency(line.item.price)} each</p>
                  </div>
                  <p className="text-sm font-bold text-white">{formatCurrency(line.subtotal)}</p>
                </div>
                <div className="mt-3 flex w-fit items-center rounded-2xl border border-white/10 bg-slate-950/60">
                  <button className="px-3 py-1.5 text-white" type="button" onClick={() => setQuantity(line.item, line.quantity - 1)}>
                    -
                  </button>
                  <span className="min-w-8 text-center text-sm font-bold text-white">{line.quantity}</span>
                  <button className="px-3 py-1.5 text-white" type="button" onClick={() => setQuantity(line.item, line.quantity + 1)}>
                    +
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="rounded-3xl border border-dashed border-white/10 p-6 text-center text-sm text-slate-400">
              Add dishes to start your order.
            </div>
          )}
        </div>
      ) : null}

      {error ? <p className="mt-3 text-sm text-red-300">{error}</p> : null}
      {order ? (
        <p className="mt-3 text-sm text-emerald-300">
          Order {order.orderNumber} created for {formatCurrency(order.totalAmount)}.
        </p>
      ) : null}
      {paymentState ? <p className="mt-2 text-sm text-slate-300">{paymentState}</p> : null}

      <Button className="mt-4 w-full" disabled={!cartLines.length || placing} onClick={placeOrder}>
        {placing ? 'Placing order...' : cartLines.length ? 'Place order & pay' : 'Cart is empty'}
      </Button>
    </div>
  );
}
