import { useMemo } from 'react';
import { useMatch } from 'react-router-dom';

export function useRestaurantScope() {
  const match = useMatch('/app/restaurants/:restaurantId/*');

  return useMemo(() => {
    const restaurantId = match?.params?.restaurantId ? String(match.params.restaurantId) : null;
    return {
      restaurantId,
      inRestaurantScope: Boolean(restaurantId)
    };
  }, [match?.params?.restaurantId]);
}

