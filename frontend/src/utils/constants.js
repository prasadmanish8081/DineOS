export const APP_NAME = 'DineOS';

const defaultApiHost = typeof window !== 'undefined' ? window.location.hostname : 'localhost';
const defaultApiProtocol = typeof window !== 'undefined' ? window.location.protocol : 'http:';
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  `${defaultApiProtocol}//${defaultApiHost}:8080`;

export const TOKEN_KEY = 'dineos.token';
export const USER_KEY = 'dineos.user';
