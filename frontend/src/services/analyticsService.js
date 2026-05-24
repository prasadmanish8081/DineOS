import apiClient from './apiClient';

export const analyticsService = {
  getDashboard(restaurantId) {
    return apiClient.get(`/api/v1/restaurants/${restaurantId}/analytics/dashboard`);
  }
};
