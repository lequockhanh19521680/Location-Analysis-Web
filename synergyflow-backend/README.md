# SynergyFlow Backend - Microservices Architecture

## 📋 Tổng Quan

SynergyFlow là một nền tảng quản lý công việc và giao tiếp đội nhóm tất cả-trong-một, kết hợp mô hình giao tiếp theo kênh của Discord với khả năng quản lý tác vụ trực quan của Trello.

## 🏗️ Kiến Trúc Hệ Thống

Hệ thống được xây dựng theo kiến trúc **Domain-Oriented Microservice Architecture (DOMA)** với Spring Boot và Gradle.

### Các Microservices

| Service | Port | Mô tả | Database |
|---------|------|-------|----------|
| **Service Discovery** | 8761 | Netflix Eureka - Service Registry | - |
| **API Gateway** | 8080 | Spring Cloud Gateway - Rate Limiting & Auth | Redis |
| **Auth Service** | 8081 | Xác thực người dùng, JWT, Google OAuth 2.0 | PostgreSQL |
| **Workspace Service** | 8082 | Quản lý Workspace, Channel, Members | PostgreSQL |
| **Task Service** | 8083 | Quản lý Task, Kanban Board, Drag-Drop | PostgreSQL |
| **Notification Service** | 8084 | Thông báo real-time qua WebSocket | Redis |
| **Reporting Service** | 8085 | Dashboard, Analytics, Báo cáo | - |

### Infrastructure Components

- **PostgreSQL 15**: Cơ sở dữ liệu chính cho các service
- **Redis**: Cache và real-time notifications
- **RabbitMQ**: Message queue cho giao tiếp bất đồng bộ
- **Netflix Eureka**: Service discovery
- **Spring Cloud Gateway**: API Gateway với rate limiting

## ✨ Tính Năng Chính

### F1: Quản lý Người dùng & Xác thực
- ✅ Đăng ký/Đăng nhập bằng Email/Password
- ✅ Google OAuth 2.0 integration
- ✅ JWT Access Token & Refresh Token
- ✅ Quản lý thông tin cá nhân

### F2: Quản lý Workspace & Channel
- ✅ Tạo/Tham gia nhiều Workspace
- ✅ Tạo Channel trong Workspace
- ✅ Quản lý thành viên (OWNER, ADMIN, MEMBER)
- ✅ Phân quyền theo vai trò

### F3: Quản lý Công việc (Task Management)
- ✅ Bảng Kanban với drag-and-drop
- ✅ Task Card với đầy đủ thông tin (tiêu đề, mô tả, deadline, priority, assignee)
- ✅ Di chuyển task giữa các cột
- ✅ Tự động reorder khi di chuyển
- ✅ Assign task cho nhiều người

### F4: Các Chế độ xem (Views)
- ✅ Board View: Giao diện Kanban
- ✅ Calendar View: Hiển thị task theo ngày deadline

### F5: Thông báo Real-time
- ✅ WebSocket notifications
- ✅ Thông báo khi được assign task
- ✅ Redis để lưu trữ notifications

### F6: Dashboard & Báo cáo
- ✅ Thống kê tiến độ dự án
- ✅ Phân tích hiệu suất team
- ✅ Biểu đồ task theo trạng thái, priority

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Gradle 7.6+

### Chạy với Docker Compose (Khuyến nghị)

```bash
# Clone repository
cd synergyflow-backend

# Build và khởi động tất cả services
docker-compose up --build -d

# Xem logs
docker-compose logs -f

# Dừng services
docker-compose down
```

### Chạy từng service riêng lẻ

```bash
# 1. Start infrastructure
docker-compose up postgres-auth postgres-workspace postgres-task redis rabbitmq -d

# 2. Build project
./gradlew build -x test

# 3. Start Service Discovery
./gradlew :service-discovery:bootRun

# 4. Start API Gateway
./gradlew :api-gateway:bootRun

# 5. Start các microservices
./gradlew :auth-service:bootRun
./gradlew :workspace-service:bootRun
./gradlew :task-service:bootRun
./gradlew :notification-service:bootRun
./gradlew :reporting-service:bootRun
```

## 📖 API Documentation

### Auth Service (Port 8081)

#### Đăng ký
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Nguyen Van A"
}
```

#### Đăng nhập
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "userId": "uuid",
      "email": "user@example.com",
      "fullName": "Nguyen Van A"
    }
  }
}
```

#### Google OAuth
```bash
POST /api/auth/oauth/google
Content-Type: application/json

{
  "googleId": "google-user-id",
  "email": "user@gmail.com",
  "fullName": "Nguyen Van A",
  "avatarUrl": "https://..."
}
```

#### Refresh Token
```bash
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Workspace Service (Port 8082)

#### Tạo Workspace
```bash
POST /api/workspaces
Authorization: Bearer {accessToken}

