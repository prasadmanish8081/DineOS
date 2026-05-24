import apiClient from './apiClient';

export const paymentService = {
  createRazorpayOrder(restaurantId, orderId) {
    return apiClient.post(`/api/v1/restaurants/${restaurantId}/orders/${orderId}/payments/razorpay/create-order`);
  },

  createPublicRazorpayOrder(restaurantId, orderId) {
    return apiClient.post(`/api/v1/public/restaurants/${restaurantId}/orders/${orderId}/payments/razorpay/create-order`);
  },

  verifyRazorpayPayment(restaurantId, orderId, payload) {
    return apiClient.post(`/api/v1/restaurants/${restaurantId}/orders/${orderId}/payments/razorpay/verify`, payload);
  },

  verifyPublicRazorpayPayment(restaurantId, orderId, payload) {
    return apiClient.post(`/api/v1/public/restaurants/${restaurantId}/orders/${orderId}/payments/razorpay/verify`, payload);
  },

  markFailed(restaurantId, orderId, payload) {
    return apiClient.post(`/api/v1/restaurants/${restaurantId}/orders/${orderId}/payments/razorpay/failed`, payload);
  },

  markPublicFailed(restaurantId, orderId, payload) {
    return apiClient.post(`/api/v1/public/restaurants/${restaurantId}/orders/${orderId}/payments/razorpay/failed`, payload);
  }
};
