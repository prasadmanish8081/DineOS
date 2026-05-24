import Button from './Button';

export default function Pagination({ page, totalPages, onPrev, onNext }) {
  const safeTotal = Number.isFinite(totalPages) ? totalPages : 1;
  const safePage = Number.isFinite(page) ? page : 0;
  const canPrev = safePage > 0;
  const canNext = safePage + 1 < safeTotal;

  return (
    <div className="flex items-center justify-between gap-3">
      <div className="text-sm text-slate-400">
        Page <span className="font-semibold text-slate-200">{safePage + 1}</span> of{' '}
        <span className="font-semibold text-slate-200">{safeTotal}</span>
      </div>
      <div className="flex gap-2">
        <Button variant="secondary" onClick={onPrev} disabled={!canPrev}>
          Prev
        </Button>
        <Button variant="secondary" onClick={onNext} disabled={!canNext}>
          Next
        </Button>
      </div>
    </div>
  );
}

