import apiClient from './apiClient';

export const tableService = {
  create(restaurantId, payload) {
    return apiClient.post(`/api/v1/restaurants/${restaurantId}/tables`, payload);
  },

  get(restaurantId, tableId) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/tables/${tableId}`);
  },

  getByNumber(restaurantId, tableNumber) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/tables/by-number/${encodeURIComponent(tableNumber)}`);
  },

  getQrCode(restaurantId, tableId) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/tables/${tableId}/qr-code`, {
      responseType: 'blob'
    });
  }
};
