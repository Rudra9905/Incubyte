import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import api from '../lib/axios';
import type { Vehicle } from '../types';

export default function DashboardPage() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const userRole = localStorage.getItem('role') || 'USER';
  const userEmail = localStorage.getItem('email') || '';
  const isAdmin = userRole === 'ADMIN';

  // Search filter input states (unsubmitted)
  const [searchQueryInput, setSearchQueryInput] = useState('');
  const [minPriceInput, setMinPriceInput] = useState('');
  const [maxPriceInput, setMaxPriceInput] = useState('');

  // Active filter states (submitted values)
  const [activeSearchQuery, setActiveSearchQuery] = useState('');
  const [activeMinPrice, setActiveMinPrice] = useState('');
  const [activeMaxPrice, setActiveMaxPrice] = useState('');

  // Modal / Form states
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [selectedVehicle, setSelectedVehicle] = useState<Vehicle | null>(null);

  // Form states for Add/Edit
  const [make, setMake] = useState('');
  const [model, setModel] = useState('');
  const [category, setCategory] = useState('');
  const [price, setPrice] = useState('');
  const [quantity, setQuantity] = useState('');

  // Fetch Vehicles
  const { data: vehicles = [], isLoading, error } = useQuery<Vehicle[]>({
    queryKey: ['vehicles', activeSearchQuery, activeMinPrice, activeMaxPrice],
    queryFn: async () => {
      // Determine endpoint based on whether filters are present
      if (activeSearchQuery || activeMinPrice || activeMaxPrice) {
        const params: Record<string, any> = {};
        if (activeSearchQuery) {
          params.make = activeSearchQuery;
          params.model = activeSearchQuery;
          params.category = activeSearchQuery;
        }
        if (activeMinPrice) params.minPrice = activeMinPrice;
        if (activeMaxPrice) params.maxPrice = activeMaxPrice;
        
        const res = await api.get('/vehicles/search', { params });
        return res.data;
      }
      
      const res = await api.get('/vehicles');
      return res.data;
    }
  });

  // Purchase Mutation
  const purchaseMutation = useMutation({
    mutationFn: async (id: number) => {
      await api.post(`/vehicles/${id}/purchase`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vehicles'] });
    }
  });

  // Add Vehicle Mutation
  const addMutation = useMutation({
    mutationFn: async (newVehicle: Omit<Vehicle, 'id'>) => {
      await api.post('/vehicles', newVehicle);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vehicles'] });
      setIsAddOpen(false);
      resetForm();
    }
  });

  // Edit Vehicle Mutation
  const editMutation = useMutation({
    mutationFn: async ({ id, updated }: { id: number; updated: Omit<Vehicle, 'id'> }) => {
      await api.put(`/vehicles/${id}`, updated);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vehicles'] });
      setIsEditOpen(false);
      setSelectedVehicle(null);
      resetForm();
    }
  });

  // Delete Vehicle Mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/vehicles/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vehicles'] });
    }
  });

  const resetForm = () => {
    setMake('');
    setModel('');
    setCategory('');
    setPrice('');
    setQuantity('');
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const openAddModal = () => {
    resetForm();
    setIsAddOpen(true);
  };

  const openEditModal = (vehicle: Vehicle) => {
    setSelectedVehicle(vehicle);
    setMake(vehicle.make);
    setModel(vehicle.model);
    setCategory(vehicle.category);
    setPrice(vehicle.price.toString());
    setQuantity(vehicle.quantity.toString());
    setIsEditOpen(true);
  };

  const handleAddSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    addMutation.mutate({
      make,
      model,
      category,
      price: parseFloat(price),
      quantity: parseInt(quantity)
    });
  };

  const handleEditSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedVehicle) {
      editMutation.mutate({
        id: selectedVehicle.id,
        updated: {
          make,
          model,
          category,
          price: parseFloat(price),
          quantity: parseInt(quantity)
        }
      });
    }
  };

  return (
    <div className="min-h-screen bg-white text-black font-sans">
      {/* Top Header */}
      <header className="border-b border-black py-4 px-6 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold tracking-tight">Car Dealership</h1>
          {userEmail && <p className="text-xs text-slate-500 mt-0.5">{userEmail} ({userRole})</p>}
        </div>
        <button
          onClick={handleLogout}
          className="border border-black text-black px-4 py-1.5 hover:bg-black hover:text-white transition text-sm font-medium"
        >
          Sign Out
        </button>
      </header>

      {/* Main Body */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {/* Search, Filter, and Admin Add Section */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
          <div className="flex-1 flex flex-col sm:flex-row items-center gap-3">
            <input
              type="text"
              placeholder="Search by make, model, or category"
              value={searchQueryInput}
              onChange={(e) => setSearchQueryInput(e.target.value)}
              className="w-full sm:max-w-md border border-black px-4 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-black"
            />
            <div className="flex items-center gap-2 w-full sm:w-auto">
              <input
                type="number"
                placeholder="Min Price"
                value={minPriceInput}
                onChange={(e) => setMinPriceInput(e.target.value)}
                className="w-full sm:w-28 border border-black px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-black"
              />
              <span className="text-sm">-</span>
              <input
                type="number"
                placeholder="Max Price"
                value={maxPriceInput}
                onChange={(e) => setMaxPriceInput(e.target.value)}
                className="w-full sm:w-28 border border-black px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-black"
              />
            </div>
            <button
              onClick={() => {
                setActiveSearchQuery(searchQueryInput);
                setActiveMinPrice(minPriceInput);
                setActiveMaxPrice(maxPriceInput);
              }}
              className="w-full sm:w-auto border border-black bg-black text-white px-5 py-2 text-sm font-semibold hover:bg-white hover:text-black transition cursor-pointer"
            >
              Search
            </button>
          </div>

          {isAdmin && (
            <button
              onClick={openAddModal}
              className="border border-black bg-black text-white px-5 py-2 text-sm font-semibold hover:bg-white hover:text-black transition"
            >
              Add Vehicle
            </button>
          )}
        </div>

        {/* Vehicles Grid */}
        {isLoading ? (
          <div className="text-center py-12">
            <p className="text-sm text-slate-500">Loading vehicles...</p>
          </div>
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-sm text-red-500">Error loading catalog</p>
          </div>
        ) : vehicles.length === 0 ? (
          <div className="text-center py-12 border border-dashed border-slate-300">
            <p className="text-sm text-slate-500">No vehicles available match the filters.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {vehicles.map((vehicle) => {
              const outOfStock = vehicle.quantity <= 0;
              return (
                <div
                  key={vehicle.id}
                  className="border border-black p-6 flex flex-col justify-between hover:shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-shadow duration-200"
                >
                  <div>
                    <div className="flex items-start justify-between">
                      <div>
                        <h2 className="text-lg font-bold">
                          {vehicle.make} {vehicle.model}
                        </h2>
                        <span className="inline-block bg-slate-100 text-slate-700 text-xs px-2 py-0.5 mt-1 font-mono uppercase">
                          {vehicle.category}
                        </span>
                      </div>
                      <p className="text-lg font-mono font-bold">${vehicle.price.toLocaleString()}</p>
                    </div>

                    <p className="text-sm text-slate-650 mt-4 font-medium">
                      {outOfStock ? (
                        <span className="text-red-650 font-semibold">Out of Stock</span>
                      ) : (
                        <span>{vehicle.quantity} available in stock</span>
                      )}
                    </p>
                  </div>

                  <div className="mt-6 space-y-2">
                    <button
                      onClick={() => purchaseMutation.mutate(vehicle.id)}
                      disabled={outOfStock}
                      className={`w-full py-2 text-sm font-semibold border border-black transition ${
                        outOfStock
                          ? 'border-slate-200 bg-slate-50 text-slate-400 cursor-not-allowed'
                          : 'bg-white text-black hover:bg-black hover:text-white'
                      }`}
                    >
                      {outOfStock ? 'Out of Stock' : 'Purchase'}
                    </button>

                    {isAdmin && (
                      <div className="grid grid-cols-2 gap-2">
                        <button
                          onClick={() => openEditModal(vehicle)}
                          className="border border-black py-1.5 text-xs font-semibold hover:bg-slate-50 transition"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => {
                            if (window.confirm('Delete this vehicle?')) {
                              deleteMutation.mutate(vehicle.id);
                            }
                          }}
                          className="border border-black py-1.5 text-xs font-semibold hover:bg-red-50 hover:text-red-600 transition"
                        >
                          Delete
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </main>

      {/* Add Modal */}
      {isAddOpen && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white border border-black max-w-md w-full p-6 space-y-4">
            <div className="flex justify-between items-center border-b border-slate-100 pb-3">
              <h3 className="font-bold text-lg">Add New Vehicle</h3>
              <button onClick={() => setIsAddOpen(false)} className="text-slate-400 hover:text-black">✕</button>
            </div>
            <form onSubmit={handleAddSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold mb-1">Make</label>
                <input
                  required
                  type="text"
                  value={make}
                  onChange={(e) => setMake(e.target.value)}
                  className="w-full border border-black p-2 text-sm focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold mb-1">Model</label>
                <input
                  required
                  type="text"
                  value={model}
                  onChange={(e) => setModel(e.target.value)}
                  className="w-full border border-black p-2 text-sm focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold mb-1">Category</label>
                <input
                  required
                  type="text"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  className="w-full border border-black p-2 text-sm focus:outline-none"
                />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold mb-1">Price ($)</label>
                  <input
                    required
                    type="number"
                    value={price}
                    onChange={(e) => setPrice(e.target.value)}
                    className="w-full border border-black p-2 text-sm focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold mb-1">Quantity</label>
                  <input
                    required
                    type="number"
                    value={quantity}
                    onChange={(e) => setQuantity(e.target.value)}
                    className="w-full border border-black p-2 text-sm focus:outline-none"
                  />
                </div>
              </div>
              <div className="pt-4 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsAddOpen(false)}
                  className="border border-black px-4 py-2 text-sm hover:bg-slate-55"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="bg-black text-white border border-black px-4 py-2 text-sm hover:bg-white hover:text-black transition"
                >
                  Save Vehicle
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {isEditOpen && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white border border-black max-w-md w-full p-6 space-y-4">
            <div className="flex justify-between items-center border-b border-slate-100 pb-3">
              <h3 className="font-bold text-lg">Edit Vehicle</h3>
              <button onClick={() => setIsEditOpen(false)} className="text-slate-400 hover:text-black">✕</button>
            </div>
            <form onSubmit={handleEditSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold mb-1">Make</label>
                <input
                  required
                  type="text"
                  value={make}
                  onChange={(e) => setMake(e.target.value)}
                  className="w-full border border-black p-2 text-sm focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold mb-1">Model</label>
                <input
                  required
                  type="text"
                  value={model}
                  onChange={(e) => setModel(e.target.value)}
                  className="w-full border border-black p-2 text-sm focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold mb-1">Category</label>
                <input
                  required
                  type="text"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  className="w-full border border-black p-2 text-sm focus:outline-none"
                />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold mb-1">Price ($)</label>
                  <input
                    required
                    type="number"
                    value={price}
                    onChange={(e) => setPrice(e.target.value)}
                    className="w-full border border-black p-2 text-sm focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold mb-1">Quantity</label>
                  <input
                    required
                    type="number"
                    value={quantity}
                    onChange={(e) => setQuantity(e.target.value)}
                    className="w-full border border-black p-2 text-sm focus:outline-none"
                  />
                </div>
              </div>
              <div className="pt-4 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsEditOpen(false)}
                  className="border border-black px-4 py-2 text-sm hover:bg-slate-55"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="bg-black text-white border border-black px-4 py-2 text-sm hover:bg-white hover:text-black transition"
                >
                  Update Vehicle
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
