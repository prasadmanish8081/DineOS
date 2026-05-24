import { API_BASE_URL } from './constants';

export function validateLoginForm(values) {
  const errors = {};
  if (!values.email?.trim()) {
    errors.email = 'Email is required';
  } else if (!/^\S+@\S+\.\S+$/.test(values.email)) {
    errors.email = 'Enter a valid email address';
  }

  if (!values.password?.trim()) {
    errors.password = 'Password is required';
  }

  return errors;
}

export function validateRegisterForm(values) {
  const errors = validateLoginForm(values);

  if (!values.name?.trim()) {
    errors.name = 'Name is required';
  } else if (values.name.trim().length < 2) {
    errors.name = 'Name must be at least 2 characters';
  }

  if (!values.password?.trim()) {
    errors.password = 'Password is required';
  } else if (values.password.length < 8) {
    errors.password = 'Password must be at least 8 characters';
  }

  return errors;
}

export function extractApiErrors(error) {
  const apiData = error?.response?.data;
  const status = error?.response?.status;

  if (error?.code === 'ECONNABORTED') {
    return {
      message: `Backend API timed out. Make sure Spring Boot is running at ${API_BASE_URL}.`,
      fieldErrors: {}
    };
  }

  if (!error?.response) {
    return {
      message: `Backend API is not reachable. Start the Spring Boot server at ${API_BASE_URL}, then try again.`,
      fieldErrors: {}
    };
  }

  if (status === 404 && !apiData) {
    return {
      message: `API endpoint was not found on ${API_BASE_URL}. Check that the Spring Boot backend is running on the same port as VITE_API_BASE_URL.`,
      fieldErrors: {}
    };
  }

  if (!apiData) {
    return { message: `Request failed with status ${status || 'unknown'}. Please try again.`, fieldErrors: {} };
  }

  const fieldErrors = apiData.fieldErrors || {};
  return {
    message: apiData.message || 'Request failed',
    fieldErrors
  };
}
