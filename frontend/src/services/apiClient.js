import axios from 'axios';
import { API_BASE_URL, TOKEN_KEY } from '../utils/constants';
import { storage } from '../utils/storage';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use((config) => {
  const token = storage.get(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      storage.remove(TOKEN_KEY);
      storage.remove('dineos.user');
    }
    return Promise.reject(error);
  }
);

export default apiClient;
