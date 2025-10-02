# Technical Requirements Document (TRD)
# Location Score AI V4.0

## 1. System Overview

Location Score AI V4.0 is a microservices-based system that provides comprehensive location analysis for businesses. The system evaluates locations using a sophisticated 5-pillar scoring methodology integrated with real-time data from Google Maps APIs.

## 2. Architecture

### 2.1 Microservices Architecture

| Service | Technology | Port | Purpose |
|---------|-----------|------|---------|
| Frontend Service | Next.js 14 + TypeScript | 3000 | User interface, authentication, visualization |
| Auth Service | Node.js/Express + TypeScript | 3001 | User management, JWT, OAuth |
| Core Data Service | Node.js/Express + TypeScript | 3002 | Business logic, scoring, Google Maps integration |
| Database | PostgreSQL 15 + Prisma ORM | 5432 | Data persistence |

### 2.2 Communication
- **Protocol**: HTTP/REST
- **Authentication**: JWT tokens
- **Data Format**: JSON

## 3. Functional Requirements

### FR1: Input & Configuration

| ID | Requirement | Implementation |
|----|-------------|----------------|
| FR1.1 | Address/Coordinate Input | Geocoding API integration, lat/lng validation |
| FR1.2 | Multi-Industry Selection | F&B, Retail, Service with sub-types |
| FR1.3 | Weight Adjustment | Dynamic weight configuration (0-1, sum=1) |
| FR1.4 | Radius Selection | 300m, 500m, 1000m, custom up to 5000m |

### FR2: Core Data Processing

| ID | Requirement | API/Data Source | Logic |
|----|-------------|-----------------|-------|
| FR2.1 | 5-Pillar Analysis | Google Maps APIs | 12+ sub-scores calculation |
| FR2.2 | Score Normalization | N/A | 0-10 scale normalization |
| FR2.3 | Legal Data Integration | Simulated/Static DB | Urban planning risk assessment |
| FR2.4 | Dynamic Total Score | N/A | Weighted sum calculation |

### FR3: Authentication & User Management

| ID | Requirement | Service | Implementation |
|----|-------------|---------|----------------|
| FR3.1 | Registration/Login | Auth Service | Email/password + OAuth |
| FR3.2 | Session Management | Auth + Core | JWT with verification |
| FR3.3 | Analysis History | Core Service | Database persistence |
| FR3.4 | Premium Payment | FE + Core | Stripe/VNPay integration |

### FR4: Visualization & Output

| ID | Requirement | Purpose |
|----|-------------|---------|
| FR4.1 | Chart Visualization | Radar chart for 5-pillar comparison |
| FR4.2 | Raw Data Display | Transparency and credibility |
| FR4.3 | Risk Alerts | Critical threshold warnings |
| FR4.4 | Improvement Suggestions | Actionable recommendations |
| FR4.5 | Location Comparison | Premium multi-location analysis |

## 4. Scoring Logic

### 4.1 Five Core Pillars

#### Pillar A: Competition & Saturation (25-35%)
- **A.1**: Competitor Density (0-20 competitors → inverse normalization)
- **A.2**: Competitor Quality (avg rating 1-5 → inverse)
- **A.3**: Market Saturation (0-100 per km² → inverse)
- **A.4**: Competitive Advantage (derived from A.1 + A.2)

#### Pillar B: Traffic & Accessibility (15-35%)
- **B.1**: Foot Traffic (0-50 POIs → direct normalization)
- **B.2**: Public Transport (0-5 stations → direct)
- **B.3**: Vehicle Access (0-10 parking spots → direct)
- **B.4**: Visibility Score (derived)

#### Pillar C: Socio-Economic (20-40%)
- **C.1**: Customer Match (0-100 score → direct)
- **C.2**: Population Density (0-10000 per km² → direct)
- **C.3**: Income Level ($10k-$100k → direct)

#### Pillar D: Infrastructure & Environment (10%)
- **D.1**: Parking (0-10 facilities → direct)
- **D.2**: Safety (0-3 facilities → direct)
- **D.3**: Aesthetics (0-5 parks → direct)

#### Pillar E: Macro & Legal (10-15%)
- **E.1**: Urban Planning Risk (0-100 → inverse) **[CRITICAL]**
- **E.2**: Future Development (0-100 → direct)
- **E.3**: Economic Outlook (0-100 → direct)

