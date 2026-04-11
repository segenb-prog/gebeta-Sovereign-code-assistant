/**
 * Tests for AuthContext.
 * Follows Gebeta Sovereign Coding Rules for security and quality.
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AuthProvider, useAuth } from '../context/AuthContext';
import api from '../services/api';

// Mock API
vi.mock('../services/api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    interceptors: {
      request: { use: vi.fn(), eject: vi.fn() },
      response: { use: vi.fn(), eject: vi.fn() },
    },
  },
}));

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: vi.fn((key: string) => store[key] || null),
    setItem: vi.fn((key: string, value: string) => { store[key] = value; }),
    removeItem: vi.fn((key: string) => { delete store[key]; }),
    clear: vi.fn(() => { store = {}; }),
  };
})();

Object.defineProperty(window, 'localStorage', { value: localStorageMock });

// Test component that uses auth context
const TestComponent = () => {
  const { isAuthenticated, user, login, logout } = useAuth();
  return (
    <div>
      <div data-testid="auth-status">{isAuthenticated ? 'authenticated' : 'unauthenticated'}</div>
      <div data-testid="user-email">{user?.email || 'no-user'}</div>
      <div data-testid="error-message">{error || 'no-error'}</div>
      <button onClick={() => login('test@example.com', 'password')}>Login</button>
      <button onClick={() => login('error@example.com', 'wrong')}>LoginWithError</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    localStorageMock.clear();
    vi.clearAllMocks();
  });

  // ========== Initial State Tests ==========

  it('provides unauthenticated state by default', () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    expect(screen.getByTestId('auth-status')).toHaveTextContent('unauthenticated');
    expect(screen.getByTestId('user-email')).toHaveTextContent('no-user');
  });

  it('reads token from localStorage on init', () => {
    localStorageMock.setItem('access_token', 'existing-token');
    
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    expect(screen.getByTestId('auth-status')).toHaveTextContent('authenticated');
  });

  // ========== Successful Login Tests ==========

  it('updates state after successful login', async () => {
    const mockToken = 'new-token';
    const mockUser = { id: 1, email: 'test@example.com', full_name: 'Test User' };
    
    const mockPost = vi.mocked(api.post);
    const mockGet = vi.mocked(api.get);
    mockPost.mockResolvedValueOnce({ data: { access_token: mockToken } });
    mockGet.mockResolvedValueOnce({ data: mockUser });
    
    const user = userEvent.setup();
    
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    await user.click(screen.getByRole('button', { name: /login/i }));
    
    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('authenticated');
      expect(screen.getByTestId('user-email')).toHaveTextContent('test@example.com');
    });
    
    expect(localStorageMock.setItem).toHaveBeenCalledWith('access_token', mockToken);
  });

  // ========== Failed Login Tests ==========

  it('handles failed login with invalid credentials', async () => {
    const mockPost = vi.mocked(api.post);
    mockPost.mockRejectedValueOnce({ response: { status: 401, data: { detail: 'Invalid credentials' } } });
    
    const user = userEvent.setup();
    
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    await user.click(screen.getByRole('button', { name: /loginwitherror/i }));
    
    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('unauthenticated');
    });
    
    expect(localStorageMock.setItem).not.toHaveBeenCalled();
  });

  it('handles network error during login', async () => {
    const mockPost = vi.mocked(api.post);
    mockPost.mockRejectedValueOnce({ message: 'Network Error' });
    
    const user = userEvent.setup();
    
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    await user.click(screen.getByRole('button', { name: /loginwitherror/i }));
    
    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('unauthenticated');
    });
    
    expect(localStorageMock.setItem).not.toHaveBeenCalled();
  });

  it('handles server error (500) during login', async () => {
    const mockPost = vi.mocked(api.post);
    mockPost.mockRejectedValueOnce({ response: { status: 500, data: { detail: 'Internal server error' } } });
    
    const user = userEvent.setup();
    
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    await user.click(screen.getByRole('button', { name: /loginwitherror/i }));
    
    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('unauthenticated');
    });
  });

  // ========== Request Interceptor Tests ==========

  it('attaches token to API requests when authenticated', async () => {
    localStorageMock.setItem('access_token', 'existing-token');
    
    // Re-import to trigger interceptor setup
    const { default: freshApi } = await import('../services/api');
    const requestInterceptor = (freshApi.interceptors.request.use as jest.Mock).mock.calls[0][0];
    
    const config = requestInterceptor({ headers: {} });
    expect(config.headers.Authorization).toBe('Bearer existing-token');
  });

  it('does not attach token to API requests when unauthenticated', async () => {
    localStorageMock.removeItem('access_token');
    
    const { default: freshApi } = await import('../services/api');
    const requestInterceptor = (freshApi.interceptors.request.use as jest.Mock).mock.calls[0][0];
    
    const config = requestInterceptor({ headers: {} });
    expect(config.headers.Authorization).toBeUndefined();
  });

  // ========== Logout Tests ==========

  it('clears state after logout', async () => {
    localStorageMock.setItem('access_token', 'existing-token');
    
    const user = userEvent.setup();
    
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    expect(screen.getByTestId('auth-status')).toHaveTextContent('authenticated');
    
    await user.click(screen.getByRole('button', { name: /logout/i }));
    
    expect(screen.getByTestId('auth-status')).toHaveTextContent('unauthenticated');
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('access_token');
  });
});
