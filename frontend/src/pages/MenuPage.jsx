import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import SectionHeader from '../components/common/SectionHeader';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Select from '../components/common/Select';
import Badge from '../components/common/Badge';
import Divider from '../components/common/Divider';
import Pagination from '../components/common/Pagination';
import LoadingScreen from '../components/common/LoadingScreen';
import { categoryService } from '../services/categoryService';
import { menuService } from '../services/menuService';
import { extractApiErrors } from '../utils/forms';
import { formatCurrency, formatNumber } from '../utils/format';

const vegOptions = [
  { label: 'All', value: '' },
  { label: 'Veg', value: 'true' },
  { label: 'Non-veg', value: 'false' }
];

export default function MenuPage() {
  const { restaurantId } = useParams();

  const [categories, setCategories] = useState([]);
  const [categoryName, setCategoryName] = useState('');
  const [categoryError, setCategoryError] = useState('');
  const [categoryLoading, setCategoryLoading] = useState(false);

  const [itemsPage, setItemsPage] = useState(null);
  const [itemsLoading, setItemsLoading] = useState(true);
  const [itemsError, setItemsError] = useState('');
  const [page, setPage] = useState(0);
  const [veg, setVeg] = useState('');

  const [editing, setEditing] = useState(null);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    name: '',
    description: '',
    price: '',
    imageUrl: '',
    isVeg: false,
    isAvailable: true,
    spicyLevel: 0,
    preparationTime: 10,
    categoryId: ''
  });

  const categoryMap = useMemo(() => new Map(categories.map((c) => [String(c.id), c])), [categories]);

  const loadCategories = async () => {
    try {
      const res = await categoryService.listByRestaurant(restaurantId);
      setCategories(res.data || []);
    } catch (err) {
      setCategoryError(extractApiErrors(err).message || 'Could not load categories');
    }
  };

  const loadItems = async (nextPage = page, nextVeg = veg) => {
    setItemsLoading(true);
    setItemsError('');
    try {
      const params = { page: nextPage, size: 10 };
      if (nextVeg !== '') params.veg = nextVeg;
      const res = await menuService.listByRestaurant(restaurantId, params);
      setItemsPage(res.data);
    } catch (err) {
      setItemsError(extractApiErrors(err).message);
    } finally {
      setItemsLoading(false);
    }
  };

  useEffect(() => {
    let active = true;
    setItemsLoading(true);
    Promise.all([loadCategories(), loadItems(0, veg)])
      .catch(() => {})
      .finally(() => {
        if (active) setItemsLoading(false);
      });
    return () => {
      active = false;
    };
  }, [restaurantId]);

  const resetForm = () => {
    setEditing(null);
    setFormError('');
    setForm({
      name: '',
      description: '',
      price: '',
      imageUrl: '',
      isVeg: false,
      isAvailable: true,
      spicyLevel: 0,
      preparationTime: 10,
      categoryId: ''
    });
  };

  const startEdit = (item) => {
    setEditing(item);
    setFormError('');
    setForm({
      name: item.name || '',
      description: item.description || '',
      price: String(item.price ?? ''),
      imageUrl: item.imageUrl || '',
      isVeg: Boolean(item.isVeg),
      isAvailable: Boolean(item.isAvailable),
      spicyLevel: Number(item.spicyLevel ?? 0),
      preparationTime: Number(item.preparationTime ?? 10),
      categoryId: String(item.categoryId ?? '')
    });
  };

  const handleCreateCategory = async (e, selectAfterCreate = true) => {
    e.preventDefault();
    setCategoryError('');
    const name = categoryName.trim();
    if (!name) {
      setCategoryError('Category name is required');
      return;
    }
    setCategoryLoading(true);
    try {
      const res = await categoryService.create(restaurantId, { name });
      const createdCategory = res.data;
      setCategoryName('');
      await loadCategories();
      if (selectAfterCreate && createdCategory?.id) {
        setForm((current) => ({ ...current, categoryId: String(createdCategory.id) }));
      }
    } catch (err) {
      setCategoryError(extractApiErrors(err).message);
    } finally {
      setCategoryLoading(false);
    }
  };

  const handleSaveItem = async (e) => {
    e.preventDefault();
    setFormError('');
    if (!form.name.trim()) {
      setFormError('Menu item name is required');
      return;
    }
    if (!form.categoryId) {
      setFormError('Category is required');
      return;
    }
    const price = Number(form.price);
    if (!Number.isFinite(price) || price <= 0) {
      setFormError('Price must be a positive number');
      return;
    }
    const prep = Number(form.preparationTime);
    if (!Number.isFinite(prep) || prep <= 0) {
      setFormError('Preparation time must be a positive number');
      return;
    }

    setSaving(true);
    try {
      const payload = {
        name: form.name.trim(),
        description: form.description?.trim() || null,
        price,
        imageUrl: form.imageUrl?.trim() || null,
        isVeg: Boolean(form.isVeg),
        isAvailable: Boolean(form.isAvailable),
        spicyLevel: Number(form.spicyLevel || 0),
        preparationTime: prep,
        categoryId: Number(form.categoryId)
      };

      if (editing) {
        await menuService.update(restaurantId, editing.id, payload);
      } else {
        await menuService.create(restaurantId, payload);
      }
      resetForm();
      await loadItems(page, veg);
    } catch (err) {
      setFormError(extractApiErrors(err).message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (item) => {
    const ok = window.confirm(`Delete "${item.name}"?`);
    if (!ok) return;
    setItemsError('');
    try {
      await menuService.remove(restaurantId, item.id);
      await loadItems(page, veg);
    } catch (err) {
      setItemsError(extractApiErrors(err).message);
    }
  };

  const handleVegFilter = (value) => {
    setVeg(value);
    setPage(0);
    loadItems(0, value);
  };

  if (itemsLoading && !itemsPage) return <LoadingScreen />;

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="Menu management"
        title="Categories and items"
        description="Create categories, add menu items, and keep availability updated."
      />

      <div className="grid gap-6 xl:grid-cols-[0.9fr_1.1fr]">
        <Card>
          <h2 className="text-lg font-semibold text-white">{editing ? 'Edit menu item' : 'Add menu item'}</h2>
          <p className="mt-2 text-sm text-slate-400">
            Items are unique per restaurant by name. Category is required.
          </p>
          <form className="mt-5 space-y-4" onSubmit={handleSaveItem} noValidate>
            <Input label="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            <Input
              label="Description (optional)"
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Short description for the menu"
            />
            <div className="grid gap-4 sm:grid-cols-2">
              <Input
                label="Price"
                type="number"
                inputMode="decimal"
                value={form.price}
                onChange={(e) => setForm({ ...form, price: e.target.value })}
                placeholder="e.g. 249"
              />
              <Input
                label="Preparation time (min)"
                type="number"
                inputMode="numeric"
                value={form.preparationTime}
                onChange={(e) => setForm({ ...form, preparationTime: e.target.value })}
              />
            </div>
            <Input
              label="Image URL (optional)"
              value={form.imageUrl}
              onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
              placeholder="https://..."
            />
            <div className="grid gap-4 sm:grid-cols-2">
              <Select
                label="Category"
                value={form.categoryId}
                onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
              >
                <option value="">Select a category</option>
                {categories.map((c) => (
                  <option key={c.id} value={String(c.id)}>
                    {c.name}
                  </option>
                ))}
              </Select>
              <Select
                label="Availability"
                value={form.isAvailable ? 'true' : 'false'}
                onChange={(e) => setForm({ ...form, isAvailable: e.target.value === 'true' })}
              >
                <option value="true">Available</option>
                <option value="false">Unavailable</option>
              </Select>
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              <Select
                label="Veg"
                value={form.isVeg ? 'true' : 'false'}
                onChange={(e) => setForm({ ...form, isVeg: e.target.value === 'true' })}
              >
                <option value="false">Non-veg</option>
                <option value="true">Veg</option>
              </Select>
              <Input
                label="Spicy level"
                type="number"
                inputMode="numeric"
                value={form.spicyLevel}
                onChange={(e) => setForm({ ...form, spicyLevel: e.target.value })}
              />
            </div>

            {formError ? <p className="text-sm text-red-300">{formError}</p> : null}

            <div className="flex flex-col gap-2 sm:flex-row">
              <Button type="submit" disabled={saving} className="sm:flex-1">
                {saving ? 'Saving...' : editing ? 'Save changes' : 'Add menu item'}
              </Button>
              {editing ? (
                <Button type="button" variant="secondary" onClick={resetForm} className="sm:flex-1">
                  Cancel
                </Button>
              ) : null}
            </div>
          </form>

          <div className="mt-8">
            <Divider label="Categories" />
            <form className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-end" onSubmit={(e) => handleCreateCategory(e, false)}>
              <Input
                label="New category"
                value={categoryName}
                onChange={(e) => {
                  setCategoryName(e.target.value);
                  setCategoryError('');
                }}
                placeholder="e.g. Starters"
              />
              <Button className="sm:h-[46px]" type="submit" disabled={categoryLoading}>
                {categoryLoading ? 'Adding...' : 'Add'}
              </Button>
            </form>
            {categoryError ? <p className="mt-2 text-sm text-red-300">{categoryError}</p> : null}
            <div className="mt-4 flex flex-wrap gap-2">
              {categories.map((c) => (
                <Badge key={c.id} tone="slate">
                  {c.name}
                </Badge>
              ))}
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <h2 className="text-lg font-semibold text-white">Menu items</h2>
              <p className="mt-2 text-sm text-slate-400">
                {itemsPage ? `${formatNumber(itemsPage.totalElements)} items` : 'Items'}
              </p>
            </div>
            <Select label="Filter" value={veg} onChange={(e) => handleVegFilter(e.target.value)} className="sm:w-48">
              {vegOptions.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </Select>
          </div>

          {itemsError ? <p className="mt-4 text-sm text-red-300">{itemsError}</p> : null}

          <div className="mt-5 space-y-3">
            {(itemsPage?.content || []).map((item) => (
              <div key={item.id} className="rounded-3xl border border-white/10 bg-white/5 p-4">
                <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <div className="flex flex-wrap items-center gap-2">
                      <h3 className="text-base font-semibold text-white">{item.name}</h3>
                      <Badge tone={item.isAvailable ? 'green' : 'red'}>
                        {item.isAvailable ? 'Available' : 'Unavailable'}
                      </Badge>
                      <Badge tone={item.isVeg ? 'green' : 'amber'}>{item.isVeg ? 'Veg' : 'Non-veg'}</Badge>
                    </div>
                    <p className="mt-2 text-sm text-slate-400">{item.description || 'No description'}</p>
                    <div className="mt-3 flex flex-wrap gap-2 text-xs text-slate-400">
                      <span className="rounded-full bg-white/5 px-3 py-1">
                        Category: {categoryMap.get(String(item.categoryId))?.name || item.categoryName || item.categoryId}
                      </span>
                      <span className="rounded-full bg-white/5 px-3 py-1">Prep: {item.preparationTime}m</span>
                      <span className="rounded-full bg-white/5 px-3 py-1">Spice: {item.spicyLevel}</span>
                    </div>
                  </div>
                  <div className="flex flex-col items-start gap-2 sm:items-end">
                    <div className="text-lg font-bold text-white">{formatCurrency(item.price)}</div>
                    <div className="flex gap-2">
                      <Button variant="secondary" onClick={() => startEdit(item)}>
                        Edit
                      </Button>
                      <Button variant="ghost" onClick={() => handleDelete(item)} className="text-red-200 hover:bg-red-500/10">
                        Delete
                      </Button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-6">
            <Pagination
              page={itemsPage?.page ?? page}
              totalPages={itemsPage?.totalPages ?? 1}
              onPrev={() => {
                const next = Math.max(0, page - 1);
                setPage(next);
                loadItems(next, veg);
              }}
              onNext={() => {
                const next = page + 1;
                setPage(next);
                loadItems(next, veg);
              }}
            />
          </div>
        </Card>
      </div>
    </div>
  );
}
