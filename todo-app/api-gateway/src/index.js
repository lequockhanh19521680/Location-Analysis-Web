import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import rateLimit from 'express-rate-limit';
import { createProxyMiddleware } from 'http-proxy-middleware';
import dotenv from 'dotenv';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 8080;

// Security middleware
app.use(helmet());

// CORS configuration
app.use(cors({
  origin: process.env.CORS_ORIGIN || '*',
  credentials: true
}));

// Body parser
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Rate limiting - General
const generalLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: 'Too many requests from this IP, please try again later.',
  standardHeaders: true,
  legacyHeaders: false,
});

// Rate limiting - Auth endpoints (stricter)
const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 20, // limit each IP to 20 requests per windowMs
  message: 'Too many authentication attempts, please try again later.',
  standardHeaders: true,
  legacyHeaders: false,
});

// Rate limiting - Todo endpoints
const todoLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 200, // limit each IP to 200 requests per windowMs
  message: 'Too many todo requests, please try again later.',
  standardHeaders: true,
  legacyHeaders: false,
});

// Apply general rate limiter to all routes
app.use(generalLimiter);

// Health check
app.get('/health', (req, res) => {
  res.json({ 
    status: 'ok', 
    service: 'api-gateway',
    timestamp: new Date().toISOString()
  });
});

// Service URLs
const AUTH_SERVICE_URL = process.env.AUTH_SERVICE_URL || 'http://localhost:8081';
const TODO_SERVICE_URL = process.env.TODO_SERVICE_URL || 'http://localhost:8082';

// Proxy configuration for Auth Service
app.use('/api/auth', authLimiter, createProxyMiddleware({
  target: AUTH_SERVICE_URL,
  changeOrigin: true,
  pathRewrite: {
    '^/api/auth': '/api/auth'
  },
  onProxyReq: (proxyReq, req, res) => {
    console.log(`[AUTH] ${req.method} ${req.path} -> ${AUTH_SERVICE_URL}${req.path}`);
  },
  onError: (err, req, res) => {
    console.error('Auth Service Proxy Error:', err);
    res.status(503).json({ 
      error: 'Auth service unavailable',
      message: 'Please try again later'
    });
  }
}));

// Proxy configuration for Todo Service
app.use('/api/todos', todoLimiter, createProxyMiddleware({
  target: TODO_SERVICE_URL,
  changeOrigin: true,
  pathRewrite: {
    '^/api/todos': '/api/todos'
  },
  onProxyReq: (proxyReq, req, res) => {
    console.log(`[TODO] ${req.method} ${req.path} -> ${TODO_SERVICE_URL}${req.path}`);
  },
  onError: (err, req, res) => {
    console.error('Todo Service Proxy Error:', err);
    res.status(503).json({ 
      error: 'Todo service unavailable',
      message: 'Please try again later'
    });
  }
}));

// 404 handler
app.use((req, res) => {
  res.status(404).json({ 
    error: 'Not Found',
    message: 'The requested resource was not found'
  });
});

// Error handler
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(err.status || 500).json({
    error: 'Internal Server Error',
    message: process.env.NODE_ENV === 'production' 
      ? 'An error occurred' 
      : err.message
  });
});

app.listen(PORT, () => {
  console.log(`🚀 API Gateway running on port ${PORT}`);
  console.log(`📡 Auth Service: ${AUTH_SERVICE_URL}`);
  console.log(`📡 Todo Service: ${TODO_SERVICE_URL}`);
  console.log(`🔒 Security features: Rate limiting, CORS, Helmet`);
});

export default app;
