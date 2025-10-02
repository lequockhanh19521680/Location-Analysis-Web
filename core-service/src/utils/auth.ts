import axios from 'axios';

const AUTH_SERVICE_URL = process.env.AUTH_SERVICE_URL || 'http://localhost:3001';

export interface AuthUser {
  id: string;
  email: string;
  name?: string;
}

export const verifyAuth = async (authHeader: string | undefined): Promise<AuthUser> => {
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    throw new Error('No token provided');
  }

  try {
    const response = await axios.get(`${AUTH_SERVICE_URL}/auth/verify`, {
      headers: { Authorization: authHeader },
    });
    return response.data.user;
  } catch (error) {
    throw new Error('Invalid token');
  }
};
