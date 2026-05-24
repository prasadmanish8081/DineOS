import apiClient from './apiClient';

export const authService = {
  register(payload) {
    return apiClient.post('/api/v1/auth/register', payload);
  },

  login(payload) {
    return apiClient.post('/api/v1/auth/login', payload);
  },

  me() {
    return apiClient.get('/api/v1/users/me');
  }
};
