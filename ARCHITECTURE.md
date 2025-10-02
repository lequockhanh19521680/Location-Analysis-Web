# System Architecture - Location Score AI V4.0

## Overview

Location Score AI V4.0 follows a microservices architecture pattern with clear separation of concerns, independent scalability, and resilient design.

```
┌─────────────────────────────────────────────────────────────────┐
│                        Internet/Users                            │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Load Balancer / CDN                          │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Frontend (Next.js)                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  UI Components │ NextAuth │ State Management │ Routing   │  │
│  └──────────────────────────────────────────────────────────┘  │
│                         Port 3000                                │
└───────┬─────────────────────────────────────────────────────────┘
        │
        ├────────────────┬────────────────┐
        ▼                ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Auth Service │  │ Core Service │  │  PostgreSQL  │
│  Port 3001   │  │  Port 3002   │  │  Port 5432   │
│              │  │              │  │              │
│ ┌──────────┐ │  │ ┌──────────┐ │  │ ┌──────────┐ │
│ │   JWT    │ │  │ │  Scoring │ │  │ │   Users  │ │
│ │  OAuth   │ │  │ │  Engine  │ │  │ │ Analysis │ │
│ └──────────┘ │  │ └──────────┘ │  │ │  Results │ │
│              │  │       │      │  │ └──────────┘ │
└──────────────┘  └───────┼──────┘  └──────────────┘
                          │
                          ▼
                  ┌──────────────┐
                  │ Google Maps  │
                  │     APIs     │
                  │              │
                  │  - Places    │
                  │  - Geocoding │
                  │  - Directions│
                  └──────────────┘
```

## Service Architecture

### 1. Frontend Service (Next.js)

**Technology Stack:**
- Next.js 14 (App Router)
- TypeScript
- Tailwind CSS
- NextAuth.js
- React Chart.js

**Key Features:**
- Server-side rendering (SSR)
- Client-side hydration
- Automatic code splitting
- Image optimization
- API routes for authentication

**Pages:**
```
/                 - Landing page
/login            - Authentication
/register         - User registration
/dashboard        - User dashboard
/analyze          - Location analysis form
/about            - About page
```

**API Routes:**
```
/api/auth/[...nextauth]  - NextAuth endpoints
```

### 2. Auth Service (Node.js/Express)

**Technology Stack:**
- Node.js 18+
- Express.js
- TypeScript
- Prisma ORM
- JWT
- bcryptjs

**Endpoints:**
```
POST   /auth/register     - User registration
POST   /auth/login        - User login
POST   /auth/oauth        - OAuth authentication
GET    /auth/verify       - Token verification
GET    /health            - Health check
```

**Security Features:**
- Password hashing (bcrypt, 10 rounds)
- JWT token generation
- OAuth 2.0 integration (Google, Facebook)
- Token expiration (24h default)
- CORS protection
- Helmet security headers

### 3. Core Data Service (Node.js/Express)

**Technology Stack:**
- Node.js 18+
- Express.js
- TypeScript
- Prisma ORM
- Google Maps APIs
- Stripe/VNPay (optional)

**Endpoints:**
```
POST   /core/score        - Analyze location
GET    /core/history      - Get analysis history
GET    /core/analysis/:id - Get specific analysis
GET    /health            - Health check
```

**Scoring Engine:**
```
src/
├── services/
│   ├── pillarA.service.ts  - Competition & Saturation
│   ├── pillarB.service.ts  - Traffic & Accessibility
│   ├── pillarC.service.ts  - Socio-Economic
│   ├── pillarD.service.ts  - Infrastructure
│   └── pillarE.service.ts  - Macro & Legal Risk
├── utils/
│   └── scoring.ts          - Normalization & weighting
└── controllers/
    └── score.controller.ts - Main scoring logic
```

### 4. Database (PostgreSQL + Prisma)

**Schema:**

```prisma
model User {
  id          String   @id @default(uuid())
  email       String   @unique
  password    String?
  name        String?
  provider    String?
  providerId  String?
  createdAt   DateTime @default(now())
  updatedAt   DateTime @updatedAt
  
  analysisResults AnalysisResult[]
}

model AnalysisResult {
  id              String   @id @default(uuid())
  userId          String
  address         String
  latitude        Float
  longitude       Float
  industry        String
  radius          Int
  
  // Weights (0-1, sum=1)
  weightA-E       Float
  
  // Pillar Scores (0-10)
  pillarA-E_score Float
  
  // Sub-scores (0-10)
  subScoreA1-A4   Float  // 4 sub-scores
  subScoreB1-B4   Float  // 4 sub-scores
  subScoreC1-C3   Float  // 3 sub-scores
  subScoreD1-D3   Float  // 3 sub-scores
  subScoreE1-E3   Float  // 3 sub-scores
  
  totalScore      Float
  hasRiskAlert    Boolean
  rawData         Json?
  isPremium       Boolean
  
  createdAt       DateTime @default(now())
  updatedAt       DateTime @updatedAt
  
  user            User     @relation(fields: [userId], references: [id])
}
```

## Data Flow

### Location Analysis Flow

```
1. User Input (Frontend)
   ├─ Address/Coordinates
   ├─ Industry Selection
   ├─ Radius Selection
   └─ Weight Configuration

2. Authentication (Auth Service)
   ├─ Verify JWT Token
   └─ Get User ID

3. Core Processing (Core Service)
   ├─ Validate Input
   ├─ Execute 5 Pillar Analysis (Parallel)
   │   ├─ Pillar A: Google Places API
   │   ├─ Pillar B: Google Places API
   │   ├─ Pillar C: Simulated Data
   │   ├─ Pillar D: Google Places API
   │   └─ Pillar E: Simulated Data
   ├─ Normalize Scores (0-10)
   ├─ Calculate Weighted Total
   ├─ Apply NFR7 Risk Logic
   └─ Save to Database

4. Response (Frontend)
   ├─ Display Total Score
   ├─ Show Pillar Breakdown
   ├─ Display Sub-scores
   ├─ Show Risk Alerts
   └─ Render Visualizations
```

