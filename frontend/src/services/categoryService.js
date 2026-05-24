import apiClient from './apiClient';

export const categoryService = {
  create(restaurantId, payload) {
    return apiClient.post(`/api/v1/restaurants/${restaurantId}/categories`, payload);
  },

  listByRestaurant(restaurantId) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/categories`);
  }
};
