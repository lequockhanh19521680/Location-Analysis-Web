# Location Score AI V4.0 - Development Guide

## Development Setup

### Local Development

1. Start PostgreSQL:
```bash
docker run -d \
  --name lsai-postgres \
  -e POSTGRES_USER=lsai_user \
  -e POSTGRES_PASSWORD=lsai_password \
  -e POSTGRES_DB=lsai_db \
  -p 5432:5432 \
  postgres:15-alpine
```

2. Set up Prisma:
```bash
cd database
npm install
npx prisma generate
npx prisma migrate dev --name init
```

3. Run services in development mode:
```bash
# Auth Service
cd auth-service
npm install
npm run dev

# Core Service
cd core-service
npm install
npm run dev

# Frontend
cd frontend
npm install
npm run dev
```

## Code Structure

### Auth Service
```
auth-service/
├── src/
│   ├── controllers/
│   │   └── auth.controller.ts    # Auth logic
│   ├── routes/
│   │   └── auth.routes.ts        # Route definitions
│   ├── utils/
│   │   ├── prisma.ts            # DB client
│   │   └── jwt.ts               # JWT utilities
│   └── index.ts                 # Entry point
├── package.json
├── tsconfig.json
└── Dockerfile
```

### Core Service
```
core-service/
├── src/
│   ├── controllers/
│   │   └── score.controller.ts   # Main scoring logic
│   ├── middleware/
│   │   └── auth.middleware.ts    # JWT verification
│   ├── services/
│   │   ├── pillarA.service.ts   # Competition analysis
│   │   ├── pillarB.service.ts   # Traffic analysis
│   │   ├── pillarC.service.ts   # Socio-economic
│   │   ├── pillarD.service.ts   # Infrastructure
│   │   └── pillarE.service.ts   # Legal risk
│   ├── utils/
│   │   ├── prisma.ts
│   │   ├── auth.ts
│   │   └── scoring.ts           # Normalization logic
│   └── index.ts
├── package.json
├── tsconfig.json
└── Dockerfile
```

### Frontend
```
frontend/
├── app/
│   ├── api/auth/[...nextauth]/
│   │   └── route.ts             # NextAuth config
│   ├── globals.css              # Global styles
│   ├── layout.tsx               # Root layout
│   └── page.tsx                 # Home page
├── components/                   # React components
├── lib/                         # Utilities
├── package.json
├── tsconfig.json
├── next.config.js
├── tailwind.config.js
└── Dockerfile
```

## Testing

### Unit Tests
```bash
# Auth Service
cd auth-service
npm test

# Core Service
cd core-service
npm test
```

### Integration Tests
```bash
# Start all services
docker-compose up -d

# Run integration tests
npm run test:integration
```

## API Testing with cURL

### Register a user
```bash
curl -X POST http://localhost:3001/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "name": "Test User"
  }'
```

### Login
```bash
curl -X POST http://localhost:3001/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123"
  }'
```

### Analyze Location (replace TOKEN with actual JWT)
```bash
curl -X POST http://localhost:3002/core/score \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "latitude": 10.762622,
    "longitude": 106.660172,
    "industry": "F&B",
    "radius": 500
  }'
```

## Database Management

### Create Migration
```bash
cd database
npx prisma migrate dev --name your_migration_name
```

### Reset Database
```bash
npx prisma migrate reset
```

### View Data
```bash
npx prisma studio
```

## Environment Variables

### Required Variables
```env
# Database
DATABASE_URL=postgresql://user:password@localhost:5432/dbname

# JWT
JWT_SECRET=your-secret-key-min-32-chars
NEXTAUTH_SECRET=your-nextauth-secret

# Google Maps
GOOGLE_MAPS_API_KEY=your-api-key

# OAuth (Optional)
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-secret
FACEBOOK_APP_ID=your-app-id
FACEBOOK_APP_SECRET=your-secret

# Payment (Optional)
STRIPE_SECRET_KEY=your-stripe-key
```

## Troubleshooting

### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check connection
psql -h localhost -U lsai_user -d lsai_db
```

### Port Already in Use
```bash
# Find process using port
lsof -i :3000
lsof -i :3001
lsof -i :3002

# Kill process
kill -9 <PID>
```

### Google Maps API Errors
- Ensure API key is valid
- Enable required APIs:
  - Places API
  - Geocoding API
  - Maps JavaScript API
- Check billing is enabled

## Performance Optimization

### Database Indexing
- User lookups: Indexed on `email`
- Analysis results: Indexed on `userId` and `createdAt`

### API Caching
Implement Redis caching for frequently accessed data:
```bash
docker run -d --name redis -p 6379:6379 redis:alpine
```

### Rate Limiting
Configure rate limits in production:
```typescript
import rateLimit from 'express-rate-limit';

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});

app.use(limiter);
```

## Production Deployment

### Build for Production
```bash
# Build all images
docker-compose build

# Push to registry
docker tag lsai-frontend:latest your-registry/lsai-frontend:latest
docker push your-registry/lsai-frontend:latest
```

### Deploy to Cloud
- Use Kubernetes for orchestration
- Set up load balancers
- Configure auto-scaling
- Enable monitoring and logging

## Contributing Guidelines

1. Follow TypeScript best practices
2. Write tests for new features
3. Update documentation
4. Follow commit message conventions
5. Create detailed PR descriptions

## Code Style

```bash
# Run linter
npm run lint

# Format code
npm run format
```

## Resources

- [Next.js Documentation](https://nextjs.org/docs)
- [Prisma Documentation](https://www.prisma.io/docs)
- [Google Maps API](https://developers.google.com/maps)
- [NextAuth.js](https://next-auth.js.org/)
