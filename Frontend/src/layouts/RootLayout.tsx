import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';

export default function RootLayout() {
  const [email, setEmail] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const checkAuth = () => {
      setEmail(localStorage.getItem('email'));
    };
    checkAuth();
    window.addEventListener('storage', checkAuth);
    const interval = setInterval(checkAuth, 1000);

    return () => {
      window.removeEventListener('storage', checkAuth);
      clearInterval(interval);
    };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    setEmail(null);
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-white text-black flex flex-col font-sans antialiased">
      <header className="border-b border-slate-100 bg-white sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Link to="/" className="text-lg font-bold tracking-tight text-black">
              Incubyte App
            </Link>
          </div>
          <nav className="flex items-center gap-6 text-sm font-medium">
            <Link to="/" className="text-slate-600 hover:text-black transition-colors">
              Home
            </Link>
            {email ? (
              <>
                <span className="text-slate-500 font-normal">{email}</span>
                <button
                  onClick={handleLogout}
                  className="text-black hover:underline cursor-pointer"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-slate-600 hover:text-black transition-colors">
                  Login
                </Link>
                <Link to="/register" className="text-slate-600 hover:text-black transition-colors">
                  Register
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>
      <main className="flex-1 flex flex-col max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-12 bg-white">
        <Outlet />
      </main>
      <footer className="border-t border-slate-100 bg-white py-6 text-center text-xs text-slate-400">
        <p>&copy; {new Date().getFullYear()} Incubyte. All rights reserved.</p>
      </footer>
    </div>
  );
}
