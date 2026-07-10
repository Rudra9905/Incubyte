import { Outlet, Link } from 'react-router-dom';

export default function RootLayout() {
  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 text-slate-800 dark:text-slate-100 flex flex-col font-sans">
      <header className="border-b border-slate-200 dark:border-slate-850 bg-white/80 dark:bg-slate-950/80 backdrop-blur-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-xl font-extrabold bg-gradient-to-r from-violet-600 to-indigo-600 dark:from-violet-400 dark:to-indigo-400 bg-clip-text text-transparent">
              Incubyte App
            </span>
          </div>
          <nav className="flex items-center gap-6 text-sm font-medium">
            <Link to="/" className="hover:text-violet-600 dark:hover:text-violet-400 transition-colors">
              Home
            </Link>
          </nav>
        </div>
      </header>
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
      <footer className="border-t border-slate-200 dark:border-slate-800 py-6 text-center text-sm text-slate-500">
        <p>&copy; {new Date().getFullYear()} Incubyte. All rights reserved.</p>
      </footer>
    </div>
  );
}
