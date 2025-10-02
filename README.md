# Location Score AI V4.0 🎯

A comprehensive microservices-based location analysis system that evaluates business locations using 5 core pillars and 12+ sub-indicators with real-time data integration.

## 🏗️ Architecture Overview

The system is built on a **Microservices Architecture** with three core services:

| Service | Technology | Port | Description |
|---------|-----------|------|-------------|
| **Frontend** | Next.js 14 (TypeScript) | 3000 | UI/UX, Authentication, Visualization |
| **Auth Service** | Node.js/Express (TypeScript) | 3001 | User Management, JWT Auth, OAuth |
| **Core Service** | Node.js/Express (TypeScript) | 3002 | Business Logic, Scoring Engine, Google Maps API |
| **Database** | PostgreSQL + Prisma ORM | 5432 | User Data, Analysis Results |

## ✨ Key Features

### 🎯 5-Pillar Scoring System
- **Pillar A: Competition & Saturation** (25-35%)
  - Competitor Density Analysis
  - Competitor Quality Assessment
  - Market Saturation Index
  - Competitive Advantage Score

- **Pillar B: Traffic & Accessibility** (15-35%)
  - Foot Traffic Estimation
  - Public Transport Access
  - Vehicle Accessibility
  - Visibility Score

- **Pillar C: Socio-Economic** (20-40%)
  - Target Customer Match
  - Population Density
  - Income Level Analysis

- **Pillar D: Infrastructure & Environment** (10%)
  - Parking Availability
  - Safety Score
  - Aesthetic Score

- **Pillar E: Macro & Legal Risk** (10-15%)
  - Urban Planning Risk (CRITICAL)
  - Future Development Potential
  - Economic Outlook

### 🚨 Critical Risk Alert System (NFR7)
- Automatic detection of high-risk locations
- Score capping at 5.0/10 when E.1 (Urban Planning Risk) < 2.0
- Red alert notifications with actionable recommendations

### 📊 Advanced Features
- Dynamic weight customization for all pillars
- Real-time analysis (< 4 seconds response time)
- Interactive radar chart visualization
- Multi-location comparison (Premium)
- Raw data transparency
- Industry-specific analysis (F&B, Retail, Service)

## 🚀 Quick Start

### Prerequisites
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 15
- Google Maps API Key

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/lequockhanh19521680/Location-Analysis-Web.git
cd Location-Analysis-Web
```

2. **Set up environment variables**
```bash
cp .env.example .env
# Edit .env with your actual credentials
```

3. **Install dependencies**
```bash
# Install database dependencies
cd database && npm install && cd ..

# Install auth service dependencies
cd auth-service && npm install && cd ..

# Install core service dependencies
cd core-service && npm install && cd ..

# Install frontend dependencies
cd frontend && npm install && cd ..
```

4. **Initialize the database**
```bash
cd database
npx prisma migrate dev
npx prisma generate
cd ..
```

5. **Run with Docker Compose (Recommended)**
```bash
docker-compose up -d
```

Or run services individually:

```bash
# Terminal 1 - Auth Service
cd auth-service
npm run dev

# Terminal 2 - Core Service
cd core-service
npm run dev

# Terminal 3 - Frontend
cd frontend
npm run dev
```

6. **Access the application**
- Frontend: http://localhost:3000
- Auth Service: http://localhost:3001
- Core Service: http://localhost:3002

## 📖 API Documentation

### Auth Service (Port 3001)

#### Register
```bash
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe"
}
```

#### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### OAuth Login
```bash
POST /auth/oauth
Content-Type: application/json

{
  "provider": "google",
  "providerId": "google-user-id",
  "email": "user@example.com",
  "name": "John Doe"
}
```

#### Verify Token
```bash
GET /auth/verify
Authorization: Bearer <token>
```

### Core Service (Port 3002)

#### Analyze Location
```bash
POST /core/score
Authorization: Bearer <token>
Content-Type: application/json

