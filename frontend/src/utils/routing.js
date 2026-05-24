export function buildRestaurantPath(restaurantId, subPath = '') {
  const clean = String(restaurantId || '').trim();
  if (!clean) return '/app/restaurants';
  const tail = subPath.startsWith('/') ? subPath : `/${subPath}`;
  return `/app/restaurants/${clean}${subPath ? tail : ''}`;
}

