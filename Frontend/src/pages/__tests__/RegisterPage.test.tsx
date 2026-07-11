import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import RegisterPage from '../RegisterPage';
import api from '../../lib/axios';

// Mock api
vi.mock('../../lib/axios');

const mockedApi = api as any;

describe('RegisterPage', () => {
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
  });

  const renderComponent = () => {
    return render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RegisterPage />
        </MemoryRouter>
      </QueryClientProvider>
    );
  };

  it('renders email and password inputs and register button', () => {
    renderComponent();

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /register/i })).toBeInTheDocument();
  });

  it('shows validation errors when fields are empty and submitted', async () => {
    renderComponent();

    const submitButton = screen.getByRole('button', { name: /register/i });
    fireEvent.click(submitButton);

    expect(await screen.findByText(/email is required/i)).toBeInTheDocument();
    expect(await screen.findByText(/password is required/i)).toBeInTheDocument();
  });

  it('calls register API and shows success message on successful submit', async () => {
    mockedApi.post.mockResolvedValueOnce({
      data: {
        id: 1,
        email: 'newuser@example.com',
      },
    });

    renderComponent();

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'newuser@example.com' } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123!' } });

    fireEvent.click(screen.getByRole('button', { name: /register/i }));

    await waitFor(() => {
      expect(mockedApi.post).toHaveBeenCalledWith('/auth/register', {
        email: 'newuser@example.com',
        password: 'Password123!',
      });
    });

    expect(await screen.findByText(/registration successful/i)).toBeInTheDocument();
  });

  it('displays API error message on registration failure', async () => {
    mockedApi.post.mockRejectedValueOnce({
      response: {
        data: {
          message: 'Email already exists',
        },
      },
    });

    renderComponent();

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'duplicate@example.com' } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123!' } });

    fireEvent.click(screen.getByRole('button', { name: /register/i }));

    expect(await screen.findByText(/email already exists/i)).toBeInTheDocument();
  });
});
