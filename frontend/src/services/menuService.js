import apiClient from './apiClient';

export const menuService = {
  create(restaurantId, payload) {
    return apiClient.post(`/api/v1/restaurants/${restaurantId}/menu-items`, payload);
  },

  update(restaurantId, menuItemId, payload) {
    return apiClient.put(`/api/v1/restaurants/${restaurantId}/menu-items/${menuItemId}`, payload);
  },

  remove(restaurantId, menuItemId) {
    return apiClient.delete(`/api/v1/restaurants/${restaurantId}/menu-items/${menuItemId}`);
  },

  listByRestaurant(restaurantId, params = {}) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/menu-items`, { params });
  }
};
