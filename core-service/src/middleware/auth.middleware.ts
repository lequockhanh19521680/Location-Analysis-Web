import { Request, Response, NextFunction } from 'express';
import { verifyAuth, AuthUser } from '../utils/auth';

export interface AuthRequest extends Request {
  user?: AuthUser;
}

export const authMiddleware = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const user = await verifyAuth(req.headers.authorization);
    req.user = user;
    next();
  } catch (error) {
    res.status(401).json({ error: 'Unauthorized' });
  }
};
