import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import api from '../lib/axios';

type RegisterFormInputs = {
  email: string;
  password: string;
};

export default function RegisterPage() {
  const { register, handleSubmit, formState: { errors } } = useForm<RegisterFormInputs>();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onSubmit = async (data: RegisterFormInputs) => {
    setLoading(true);
    setErrorMessage(null);
    setSuccessMessage(null);
    try {
      await api.post('/auth/register', data);
      setSuccessMessage('Registration successful');

      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } catch (error: any) {
      const msg = error.response?.data?.message || 'Registration failed';
      setErrorMessage(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center flex-1 bg-white px-4">
      <div className="max-w-md w-full space-y-6 p-8 border border-slate-100 rounded-xl bg-white shadow-sm">
        <div className="text-center">
          <h2 className="text-2xl font-bold tracking-tight text-black">
            Create account
          </h2>
          <p className="mt-2 text-sm text-slate-500">
            Already have an account?{' '}
            <Link to="/login" className="font-semibold text-black hover:underline">
              Sign in
            </Link>
          </p>
        </div>

        {errorMessage && (
          <div className="p-3 bg-red-50 border border-red-100 text-red-700 text-sm rounded-lg text-center">
            {errorMessage}
          </div>
        )}

        {successMessage && (
          <div className="p-3 bg-green-50 border border-green-100 text-green-700 text-sm rounded-lg text-center">
            {successMessage}
          </div>
        )}

        <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-slate-700">
              Email address
            </label>
            <input
              id="email"
              type="email"
              autoComplete="email"
              {...register('email', { required: 'Email is required' })}
              className="mt-1 block w-full px-3 py-2 border border-slate-200 rounded-lg text-black placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-black focus:border-black sm:text-sm bg-white"
              placeholder="you@example.com"
            />
            {errors.email && (
              <p className="mt-1 text-xs text-red-650">{errors.email.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-slate-700">
              Password
            </label>
            <input
              id="password"
              type="password"
              autoComplete="new-password"
              {...register('password', { required: 'Password is required' })}
              className="mt-1 block w-full px-3 py-2 border border-slate-200 rounded-lg text-black placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-black focus:border-black sm:text-sm bg-white"
              placeholder="••••••••"
            />
            {errors.password && (
              <p className="mt-1 text-xs text-red-650">{errors.password.message}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full flex justify-center py-2.5 px-4 border border-transparent text-sm font-semibold rounded-lg text-white bg-black hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-black disabled:opacity-50 transition-colors cursor-pointer"
          >
            {loading ? 'Registering...' : 'Register'}
          </button>
        </form>
      </div>
    </div>
  );
}
