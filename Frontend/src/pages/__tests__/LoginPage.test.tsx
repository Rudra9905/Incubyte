import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import LoginPage from '../LoginPage';
import axios from 'axios';

// Mock axios
vi.mock('axios');

const mockedAxios = axios as any;

describe('LoginPage', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
        },
        mutations: {
          retry: false,
        }
      },
    });
    vi.clearAllMocks();
    localStorage.clear();
  });

  const renderComponent = () => {
    return render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <LoginPage />
        </MemoryRouter>
      </QueryClientProvider>
    );
  };

  it('renders email and password inputs and login button', () => {
    renderComponent();

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
  });

  it('shows validation errors when fields are empty and submitted', async () => {
    renderComponent();

    const submitButton = screen.getByRole('button', { name: /login/i });
    fireEvent.click(submitButton);

    expect(await screen.findByText(/email is required/i)).toBeInTheDocument();
    expect(await screen.findByText(/password is required/i)).toBeInTheDocument();
  });

  it('calls login API, stores token, and shows success message on successful submit', async () => {
    mockedAxios.post.mockResolvedValueOnce({
      data: {
        token: 'fake-jwt-token',
        email: 'user@example.com',
      },
    });

    renderComponent();

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@example.com' } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123!' } });

    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(mockedAxios.post).toHaveBeenCalledWith('/api/auth/login', {
        email: 'user@example.com',
        password: 'Password123!',
      });
    });

    await waitFor(() => {
      expect(localStorage.getItem('token')).toBe('fake-jwt-token');
      expect(localStorage.getItem('email')).toBe('user@example.com');
    });

    expect(await screen.findByText(/login successful/i)).toBeInTheDocument();
  });

  it('displays API error message on login failure', async () => {
    mockedAxios.post.mockRejectedValueOnce({
      response: {
        data: {
          message: 'Invalid email or password',
        },
      },
    });

    renderComponent();

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@example.com' } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'wrongpassword' } });

    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    expect(await screen.findByText(/invalid email or password/i)).toBeInTheDocument();
  });
});
