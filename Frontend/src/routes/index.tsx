import { createBrowserRouter, Navigate } from 'react-router-dom';
import RootLayout from '../layouts/RootLayout';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';

const HomeRoute = () => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  const email = localStorage.getItem('email');

  return (
    <div className="flex flex-col items-center justify-center min-h-[50vh] text-center px-4 bg-white">
      <div className="space-y-4">
        <h1 className="text-3xl font-bold tracking-tight text-black">
          Welcome to Incubyte
        </h1>
        <p className="text-slate-500 max-w-md mx-auto">
          You are logged in as <span className="font-semibold text-black">{email}</span>.
        </p>
      </div>
    </div>
  );
};

export const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    children: [
      {
        index: true,
        element: <HomeRoute />,
      },
      {
        path: 'login',
        element: <LoginPage />,
      },
      {
        path: 'register',
        element: <RegisterPage />,
      },
    ],
  },
]);
