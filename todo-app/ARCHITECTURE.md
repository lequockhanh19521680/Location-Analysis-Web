# System Architecture Documentation

## Overview

This TODO list application is built using a **microservices architecture** pattern with independent services communicating through an API Gateway. The system is designed for high availability, security, and scalability following modern best practices.

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                          Internet/Users                           │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │   Frontend (Next.js)  │
                  │   Port 3000           │
                  │   - TypeScript        │
                  │   - shadcn/ui         │
                  │   - Tailwind CSS      │
                  └──────────┬────────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │   API Gateway         │
                  │   Port 8080           │
                  │   - Rate Limiting     │
                  │   - CORS              │
                  │   - Helmet Security   │
                  └──────────┬────────────┘
                             │
                ┌────────────┴────────────┐
                │                         │
                ▼                         ▼
    ┌───────────────────┐     ┌───────────────────┐
    │  Auth Service     │     │  Todo Service     │
    │  (Spring Boot)    │     │  (Golang)         │
    │  Port 8081        │     │  Port 8082        │
    │  - DOMA ORM       │     │  - Gin Framework  │
    │  - JWT            │     │  - SQL Driver     │
    │  - BCrypt         │     │  - JWT Verify     │
    └─────────┬─────────┘     └─────────┬─────────┘
              │                         │
              └────────────┬────────────┘
                           ▼
                ┌──────────────────────┐
                │   PostgreSQL 15      │
                │   Port 5432          │
                │   - Users Table      │
                │   - Todos Table      │
                │   - Indexes          │
                └──────────────────────┘