{
  "name": "My Workspace",
  "description": "Project workspace"
}
```

#### Tạo Channel
```bash
POST /api/workspaces/{workspaceId}/channels
Authorization: Bearer {accessToken}

{
  "name": "General",
  "description": "General discussion"
}
```

#### Thêm Member
```bash
POST /api/workspaces/{workspaceId}/members
Authorization: Bearer {accessToken}

{
  "userId": "user-id",
  "role": "MEMBER"
}
```

### Task Service (Port 8083)

#### Tạo Task Board cho Channel
```bash
GET /api/tasks/boards/channel/{channelId}
Authorization: Bearer {accessToken}
```

#### Tạo Column trong Board
```bash
POST /api/tasks/boards/channel/{channelId}/columns
Authorization: Bearer {accessToken}

{
  "title": "To Do"
}
```

#### Tạo Task
```bash
POST /api/tasks/columns/{columnId}
Authorization: Bearer {accessToken}

{
  "title": "Implement login feature",
  "description": "Create login page with form validation",
  "dueDate": "2025-10-15T10:00:00",
  "priority": "HIGH",
  "assignedUserIds": ["user-id-1", "user-id-2"]
}
```

#### Di chuyển Task (Drag & Drop)
```bash
POST /api/tasks/{taskId}/move
Authorization: Bearer {accessToken}

{
  "targetColumnId": "column-id",
  "newOrder": 2
}
```

#### Lấy Tasks theo Calendar View
```bash
GET /api/tasks/calendar?start=2025-10-01T00:00:00&end=2025-10-31T23:59:59
Authorization: Bearer {accessToken}
```

### Notification Service (Port 8084)

#### Lấy Notifications
```bash
GET /api/notifications?limit=50
Authorization: Bearer {accessToken}
```

#### WebSocket Connection
```javascript
const socket = new SockJS('http://localhost:8084/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    stompClient.subscribe('/user/queue/notifications', function(notification) {
        console.log('New notification:', JSON.parse(notification.body));
    });
});
```

### Reporting Service (Port 8085)

#### Lấy thống kê Workspace
```bash
GET /api/reports/workspaces/{workspaceId}/statistics
Authorization: Bearer {accessToken}
```

#### Lấy hiệu suất User
```bash
GET /api/reports/users/{userId}/performance
Authorization: Bearer {accessToken}
```

## 🔒 Security Features

### API Gateway
- **Rate Limiting**: Giới hạn request theo user/IP
- **JWT Authentication**: Xác thực tất cả protected endpoints
- **Access Token & Refresh Token**: Token rotation strategy
- **CORS Protection**: Cấu hình cho frontend

### Auth Service
- **BCrypt Password Hashing**: 10 rounds
- **JWT với HS256**: Signing algorithm
- **Token Expiration**:
  - Access Token: 1 hour
  - Refresh Token: 7 days
- **Google OAuth 2.0**: Social login

## 📊 Database Schema

### Auth Service DB (synergyflow_auth)
```sql
users (
  user_id UUID PRIMARY KEY,
  email VARCHAR UNIQUE NOT NULL,
  password_hash VARCHAR,
  google_id VARCHAR UNIQUE,
  full_name VARCHAR,
  avatar_url VARCHAR,
  refresh_token VARCHAR,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)