{
  "address": "123 Main St, City",
  "latitude": 10.762622,
  "longitude": 106.660172,
  "industry": "F&B",
  "industrySubType": "cafe",
  "radius": 500,
  "weights": {
    "weightA": 0.30,
    "weightB": 0.25,
    "weightC": 0.30,
    "weightD": 0.10,
    "weightE": 0.05
  }
}
```

#### Get Analysis History
```bash
GET /core/history?page=1&limit=10
Authorization: Bearer <token>
```

#### Get Analysis by ID
```bash
GET /core/analysis/:id
Authorization: Bearer <token>
```

## 🧪 Testing

```bash
# Run tests for auth service
cd auth-service && npm test

# Run tests for core service
cd core-service && npm test

# Run frontend tests
cd frontend && npm test
```

## 📊 Scoring Logic

### Normalization Formula
All sub-scores are normalized to a 0-10 scale:

```typescript
normalizedScore = ((value - minRange) / (maxRange - minRange)) * 10
```

For inverse metrics (higher is worse):
```typescript
normalizedScore = 10 - normalizedScore
```

### Weighted Total Score
```typescript
totalScore = (pillarA * weightA) + (pillarB * weightB) + 
             (pillarC * weightC) + (pillarD * weightD) + 
             (pillarE * weightE)
```

### Critical Risk Threshold (NFR7)
```typescript
if (subScoreE1 < 2.0) {
  finalScore = Math.min(totalScore, 5.0)
  hasRiskAlert = true
}
```

## 🔒 Security Features

- JWT-based authentication with HttpOnly cookies
- OAuth 2.0 support (Google, Facebook)
- Bcrypt password hashing (10 rounds)
- Environment variable protection for API keys
- CORS configuration
- Helmet.js security headers
- Rate limiting on API endpoints

## 🔧 Configuration

### Default Weights
- Competition & Saturation: 30%
- Traffic & Accessibility: 25%
- Socio-Economic: 30%
- Infrastructure: 10%
- Legal Risk: 5%

### Supported Industries
- **F&B**: Restaurant, Cafe, Food Service
- **Retail**: Store, Shopping Mall, Clothing Store
- **Service**: Beauty Salon, Gym, Spa

### Radius Options
- 300m: Small neighborhood analysis
- 500m: Standard area analysis
- 1000m: Large area analysis
- Custom: Up to 5000m

## 📦 Database Schema

### Users Table
- id (UUID, Primary Key)
- email (Unique)
- password (Hashed)
- name
- provider (local/google/facebook)
- providerId

### AnalysisResults Table
- id (UUID, Primary Key)
- userId (Foreign Key)
- location data (address, lat, lng, radius)
- industry information
- weight configuration
- 5 pillar scores
- 12+ sub-scores (A.1-A.4, B.1-B.4, C.1-C.3, D.1-D.3, E.1-E.3)
- totalScore
- hasRiskAlert
- rawData (JSON)
- isPremium
- timestamps

## 🌐 Deployment

### Production Build
```bash
# Build all services
docker-compose -f docker-compose.yml build

# Start in production mode
docker-compose up -d
```

### Environment Variables for Production
Ensure these are set:
- `DATABASE_URL`
- `JWT_SECRET`
- `NEXTAUTH_SECRET`
- `GOOGLE_MAPS_API_KEY`
- OAuth credentials (if using)
- Payment gateway credentials (if using premium features)

## 📈 Performance Benchmarks

- Analysis Response Time: < 4 seconds (NFR1)
- API Call Retry: Exponential backoff (NFR4)
- Database Transactions: ACID compliant (NFR5)
- Horizontal Scaling: Docker-ready (NFR2)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👥 Authors

- Le Quoc Khanh - [@lequockhanh19521680](https://github.com/lequockhanh19521680)

## 🙏 Acknowledgments

- Google Maps Platform for geospatial data
- Next.js team for the amazing framework
- Prisma for the excellent ORM
- All contributors and testers

## 📞 Support

For issues and questions:
- GitHub Issues: [Create an issue](https://github.com/lequockhanh19521680/Location-Analysis-Web/issues)
- Email: support@locationscore.ai

---

**Made with ❤️ by Location Score AI Team**