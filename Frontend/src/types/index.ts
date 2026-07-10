export interface User {
  id: string;
  email: string;
  name?: string;
  role?: string;
}

export interface ApiError {
  message: string;
  status?: number;
  errors?: Record<string, string[]>;
}