## Scoring Algorithm

### Pillar A: Competition & Saturation

```typescript
// A.1: Competitor Density
competitors = getNearbyCompetitors(location, radius, industry)
a1 = normalize(competitors.count, 0, 20, inverse=true)

// A.2: Competitor Quality
avgRating = mean(competitors.ratings)
a2 = normalize(avgRating, 1, 5, inverse=true)

// A.3: Market Saturation
density = competitors.count / area_km2
a3 = normalize(density, 0, 100, inverse=true)

// A.4: Competitive Advantage
a4 = (a1 + a2) / 2

pillarA = (a1 + a2 + a3 + a4) / 4
```

### Pillar B: Traffic & Accessibility

```typescript
// B.1: Foot Traffic
pois = getNearbyPOIs(location, radius)
b1 = normalize(pois.count, 0, 50)

// B.2: Public Transport
stations = getTransitStations(location, 500m)
b2 = normalize(stations.count, 0, 5)

// B.3: Vehicle Accessibility
parking = getParkingSpots(location, radius)
b3 = normalize(parking.count, 0, 10)

// B.4: Visibility
b4 = (b1 + b2) / 2 + 2  // Bonus for visibility

pillarB = (b1 + b2 + b3 + b4) / 4
```

### Normalization Function

```typescript
function normalize(value, min, max, inverse = false) {
  clamped = clamp(value, min, max)
  normalized = ((clamped - min) / (max - min)) * 10
  return inverse ? 10 - normalized : normalized
}
```

### Weighted Total Score

```typescript
totalScore = 
  pillarA * weightA +
  pillarB * weightB +
  pillarC * weightC +
  pillarD * weightD +
  pillarE * weightE
```

### NFR7 Critical Risk Logic

```typescript
if (subScoreE1 < 2.0) {
  finalScore = min(totalScore, 5.0)
  hasRiskAlert = true
  displayRedAlert()
} else {
  finalScore = totalScore
  hasRiskAlert = false
}
```

## Communication Patterns

### Synchronous Communication (HTTP/REST)
- Frontend → Auth Service (authentication)
- Frontend → Core Service (analysis requests)
- Core Service → Auth Service (token verification)
- Core Service → Google Maps APIs (data fetching)

### Asynchronous Communication (Future Enhancement)
- Message Queue (RabbitMQ/Kafka) for:
  - Batch processing
  - Long-running analyses
  - Email notifications
  - Premium report generation

## Security Architecture

### Authentication Flow
```
1. User logs in → Frontend
2. Frontend → POST /auth/login → Auth Service
3. Auth Service validates credentials
4. Auth Service generates JWT
5. JWT returned to Frontend
6. Frontend stores JWT (HttpOnly cookie)
7. Frontend → Request with JWT → Core Service
8. Core Service → Verify JWT → Auth Service
9. Auth Service validates & returns user info
10. Core Service processes request
```

### Security Layers
1. **Network Layer**: HTTPS/TLS encryption
2. **Application Layer**: CORS, Helmet, Rate limiting
3. **Authentication Layer**: JWT with expiration
4. **Authorization Layer**: User-based resource access
5. **Data Layer**: Encrypted database, parameterized queries

## Performance Optimization

### Caching Strategy
```
Level 1: Browser Cache (static assets)
Level 2: CDN Cache (Next.js pages)
Level 3: Application Cache (Redis - future)
Level 4: Database Cache (Prisma query cache)
```

### API Call Optimization
- Parallel execution of 5 pillar analyses
- Request batching for Google Maps APIs
- Exponential backoff for rate limiting
- Circuit breaker pattern for fault tolerance

### Database Optimization
- Indexed queries on userId, createdAt
- Connection pooling
- Read replicas for analytics (future)
- Query result caching

## Monitoring & Observability

### Metrics to Track
- Request latency (p50, p95, p99)
- Error rates by service
- Database query performance
- API call success rates
- User authentication success/failure
- Analysis completion time

### Logging Strategy
- Structured JSON logs
- Log levels: ERROR, WARN, INFO, DEBUG
- Correlation IDs for request tracing
- Centralized log aggregation (ELK stack)

## Disaster Recovery

### Backup Strategy
- Database: Daily automated backups
- Configuration: Version controlled
- Secrets: Stored in secure vaults

### Recovery Procedures
- RTO (Recovery Time Objective): 4 hours
- RPO (Recovery Point Objective): 24 hours
- Automated failover for critical services
- Multi-region deployment (future)

## Future Enhancements

1. **Machine Learning Integration**
   - Predictive scoring models
   - Historical trend analysis
   - Anomaly detection

2. **Real-time Data Streaming**
   - WebSocket for live updates
   - Real-time competitor monitoring
   - Dynamic pricing suggestions

3. **Advanced Analytics**
   - Heat maps visualization
   - Time-series analysis
   - Comparative analytics

4. **API Ecosystem**
   - Public API for third-party integration
   - Webhook support
   - SDK for multiple languages

## Technology Decisions

### Why Microservices?
- Independent scaling
- Technology flexibility
- Fault isolation
- Team autonomy

### Why Next.js?
- SEO optimization
- Server-side rendering
- API routes
- Built-in optimization

### Why PostgreSQL?
- ACID compliance
- JSON support
- Scalability
- Strong ecosystem

### Why Prisma?
- Type-safe queries
- Migration management
- Multi-database support
- Developer experience

---

**Version**: 4.0  
**Last Updated**: 2024  
**Status**: Production Ready
