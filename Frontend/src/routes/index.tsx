import { createBrowserRouter } from 'react-router-dom';
import RootLayout from '../layouts/RootLayout';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    children: [
      {
        index: true,
        element: (
          <div className="flex flex-col items-center justify-center min-h-[50vh] text-center px-4">
            <div className="animate-fade-in">
              <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight bg-gradient-to-r from-violet-650 to-indigo-650 dark:from-violet-400 dark:to-indigo-400 bg-clip-text text-transparent mb-4">
                Welcome to Incubyte
              </h1>
              <p className="text-lg text-slate-600 dark:text-slate-450 max-w-md mx-auto">
                Frontend folder structure initialized successfully with React + TypeScript + Vite + Tailwind CSS.
              </p>
            </div>
          </div>
        ),
      },
    ],
  },
]);