```

## Service Breakdown

### 1. Frontend Service (Next.js 14)

**Technology Stack:**
- Next.js 14 with App Router
- TypeScript 5
- Tailwind CSS 3
- shadcn/ui components
- Lucide React icons

**Responsibilities:**
- User interface rendering
- Client-side routing
- Form validation
- State management
- API calls to gateway
- Authentication state

**Key Features:**
- Server-side rendering (SSR)
- Static site generation (SSG)
- Client-side navigation
- Responsive design
- Modern UI components

**Pages:**
- `/` - Landing page with features
- `/login` - User authentication
- `/register` - User registration
- `/dashboard` - Todo management interface

### 2. API Gateway (Node.js + Express)

**Technology Stack:**
- Node.js 18
- Express 4.18
- http-proxy-middleware
- express-rate-limit
- helmet
- cors

**Responsibilities:**
- Single entry point for all requests
- Request routing to microservices
- Rate limiting enforcement
- Security headers
- CORS management
- Error handling

**Rate Limits:**
```javascript
General API: 100 requests / 15 minutes
Auth endpoints: 20 requests / 15 minutes
Todo endpoints: 200 requests / 15 minutes
```

**Routes:**
- `/api/auth/*` → Auth Service
- `/api/todos/*` → Todo Service
- `/health` → Health check

### 3. Auth Service (Spring Boot 3.2)

**Technology Stack:**
- Spring Boot 3.2
- Spring Security
- DOMA 2.56 (SQL Mapper)
- JWT (jjwt 0.12.3)
- BCrypt
- PostgreSQL Driver

**Responsibilities:**
- User registration
- User authentication
- JWT token generation
- Token validation
- Password hashing
- User management

**Endpoints:**
```
POST   /api/auth/register    - Create new user
POST   /api/auth/login       - Authenticate user
GET    /api/auth/verify      - Validate JWT token
GET    /health               - Health check
```

**DOMA Integration:**
- Type-safe SQL mapping
- External SQL files
- Compile-time verification
- Zero runtime overhead

**Security Features:**
- BCrypt password hashing (10 rounds)
- JWT token with 24-hour expiration
- Spring Security configuration
- CORS enabled

### 4. Todo Service (Golang + Gin)

**Technology Stack:**
- Golang 1.21
- Gin web framework
- PostgreSQL driver (lib/pq)
- JWT verification (golang-jwt)

**Responsibilities:**
- CRUD operations for todos
- Todo filtering (status, priority)
- Statistics calculation
- JWT token verification
- Database queries

**Endpoints:**
```
GET    /api/todos           - List all todos (with filters)
GET    /api/todos/:id       - Get specific todo
POST   /api/todos           - Create new todo
PUT    /api/todos/:id       - Update todo
DELETE /api/todos/:id       - Delete todo
GET    /api/todos/stats     - Get statistics
GET    /health              - Health check
```

**Features:**
- High performance (Golang)
- Concurrent request handling
- Optimized SQL queries
- Connection pooling

### 5. Database (PostgreSQL 15)

**Schema Design:**

**Users Table:**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Todos Table:**
```sql
CREATE TABLE todos (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    priority VARCHAR(20) DEFAULT 'medium',
    due_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_todos_user_id` - Fast user-specific queries
- `idx_todos_status` - Filter by status
- `idx_todos_priority` - Filter by priority
- `idx_users_email` - Fast login lookups
- `idx_users_username` - Fast username lookups

**Triggers:**
- Auto-update `updated_at` on record modification

## Communication Patterns

### 1. Authentication Flow

```
User → Frontend → API Gateway → Auth Service → Database
                                      ↓
                                  JWT Token
                                      ↓
User ← Frontend ← API Gateway ← Auth Service
```

**Steps:**
1. User submits credentials (email/username + password)
2. Frontend sends POST to `/api/auth/login`
3. API Gateway routes to Auth Service
4. Auth Service queries database
5. Auth Service validates password (BCrypt)
6. Auth Service generates JWT token
7. Token returned to frontend
8. Frontend stores token in localStorage

### 2. Protected Resource Access

```
User → Frontend → API Gateway → Todo Service → Database
         (JWT)        (JWT)         (JWT)
                                      ↓
User ← Frontend ← API Gateway ← Todo Service (Data)
```

**Steps:**
1. Frontend includes JWT in Authorization header
2. API Gateway forwards request with token
3. Todo Service validates JWT
4. If valid, processes request
5. Returns data or error

### 3. Rate Limiting Flow

```
Request → API Gateway → Rate Limiter → Service
              ↓
         Exceeds limit?
              ↓
         429 Error ← Return
```

## Security Architecture

### 1. Authentication & Authorization

**JWT Token Structure:**
```json
{
  "userId": 123,
  "sub": "username",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

**Token Lifecycle:**
- Generated on successful login
- Expires in 24 hours
- Validated on every protected request
- Stored client-side (localStorage)

### 2. Password Security

**Hashing:**
- Algorithm: BCrypt
- Cost factor: 10 rounds
- Salt: Auto-generated per password

### 3. Rate Limiting

**Per-IP Limits:**
- Prevents brute force attacks
- Prevents DDoS attacks
- Configurable per endpoint
- Standard headers (RateLimit-Limit, RateLimit-Remaining)

### 4. CORS Protection

**Configuration:**
```javascript
origin: '*' // Configure per environment
credentials: true
methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS']
```

### 5. Security Headers (Helmet.js)

- X-DNS-Prefetch-Control
- X-Frame-Options
- X-Content-Type-Options
- X-XSS-Protection
- Content-Security-Policy

## Data Flow Examples

### Create Todo Flow

```
1. User fills form in Dashboard
2. Frontend validates input
3. POST /api/todos with JWT token
4. API Gateway checks rate limit
5. API Gateway forwards to Todo Service
6. Todo Service validates JWT
7. Todo Service extracts userId from token
8. Todo Service inserts into database
9. Database returns new todo
10. Response flows back through chain
11. Frontend updates UI
```

### Filter Todos Flow

```
1. User selects filter (e.g., "completed")
2. Frontend sends GET /api/todos?status=completed
3. API Gateway routes request
4. Todo Service validates token
5. Todo Service queries: SELECT * FROM todos WHERE user_id=? AND status=?
6. Database returns filtered results
7. Response sent back to frontend
8. Frontend renders filtered list
```

## Scalability Considerations

### Horizontal Scaling

**Stateless Services:**
- All services are stateless
- Can run multiple instances
- Load balancer can distribute traffic

**Example:**
```yaml
auth-service:
  replicas: 3
  
todo-service:
  replicas: 5
  
api-gateway:
  replicas: 2
```

### Database Scaling

**Current:**
- Single PostgreSQL instance
- Indexed queries for performance

**Future:**
- Read replicas for queries
- Master for writes
- Connection pooling
- Query optimization

### Caching Strategy (Future)

**Redis Integration:**
```
API Gateway → Redis Cache → Service
                 ↓
            Cache Hit? → Return
            Cache Miss → Query DB → Cache Result
```

## Deployment Architecture

### Docker Containerization

**Services:**
```
todo-postgres:      PostgreSQL 15 Alpine
auth-service:       Java 17 + Spring Boot
todo-service:       Golang 1.21 Alpine
api-gateway:        Node.js 18 Alpine
frontend:           Node.js 18 + Next.js
```

**Network:**
- Custom bridge network: `todo-network`
- Internal service communication
- Exposed ports for external access

**Volumes:**
- `postgres_data` - Database persistence

### Environment Variables

**Secrets Management:**
- JWT secret
- Database credentials
- API keys

**Configuration:**
- Service URLs
- Port numbers
- Feature flags

## Monitoring & Observability (Future)

### Health Checks

All services expose `/health` endpoint:
```json
{
  "status": "ok",
  "service": "auth-service"
}
```

### Logging

**Structured Logging:**
- Request/response logging
- Error logging
- Performance metrics

**Log Levels:**
- DEBUG - Development
- INFO - Production
- WARN - Issues
- ERROR - Failures

### Metrics (Proposed)

- Request count
- Response times
- Error rates
- Active users
- Database query times

## Technology Decision Rationale

### Why Microservices?

1. **Independent Deployment** - Update services separately
2. **Technology Flexibility** - Different languages per service
3. **Scalability** - Scale services independently
4. **Fault Isolation** - Service failures don't cascade
5. **Team Autonomy** - Different teams can own services

### Why Spring Boot for Auth?

1. **Enterprise-grade** - Proven in production
2. **Security** - Spring Security is mature
3. **DOMA ORM** - Type-safe SQL mapping
4. **Java Ecosystem** - Rich library support
5. **Recruitment Demo** - Shows Java proficiency

### Why Golang for Todos?

1. **Performance** - Fast execution
2. **Concurrency** - Built-in goroutines
3. **Low Memory** - Efficient resource usage
4. **Simple Deployment** - Single binary
5. **Modern** - Trending technology

### Why Next.js?

1. **SSR/SSG** - SEO and performance
2. **React** - Popular framework
3. **TypeScript** - Type safety
4. **Developer Experience** - Great DX
5. **Production Ready** - Used by major companies

### Why PostgreSQL?

1. **ACID Compliance** - Data integrity
2. **Performance** - Fast queries
3. **JSON Support** - Flexible schema
4. **Open Source** - No licensing costs
5. **Ecosystem** - Great tooling

## Best Practices Implemented

### Code Organization

- **Separation of Concerns** - Clear boundaries
- **DRY Principle** - Reusable code
- **SOLID Principles** - Clean architecture
- **Type Safety** - TypeScript, Go, Java

### Security

- **Least Privilege** - Minimal permissions
- **Defense in Depth** - Multiple security layers
- **Secure by Default** - Safe configurations
- **Input Validation** - All user input validated

### Performance

- **Database Indexing** - Fast queries
- **Connection Pooling** - Reuse connections
- **Efficient Queries** - Optimized SQL
- **Caching Ready** - Structured for caching

### DevOps

- **Containerization** - Docker for all services
- **Orchestration** - Docker Compose
- **Environment Parity** - Dev = Prod
- **Health Checks** - Service monitoring

## Conclusion

This architecture demonstrates modern software engineering practices suitable for production environments and serves as an excellent showcase for recruitment purposes. It balances complexity with maintainability while using industry-standard technologies.
