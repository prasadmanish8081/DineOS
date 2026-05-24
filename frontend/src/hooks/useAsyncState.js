import { useState } from 'react';

export function useAsyncState(initialValue = null) {
  const [value, setValue] = useState(initialValue);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  return {
    value,
    setValue,
    loading,
    setLoading,
    error,
    setError
  };
}
