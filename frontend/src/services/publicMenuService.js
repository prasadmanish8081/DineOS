import apiClient from './apiClient';

export const publicMenuService = {
  getBySlugAndTable(slug, tableNumber) {
    return apiClient.get(`/api/v1/public/menu/${encodeURIComponent(slug)}/table/${encodeURIComponent(tableNumber)}`);
  }
};
