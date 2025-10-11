# Modern Todo List Application

A production-ready, microservices-based todo list application built with modern technologies for recruitment demonstration purposes.

## 🏗️ Architecture

This application follows a **microservices architecture** with the following components:

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Next.js 14)                   │
│                    Port 3000 - TypeScript                    │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────────┐
│               API Gateway (Node.js + Express)                │
│          Port 8080 - Rate Limiting & Security               │
└──────┬─────────────────────┬────────────────────────────────┘
       │                     │
       ▼                     ▼
┌──────────────────┐   ┌──────────────────────────────────────┐
│  Auth Service    │   │       Todo Service                   │
│  (Spring Boot)   │   │       (Golang + Gin)                 │
│  Port 8081       │   │       Port 8082                      │
│  DOMA ORM        │   │       PostgreSQL Driver              │
└──────┬───────────┘   └──────┬───────────────────────────────┘
       │                      │
       └──────────┬───────────┘
                  ▼
         ┌────────────────────┐
         │    PostgreSQL      │
         │    Port 5432       │
         └────────────────────┘
```

## ✨ Key Features

### 🔐 Security
- **JWT Authentication**: Secure token-based authentication with Spring Boot
- **Rate Limiting**: Protect APIs from abuse with configurable rate limits
- **CORS Protection**: Cross-origin resource sharing configuration
- **Helmet.js**: Security headers for Express applications
- **Password Hashing**: BCrypt password encryption

### 🚀 Performance
- **Microservices**: Independent scaling and deployment
- **Golang Service**: High-performance todo operations
- **Connection Pooling**: Optimized database connections
- **API Gateway**: Single entry point with load balancing capability

### 🎨 Modern UI
- **Next.js 14**: Latest React framework with App Router
- **TypeScript**: Type-safe development
- **shadcn/ui**: Beautiful, accessible component library
- **Tailwind CSS**: Utility-first CSS framework
- **Responsive Design**: Works on all devices

## 📦 Technology Stack

### Frontend
- **Next.js 14** - React framework with App Router
- **TypeScript** - Type-safe JavaScript
- **Tailwind CSS** - Utility-first CSS
- **shadcn/ui** - Component library
- **Lucide React** - Icon library

### Backend Services

#### Auth Service
- **Spring Boot 3.2** - Java application framework
- **DOMA 2** - Type-safe SQL mapping framework
- **JWT (jjwt)** - JSON Web Token implementation
- **BCrypt** - Password hashing
- **PostgreSQL** - Database

#### Todo Service
- **Golang 1.21** - High-performance language
- **Gin** - Web framework
- **PostgreSQL Driver** - Database connectivity
- **JWT** - Token verification

#### API Gateway
- **Node.js 18** - JavaScript runtime
- **Express** - Web framework
- **http-proxy-middleware** - Request proxying
- **express-rate-limit** - Rate limiting
- **Helmet** - Security headers

### Database
- **PostgreSQL 15** - Relational database
- **Indexed queries** - Optimized performance

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Node.js 18+ (for local development)
- Java 17+ (for local Spring Boot development)
- Go 1.21+ (for local Golang development)

### Running with Docker (Recommended)

1. **Clone the repository**
```bash
git clone <repository-url>
cd todo-app
```

2. **Start all services**
```bash
docker-compose up -d
```

3. **Access the application**
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Auth Service: http://localhost:8081
- Todo Service: http://localhost:8082
- PostgreSQL: localhost:5432

4. **View logs**
```bash
docker-compose logs -f
```

5. **Stop all services**
```bash
docker-compose down
```

### Running Locally (Development)

#### 1. Setup Database
```bash
# Start PostgreSQL
docker run -d \
  --name todo-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=todo_db \
  -p 5432:5432 \
  postgres:15-alpine

# Apply schema
psql -h localhost -U postgres -d todo_db -f schema.sql
```

#### 2. Start Auth Service (Spring Boot)
```bash
cd auth-service-spring
mvn clean install
mvn spring-boot:run
```

#### 3. Start Todo Service (Golang)
```bash
cd todo-service-go
go mod download
go run main.go
```

#### 4. Start API Gateway
```bash
cd api-gateway
npm install
npm run dev
```

#### 5. Start Frontend
```bash
cd todo-frontend
npm install
npm run dev
```

## 📖 API Documentation

### Auth Service (http://localhost:8081)

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrUsername": "john@example.com",
  "password": "password123"
}
```

#### Verify Token
```http
GET /api/auth/verify
Authorization: Bearer <your-jwt-token>
```

### Todo Service (http://localhost:8082)

All todo endpoints require authentication (Bearer token).

#### Get All Todos
```http
GET /api/todos
Authorization: Bearer <token>

# Optional query parameters:
# ?status=pending|in_progress|completed
# ?priority=low|medium|high
```

#### Get Todo by ID
```http
GET /api/todos/:id
Authorization: Bearer <token>
```