```

### Workspace Service DB (synergyflow_workspace)
```sql
workspaces (
  workspace_id UUID PRIMARY KEY,
  name VARCHAR NOT NULL,
  owner_id UUID NOT NULL,
  description TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

channels (
  channel_id UUID PRIMARY KEY,
  name VARCHAR NOT NULL,
  workspace_id UUID REFERENCES workspaces,
  description TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

workspace_members (
  id UUID PRIMARY KEY,
  workspace_id UUID REFERENCES workspaces,
  user_id UUID NOT NULL,
  role VARCHAR NOT NULL,
  joined_at TIMESTAMP,
  UNIQUE(workspace_id, user_id)
)
```

### Task Service DB (synergyflow_task)
```sql
task_boards (
  board_id UUID PRIMARY KEY,
  channel_id UUID UNIQUE NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

task_columns (
  column_id UUID PRIMARY KEY,
  title VARCHAR NOT NULL,
  board_id UUID REFERENCES task_boards,
  column_order INTEGER NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

tasks (
  task_id UUID PRIMARY KEY,
  title VARCHAR NOT NULL,
  description TEXT,
  due_date TIMESTAMP,
  priority VARCHAR NOT NULL,
  status VARCHAR NOT NULL,
  column_id UUID REFERENCES task_columns,
  creator_id UUID NOT NULL,
  task_order INTEGER NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

task_assignments (
  id UUID PRIMARY KEY,
  task_id UUID REFERENCES tasks,
  user_id UUID NOT NULL,
  assigned_at TIMESTAMP,
  UNIQUE(task_id, user_id)
)
```

## 🏭 Best Practices Implemented

### 1. **DOMA (Domain-Oriented Microservice Architecture)**
- Mỗi service đại diện cho một domain nghiệp vụ
- Autonomous và loosely coupled
- Có database riêng

### 2. **DTO & Entity Mapping**
- Sử dụng **MapStruct** cho mapping
- Tách biệt Entity và DTO
- Validation với Jakarta Bean Validation

### 3. **Repository Pattern**
- Spring Data JPA repositories
- Custom queries với @Query
- Transaction management

### 4. **Service Layer**
- Business logic trong Service
- @Transactional cho data consistency
- Exception handling

### 5. **Controller Best Practices**
- RESTful API design
- Standard HTTP status codes
- Consistent response format (ApiResponse)

### 6. **Security**
- JWT với Access & Refresh tokens
- Rate limiting tại API Gateway
- Role-based access control

### 7. **Async Communication**
- RabbitMQ cho events
- WebSocket cho real-time notifications

## 📁 Project Structure

```
synergyflow-backend/
├── api-gateway/                 # API Gateway với rate limiting
│   ├── src/main/java/com/synergyflow/gateway/
│   │   ├── filter/             # JWT authentication filter
│   │   └── config/             # Rate limit config
│   └── Dockerfile
│
├── service-discovery/           # Netflix Eureka Server
│   └── Dockerfile
│
├── auth-service/                # Authentication & User Management
│   ├── src/main/java/com/synergyflow/auth/
│   │   ├── controller/         # AuthController
│   │   ├── service/            # AuthService
│   │   ├── repository/         # UserRepository
│   │   ├── entity/             # User entity
│   │   ├── dto/                # Request/Response DTOs
│   │   ├── mapper/             # MapStruct mappers
│   │   └── config/             # Security config
│   └── Dockerfile
│
├── workspace-service/           # Workspace & Channel Management
│   ├── src/main/java/com/synergyflow/workspace/
│   │   ├── controller/         # WorkspaceController
│   │   ├── service/            # WorkspaceService
│   │   ├── repository/         # Repositories
│   │   ├── entity/             # Workspace, Channel, Member
│   │   ├── dto/                # DTOs
│   │   └── mapper/             # Mappers
│   └── Dockerfile
│
├── task-service/                # Task & Kanban Board Management
│   ├── src/main/java/com/synergyflow/task/
│   │   ├── controller/         # TaskController
│   │   ├── service/            # TaskService (với drag-drop logic)
│   │   ├── repository/         # Repositories
│   │   ├── entity/             # TaskBoard, Column, Task, Assignment
│   │   ├── dto/                # DTOs
│   │   ├── mapper/             # Mappers
│   │   └── config/             # RabbitMQ config
│   └── Dockerfile
│
├── notification-service/        # Real-time Notifications
│   ├── src/main/java/com/synergyflow/notification/
│   │   ├── controller/         # NotificationController
│   │   ├── service/            # NotificationService
│   │   ├── dto/                # DTOs
│   │   └── config/             # WebSocket config
│   └── Dockerfile
│
├── reporting-service/           # Analytics & Reporting
│   ├── src/main/java/com/synergyflow/reporting/
│   │   ├── controller/         # ReportingController
│   │   ├── service/            # ReportingService
│   │   └── dto/                # Statistics DTOs
│   └── Dockerfile
│
├── common-lib/                  # Shared Library
│   └── src/main/java/com/synergyflow/common/
│       ├── dto/                # ApiResponse, ErrorResponse
│       ├── exception/          # Custom exceptions
│       ├── security/           # JwtTokenProvider
│       └── util/               # Utilities
│
├── docker-compose.yml           # Docker orchestration
├── build.gradle                 # Root Gradle config
├── settings.gradle              # Multi-project setup
└── README.md
```

## 🌐 Environment Variables

```bash
# JWT Configuration
JWT_SECRET=your-secret-key-min-256-bits
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=postgres

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# RabbitMQ Configuration
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific service
./gradlew :auth-service:test
./gradlew :workspace-service:test
./gradlew :task-service:test
```

## 📦 Build & Deploy

```bash
# Build all services
./gradlew build

# Build specific service
./gradlew :auth-service:build

# Build Docker images
docker-compose build

# Deploy to production
docker-compose -f docker-compose.prod.yml up -d
```

## 🎯 Service URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Auth Service**: http://localhost:8081
- **Workspace Service**: http://localhost:8082
- **Task Service**: http://localhost:8083
- **Notification Service**: http://localhost:8084
- **Reporting Service**: http://localhost:8085

## 👥 Contributors

- Le Quoc Khanh - [@lequockhanh19521680](https://github.com/lequockhanh19521680)

## 📝 License

This project is licensed under the MIT License.

---

**Made with ❤️ for SynergyFlow Team**
