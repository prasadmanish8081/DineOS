import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import SectionHeader from '../components/common/SectionHeader';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import { tableService } from '../services/tableService';
import { extractApiErrors } from '../utils/forms';
import { downloadBlob } from '../utils/blob';

export default function QrTablesPage() {
  const { restaurantId } = useParams();
  const [creating, setCreating] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [tableNumber, setTableNumber] = useState('');
  const [tableIdLookup, setTableIdLookup] = useState('');
  const [table, setTable] = useState(null);
  const [qrUrl, setQrUrl] = useState('');
  const [qrLoading, setQrLoading] = useState(false);

  useEffect(() => {
    return () => {
      if (qrUrl) URL.revokeObjectURL(qrUrl);
    };
  }, [qrUrl]);

  const loadTable = async (tableId) => {
    setLoading(true);
    setError('');
    try {
      const res = await tableService.get(restaurantId, tableId);
      setTable(res.data);
    } catch (err) {
      setError(extractApiErrors(err).message);
      setTable(null);
    } finally {
      setLoading(false);
    }
  };

  const createTable = async (e) => {
    e.preventDefault();
    setError('');
    const tn = tableNumber.trim();
    if (!tn) {
      setError('Table number is required');
      return;
    }
    setCreating(true);
    try {
      const res = await tableService.create(restaurantId, { tableNumber: tn });
      setTable(res.data);
      setTableNumber('');
      setTableIdLookup(String(res.data.id));
    } catch (err) {
      setError(extractApiErrors(err).message);
    } finally {
      setCreating(false);
    }
  };

  const fetchQr = async () => {
    if (!table?.id) return;
    setQrLoading(true);
    setError('');
    try {
      const res = await tableService.getQrCode(restaurantId, table.id);
      const blob = res.data;
      if (qrUrl) URL.revokeObjectURL(qrUrl);
      setQrUrl(URL.createObjectURL(blob));
    } catch (err) {
      setError(extractApiErrors(err).message);
    } finally {
      setQrLoading(false);
    }
  };

  const downloadQr = async () => {
    if (!table?.id) return;
    setQrLoading(true);
    setError('');
    try {
      const res = await tableService.getQrCode(restaurantId, table.id);
      downloadBlob(res.data, `table-${table.tableNumber}-qr.png`);
    } catch (err) {
      setError(extractApiErrors(err).message);
    } finally {
      setQrLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <SectionHeader
        eyebrow="QR tables"
        title="Generate QR codes for tables"
        description="Create a table and generate a QR that points to /menu/{restaurantSlug}/table/{tableNumber}."
      />

      <div className="grid gap-6 xl:grid-cols-[0.9fr_1.1fr]">
        <Card>
          <h2 className="text-lg font-semibold text-white">Create table</h2>
          <p className="mt-2 text-sm text-slate-400">Table numbers must be unique per restaurant.</p>
          <form className="mt-5 flex flex-col gap-3 sm:flex-row sm:items-end" onSubmit={createTable}>
            <Input
              label="Table number"
              value={tableNumber}
              onChange={(e) => setTableNumber(e.target.value)}
              placeholder="e.g. A1, 10, Patio-3"
            />
            <Button type="submit" disabled={creating} className="sm:h-[46px]">
              {creating ? 'Creating...' : 'Create'}
            </Button>
          </form>

          <div className="mt-8">
            <h3 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-400">Find table</h3>
            <p className="mt-2 text-sm text-slate-400">
              Backend currently supports get-by-id. If you want a full list view, we can add a list endpoint.
            </p>
            <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-end">
              <Input
                label="Table ID"
                value={tableIdLookup}
                onChange={(e) => setTableIdLookup(e.target.value)}
                placeholder="e.g. 12"
              />
              <Button
                variant="secondary"
                className="sm:h-[46px]"
                onClick={() => {
                  const id = String(tableIdLookup || '').trim();
                  if (id) loadTable(id);
                }}
                disabled={loading}
              >
                {loading ? 'Loading...' : 'Load'}
              </Button>
            </div>
          </div>

          {error ? <p className="mt-4 text-sm text-red-300">{error}</p> : null}
        </Card>

        <Card>
          <h2 className="text-lg font-semibold text-white">Table details</h2>
          {!table ? (
            <div className="mt-5 rounded-3xl border border-dashed border-white/10 p-8 text-center text-sm text-slate-400">
              Create or load a table to see its QR code.
            </div>
          ) : (
            <>
              <div className="mt-4 flex flex-wrap items-center gap-2">
                <Badge tone="slate">Table {table.tableNumber}</Badge>
                <Badge tone="blue">ID {table.id}</Badge>
                <Badge tone="slate">Slug {table.restaurantSlug}</Badge>
              </div>

              <div className="mt-5 grid gap-4 lg:grid-cols-[0.9fr_1.1fr]">
                <div className="rounded-3xl border border-white/10 bg-white/5 p-4">
                  <p className="text-sm font-semibold text-white">Menu URL</p>
                  <p className="mt-2 break-all text-sm text-slate-300">
                    /menu/{table.restaurantSlug}/table/{table.tableNumber}
                  </p>
                  <div className="mt-4 flex flex-col gap-2 sm:flex-row">
                    <Button onClick={fetchQr} disabled={qrLoading}>
                      {qrLoading ? 'Generating...' : 'Generate QR'}
                    </Button>
                    <Button variant="secondary" onClick={downloadQr} disabled={qrLoading}>
                      Download PNG
                    </Button>
                  </div>
                </div>

                <div className="rounded-3xl border border-white/10 bg-white/5 p-4">
                  <p className="text-sm font-semibold text-white">QR preview</p>
                  <p className="mt-1 text-xs text-slate-500">Generated by the backend as a PNG image.</p>
                  <div className="mt-4 flex items-center justify-center rounded-2xl bg-slate-950/40 p-4">
                    {qrUrl ? (
                      <img
                        src={qrUrl}
                        alt={`Table ${table.tableNumber} QR`}
                        className="h-52 w-52 rounded-2xl bg-white p-3"
                      />
                    ) : (
                      <div className="text-sm text-slate-500">Generate to preview</div>
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </Card>
      </div>
    </div>
  );
}
