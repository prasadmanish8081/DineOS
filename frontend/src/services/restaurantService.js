import apiClient from './apiClient';

export const restaurantService = {
  create(payload) {
    return apiClient.post('/api/v1/restaurants', payload);
  },

  update(restaurantId, payload) {
    return apiClient.put(`/api/v1/restaurants/${restaurantId}`, payload);
  },

  getById(restaurantId) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}`);
  },

  getBySlug(slug) {
    return apiClient.get(`/api/v1/restaurants/slug/${slug}`);
  }
};