### 4.2 Normalization Formula

```typescript
normalizedScore = ((value - minRange) / (maxRange - minRange)) * 10

// For inverse metrics:
normalizedScore = 10 - normalizedScore
```

### 4.3 Weighted Score Calculation

```typescript
totalScore = (A * wA) + (B * wB) + (C * wC) + (D * wD) + (E * wE)
```

### 4.4 NFR7 Critical Risk Logic

```typescript
if (E.1 < 2.0) {
  finalScore = Math.min(totalScore, 5.0)
  hasRiskAlert = true
  displayRedAlert()
}
```

## 5. Non-Functional Requirements

### NFR1: Performance
- API response time: < 4 seconds
- Database query optimization
- Parallel API calls

### NFR2: Scalability
- Docker containerization
- Horizontal scaling support
- Load balancing ready

### NFR3: Security
- Environment variable protection
- HttpOnly cookies for JWT
- Password hashing (bcrypt)
- API key encryption

### NFR4: Reliability
- Exponential backoff for API retries
- Error handling and logging
- Graceful degradation

### NFR5: Data Integrity
- Database transactions
- Foreign key constraints
- Data validation

### NFR6: Usability
- Responsive design (mobile/desktop)
- Intuitive UI/UX
- Loading states and feedback

### NFR7: Critical Risk Alert
- Automatic detection (E.1 < 2.0)
- Score capping at 5.0
- Red alert display
- Recommendation system

## 6. Data Models

### User Model
```prisma
model User {
  id            String    @id @default(uuid())
  email         String    @unique
  password      String?
  name          String?
  provider      String?
  providerId    String?
  createdAt     DateTime  @default(now())
  updatedAt     DateTime  @updatedAt
  analysisResults AnalysisResult[]
}
```

### AnalysisResult Model
```prisma
model AnalysisResult {
  id                String    @id @default(uuid())
  userId            String
  address           String
  latitude          Float
  longitude         Float
  industry          String
  radius            Int
  
  // Weights
  weightA-E         Float
  
  // Pillar Scores
  pillarA-E_score   Float
  
  // Sub-scores (12+)
  subScoreA1-A4     Float
  subScoreB1-B4     Float
  subScoreC1-C3     Float
  subScoreD1-D3     Float
  subScoreE1-E3     Float
  
  totalScore        Float
  hasRiskAlert      Boolean
  rawData           Json?
  isPremium         Boolean
  
  createdAt         DateTime  @default(now())
}
```

## 7. API Endpoints

### Auth Service (3001)
```
POST   /auth/register       - User registration
POST   /auth/login          - User login
POST   /auth/oauth          - OAuth login
GET    /auth/verify         - Token verification
```

### Core Service (3002)
```
POST   /core/score          - Analyze location
GET    /core/history        - Get analysis history
GET    /core/analysis/:id   - Get specific analysis
```

## 8. External Dependencies

### Google Maps APIs
- Places API (competitor search)
- Nearby Search API (POI discovery)
- Geocoding API (address conversion)
- Directions API (accessibility)

### OAuth Providers
- Google OAuth 2.0
- Facebook OAuth 2.0

### Payment Gateways
- Stripe (international)
- VNPay (Vietnam)

## 9. Deployment

### Docker Compose
```yaml
services:
  - postgres (database)
  - auth-service (authentication)
  - core-service (business logic)
  - frontend (user interface)
```

### Environment Variables
- DATABASE_URL
- JWT_SECRET
- GOOGLE_MAPS_API_KEY
- OAuth credentials
- Payment gateway keys

## 10. Testing Strategy

### Unit Tests
- Service layer logic
- Scoring calculations
- Normalization functions

### Integration Tests
- API endpoint testing
- Database operations
- Authentication flow

### End-to-End Tests
- User registration/login
- Location analysis flow
- Premium features

## 11. Monitoring & Logging

### Metrics
- API response times
- Error rates
- User activity
- Database performance

### Logging
- Request/response logs
- Error tracking
- Audit trails
- Performance metrics

## 12. Future Enhancements

1. Machine learning for predictive scoring
2. Real-time demographic data integration
3. Mobile native applications
4. Advanced visualization (heat maps)
5. Batch location analysis
6. API for third-party integration
7. Multi-language support
8. Real estate price prediction

## Document Version
- Version: 4.0
- Last Updated: 2024
- Status: Implementation Complete