#### Create Todo
```http
POST /api/todos
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive README",
  "status": "pending",
  "priority": "high",
  "due_date": "2024-12-31T23:59:59Z"
}
```

#### Update Todo
```http
PUT /api/todos/:id
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated title",
  "status": "completed",
  "priority": "medium"
}
```

#### Delete Todo
```http
DELETE /api/todos/:id
Authorization: Bearer <token>
```

#### Get Statistics
```http
GET /api/todos/stats
Authorization: Bearer <token>
```

## 🗄️ Database Schema

### Users Table
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

### Todos Table
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

## 🔒 Security Features

### Authentication
- JWT-based stateless authentication
- Secure password hashing with BCrypt
- Token expiration (24 hours)

### Rate Limiting
- General API: 100 requests per 15 minutes
- Auth endpoints: 20 requests per 15 minutes
- Todo endpoints: 200 requests per 15 minutes

### Security Headers
- Helmet.js for Express security
- CORS configuration
- Content Security Policy

## 🏗️ Project Structure

```
todo-app/
├── auth-service-spring/          # Spring Boot Auth Service
│   ├── src/
│   │   └── main/
│   │       ├── java/com/todo/auth/
│   │       │   ├── config/       # Security config
│   │       │   ├── controller/   # REST controllers
│   │       │   ├── dto/          # Data transfer objects
│   │       │   ├── entity/       # DOMA entities
│   │       │   ├── repository/   # DOMA DAOs
│   │       │   ├── security/     # JWT utilities
│   │       │   └── service/      # Business logic
│   │       └── resources/
│   │           ├── META-INF/     # DOMA SQL files
│   │           └── application.properties
│   ├── pom.xml
│   └── Dockerfile
│
├── todo-service-go/              # Golang Todo Service
│   ├── database/                 # DB connection
│   ├── handlers/                 # HTTP handlers
│   ├── middleware/               # Auth middleware
│   ├── models/                   # Data models
│   ├── main.go
│   ├── go.mod
│   └── Dockerfile
│
├── api-gateway/                  # Node.js API Gateway
│   ├── src/
│   │   └── index.js             # Gateway logic
│   ├── package.json
│   └── Dockerfile
│
├── todo-frontend/                # Next.js Frontend
│   ├── app/
│   │   ├── login/               # Login page
│   │   ├── register/            # Registration page
│   │   ├── dashboard/           # Dashboard page
│   │   ├── page.tsx             # Home page
│   │   ├── layout.tsx           # Root layout
│   │   └── globals.css          # Global styles
│   ├── components/
│   │   └── ui/                  # shadcn/ui components
│   ├── lib/
│   │   └── utils.ts             # Utilities
│   ├── package.json
│   └── Dockerfile
│
├── schema.sql                    # Database schema
├── docker-compose.yml            # Docker orchestration
└── README.md                     # This file
```

## 🧪 Testing

### Manual Testing

1. **Register a new user**
   - Go to http://localhost:3000
   - Click "Get Started"
   - Fill in the registration form

2. **Login**
   - Use your credentials to login
   - You'll be redirected to the dashboard

3. **Create todos**
   - Add a new task using the form
   - View your tasks in the list

4. **Update todo status**
   - Click the circle icon to change status
   - Status cycles: pending → in_progress → completed → pending

5. **Filter todos**
   - Use the filter buttons to view specific statuses

6. **Delete todos**
   - Click the trash icon to delete a task

## 🎯 Use Cases

### For Recruitment Purposes

This project demonstrates:

1. **Full-Stack Development**
   - Frontend: React/Next.js with modern UI
   - Backend: Multiple languages (Java, Go, JavaScript)
   - Database: PostgreSQL with proper schema design

2. **Microservices Architecture**
   - Service isolation
   - Independent deployment
   - Inter-service communication

3. **Best Practices**
   - Clean code architecture
   - Type safety (TypeScript, Go, Java)
   - Error handling
   - Security considerations

4. **DevOps Skills**
   - Docker containerization
   - Docker Compose orchestration
   - Environment configuration

5. **Modern Technologies**
   - Spring Boot 3.2
   - DOMA ORM
   - Golang with Gin
   - Next.js 14
   - shadcn/ui

## 🚧 Future Enhancements

- [ ] Real-time updates with WebSockets
- [ ] Email notifications
- [ ] Task categories and tags
- [ ] Task sharing and collaboration
- [ ] File attachments
- [ ] Dark mode
- [ ] Mobile app (React Native)
- [ ] CI/CD pipeline
- [ ] Kubernetes deployment
- [ ] Monitoring and logging (Prometheus, Grafana)
- [ ] Unit and integration tests

## 📄 License

MIT License

## 👥 Author

Created for recruitment demonstration purposes.

## 🙏 Acknowledgments

- Spring Boot Team
- Golang Community
- Next.js Team
- shadcn for the UI components
- All open-source contributors

---

**Made with ❤️ for modern web development**
