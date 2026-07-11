import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import DashboardPage from '../DashboardPage';
import api from '../../lib/axios';

// Mock api
vi.mock('../../lib/axios');

const mockedApi = api as any;

describe('DashboardPage', () => {
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
          <DashboardPage />
        </MemoryRouter>
      </QueryClientProvider>
    );
  };

  it('renders search input and list of vehicles from API', async () => {
    mockedApi.get.mockResolvedValueOnce({
      data: [
        { id: 1, make: 'Toyota', model: 'Corolla', category: 'Sedan', price: 22000, quantity: 5 },
        { id: 2, make: 'Ford', model: 'Mustang', category: 'Coupe', price: 36000, quantity: 2 },
      ],
    });

    renderComponent();

    expect(screen.getByPlaceholderText(/search by make, model, or category/i)).toBeInTheDocument();

    expect(await screen.findByText('Toyota Corolla')).toBeInTheDocument();
    expect(await screen.findByText('Ford Mustang')).toBeInTheDocument();
  });

  it('disables purchase button when vehicle quantity is zero', async () => {
    mockedApi.get.mockResolvedValueOnce({
      data: [
        { id: 1, make: 'Toyota', model: 'Corolla', category: 'Sedan', price: 22000, quantity: 5 },
        { id: 2, make: 'Honda', model: 'Civic', category: 'Sedan', price: 25000, quantity: 0 },
      ],
    });

    renderComponent();

    // Corolla has qty 5, purchase button should be enabled
    const corollaText = await screen.findByText('Toyota Corolla');
    const corollaCard = corollaText.closest('.border') as HTMLElement;
    const corollaPurchaseBtn = within(corollaCard).getByRole('button', { name: /purchase/i });
    expect(corollaPurchaseBtn).not.toBeDisabled();

    // Civic has qty 0, purchase button should be disabled
    const civicText = await screen.findByText('Honda Civic');
    const civicCard = civicText.closest('.border') as HTMLElement;
    const civicPurchaseBtn = within(civicCard).getByRole('button', { name: /out of stock/i });
    expect(civicPurchaseBtn).toBeDisabled();
  });

  it('shows admin actions (Add, Edit, Delete) ONLY if logged-in user is ADMIN', async () => {
    // Scenario 1: User is USER
    localStorage.setItem('role', 'USER');
    mockedApi.get.mockResolvedValueOnce({
      data: [{ id: 1, make: 'Toyota', model: 'Corolla', category: 'Sedan', price: 22000, quantity: 5 }],
    });

    renderComponent();

    await screen.findByText('Toyota Corolla');
    expect(screen.queryByRole('button', { name: /add vehicle/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /delete/i })).not.toBeInTheDocument();

    // Scenario 2: User is ADMIN
    vi.clearAllMocks();
    localStorage.setItem('role', 'ADMIN');
    mockedApi.get.mockResolvedValueOnce({
      data: [{ id: 1, make: 'Toyota', model: 'Corolla', category: 'Sedan', price: 22000, quantity: 5 }],
    });

    // Clean client cache to refetch
    queryClient.clear();
    renderComponent();

    expect(await screen.findByRole('button', { name: /add vehicle/i })).toBeInTheDocument();
    expect(await screen.findByRole('button', { name: /edit/i })).toBeInTheDocument();
    expect(await screen.findByRole('button', { name: /delete/i })).toBeInTheDocument();
  });

  it('triggers purchase API when purchase button is clicked', async () => {
    mockedApi.get.mockResolvedValueOnce({
      data: [{ id: 1, make: 'Toyota', model: 'Corolla', category: 'Sedan', price: 22000, quantity: 5 }],
    });
    mockedApi.post.mockResolvedValueOnce({
      data: { id: 1, make: 'Toyota', model: 'Corolla', category: 'Sedan', price: 22000, quantity: 4 },
    });

    renderComponent();

    await screen.findByText('Toyota Corolla');
    const purchaseBtn = screen.getByRole('button', { name: /purchase/i });
    fireEvent.click(purchaseBtn);

    await waitFor(() => {
      expect(mockedApi.post).toHaveBeenCalledWith('/vehicles/1/purchase');
    });
  });

  it('triggers search API only when Search button is clicked', async () => {
    mockedApi.get.mockResolvedValueOnce({
      data: [{ id: 1, make: 'Honda', model: 'Civic', category: 'Sedan', price: 25000, quantity: 5 }],
    });

    renderComponent();

    // 1. Find and type into search inputs
    const searchInput = screen.getByPlaceholderText(/search by make, model, or category/i);
    fireEvent.change(searchInput, { target: { value: 'Honda' } });

    const minPriceInput = screen.getByPlaceholderText(/min price/i);
    fireEvent.change(minPriceInput, { target: { value: '20000' } });

    const maxPriceInput = screen.getByPlaceholderText(/max price/i);
    fireEvent.change(maxPriceInput, { target: { value: '30000' } });

    // The API should NOT be called with search query immediately on typing (should wait for button click)
    expect(mockedApi.get).not.toHaveBeenCalledWith('/vehicles/search', expect.any(Object));

    // 2. Click Search button
    const searchButton = screen.getByRole('button', { name: /search/i });
    fireEvent.click(searchButton);

    // 3. Verify the search API is called with the correct parameters
    await waitFor(() => {
      expect(mockedApi.get).toHaveBeenCalledWith('/vehicles/search', {
        params: {
          make: 'Honda',
          model: 'Honda',
          category: 'Honda',
          minPrice: '20000',
          maxPrice: '30000'
        }
      });
    });
  });
});
