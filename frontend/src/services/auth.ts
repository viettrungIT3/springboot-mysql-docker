import { LoginRequest, LoginResponse } from '@/types/api';
import apiClient from './api';

export const authService = {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post('/auth/login', credentials);
    return response.data;
  },

  async validateToken(token: string): Promise<boolean> {
    try {
      const response = await apiClient.post('/api/v1/auth/validate', null, {
        params: { token }
      });
      return response.data;
    } catch {
      return false;
    }
  },

  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_info');
  },

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  },

  getUserInfo() {
    const userInfo = localStorage.getItem('user_info');
    return userInfo ? JSON.parse(userInfo) : null;
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
};
