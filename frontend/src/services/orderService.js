import apiClient from './apiClient';

export const orderService = {
  placeOrder(restaurantId, tableId, payload) {
    return apiClient.post(`/api/v1/restaurants/${restaurantId}/tables/${tableId}/orders`, payload);
  },

  placePublicOrder(payload) {
    return apiClient.post('/api/v1/public/orders', payload);
  },

  updateStatus(restaurantId, orderId, payload) {
    return apiClient.patch(`/api/v1/restaurants/${restaurantId}/orders/${orderId}/status`, payload);
  },

  listByRestaurant(restaurantId, params = {}) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/orders`, { params });
  },

  listPending(restaurantId, params = {}) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/orders/pending`, { params });
  }
};
