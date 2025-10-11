# SynergyFlow - Architecture & Design Patterns

## 📐 Architectural Overview

SynergyFlow implements a **Domain-Oriented Microservice Architecture (DOMA)** pattern, where each microservice represents a distinct business domain with its own database, ensuring loose coupling and high cohesion.

## 🏛️ Key Architectural Patterns

### 1. Microservices Architecture

Each service is:
- **Autonomous**: Can be developed, deployed, and scaled independently
- **Domain-focused**: Represents a single business capability
- **Database-per-service**: Each service owns its data
- **API-driven**: Communication via REST APIs

```
┌─────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                    │
│           Rate Limiting + JWT Authentication             │
└─────────────────────┬───────────────────────────────────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
        ▼             ▼             ▼
   ┌────────┐   ┌──────────┐   ┌────────┐
   │  Auth  │   │Workspace │   │  Task  │
   │Service │   │ Service  │   │Service │
   └────┬───┘   └────┬─────┘   └───┬────┘
        │            │              │
        ▼            ▼              ▼
   ┌────────┐   ┌──────────┐   ┌────────┐
   │Postgres│   │Postgres  │   │Postgres│
   │  Auth  │   │Workspace │   │  Task  │
   └────────┘   └──────────┘   └────────┘
```

### 2. Service Discovery Pattern

**Netflix Eureka** for dynamic service registration and discovery:
- Services register themselves on startup
- API Gateway discovers services dynamically
- Load balancing across service instances
- Health checking and failover

### 3. API Gateway Pattern

**Spring Cloud Gateway** as single entry point:
- Centralized authentication
- Rate limiting per user/IP
- Request routing to microservices
- CORS handling
- Response aggregation

### 4. Database Per Service Pattern

Each microservice has its own database:
- **Auth DB**: User authentication data
- **Workspace DB**: Workspaces, channels, members
- **Task DB**: Tasks, boards, columns, assignments

Benefits:
- Data isolation and independence
- Technology flexibility
- Easier scaling
- Reduced coupling

### 5. Event-Driven Architecture

**RabbitMQ** for asynchronous communication:
- Task Service publishes events when tasks are created/assigned
- Notification Service consumes events and sends notifications
- Decoupled services
- Improved scalability

```
Task Service → RabbitMQ Queue → Notification Service
   (Publisher)                      (Consumer)
```

### 6. CQRS Lite Pattern

Separation of read and write operations:
- **Reporting Service**: Read-only analytics (queries)
- **Task Service**: Write operations (commands)
- Optimized for different access patterns

## 🎨 Design Patterns

### 1. Repository Pattern

Data access abstraction using Spring Data JPA:

```java
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
    boolean existsByEmail(String email);
}
```

Benefits:
- Abstraction over data access
- Testability
- Consistent interface
- Query method generation

### 2. Service Layer Pattern

Business logic encapsulation:

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Business logic here
    }
}
```

### 3. DTO Pattern

Data Transfer Objects for API contracts:

```java
// Request DTO
@Data
@Builder
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
}

// Response DTO
@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserDto user;
}
```

Benefits:
- API contract stability
- Validation
- Version control
- Security (hide internal fields)

### 4. Mapper Pattern

Entity-DTO conversion using MapStruct:

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
```

Benefits:
- Type-safe conversion
- Compile-time validation
- Performance (no reflection)
- Maintainability

### 5. Builder Pattern

Fluent object construction with Lombok:

```java
User user = User.builder()
    .email("user@example.com")
    .fullName("John Doe")
    .build();
```

### 6. Strategy Pattern

JWT token types (Access vs Refresh):

```java
public String generateAccessToken(String userId, String email) {
    return generateToken(userId, email, accessTokenExpiration, "ACCESS");
}

public String generateRefreshToken(String userId, String email) {
    return generateToken(userId, email, refreshTokenExpiration, "REFRESH");
}
```

### 7. Observer Pattern

WebSocket notifications:
- Services publish events
- Clients subscribe to notification topics
- Real-time updates without polling

### 8. Facade Pattern

API Gateway acts as facade:
- Simplifies complex microservices interaction
- Single entry point
- Unified API for clients

## 🔐 Security Architecture

### 1. JWT Token Strategy

**Two-token approach**:
- **Access Token**: Short-lived (1 hour), for API access
- **Refresh Token**: Long-lived (7 days), for token renewal

Flow:
```
1. Login → Access Token + Refresh Token
2. API Request → Use Access Token
3. Token Expired → Use Refresh Token to get new Access Token
4. Refresh Token Expired → Login again
```

### 2. Password Security

- **BCrypt hashing**: 10 rounds
- **Salt**: Automatically generated per password
- **No plain text storage**

### 3. API Gateway Security

```yaml
routes:
  - id: auth-service-login
    # No authentication required
  
  - id: workspace-service
    filters:
      - JwtAuthenticationFilter  # Authentication required
      - RequestRateLimiter       # Rate limiting
```

### 4. Role-Based Access Control (RBAC)

Workspace member roles:
- **OWNER**: Full control
- **ADMIN**: Manage members and content
- **MEMBER**: View and create content

```java
private void checkAdminAccess(String workspaceId, String userId) {
    WorkspaceMember member = memberRepository
        .findByWorkspace_WorkspaceIdAndUserId(workspaceId, userId)
        .orElseThrow(() -> new UnauthorizedException(...));
    
    if (member.getRole() != OWNER && member.getRole() != ADMIN) {
        throw new UnauthorizedException(...);
    }
}
```

## 📊 Data Flow Examples

### 1. User Registration Flow

```
Frontend → API Gateway → Auth Service
                              ↓
                         Hash Password
                              ↓
                         Save to DB
                              ↓
                    Generate JWT Tokens
                              ↓
Frontend ← API Gateway ← Return Tokens
```

### 2. Create Task with Notification Flow

```
Frontend → API Gateway → Task Service
                              ↓
                         Save Task to DB
                              ↓
                    Publish Event to RabbitMQ
                              ↓
                    Notification Service (Consumer)
                              ↓
                    Store in Redis + Send via WebSocket
                              ↓
Frontend ← WebSocket ← Real-time Notification
```

### 3. Task Drag & Drop Flow

```
Frontend → API Gateway → Task Service
                              ↓
                    1. Remove from source column
                    2. Reorder source tasks
                    3. Add to target column at position
                    4. Reorder target tasks
                    5. Update DB
                              ↓
Frontend ← API Gateway ← Updated Task
```

## 🎯 Best Practices Implemented

### 1. Clean Code
- **Single Responsibility**: Each class has one reason to change
- **DRY**: Don't Repeat Yourself
- **KISS**: Keep It Simple, Stupid
- **Meaningful names**: Clear, descriptive variable/method names

### 2. SOLID Principles

**S**ingle Responsibility
```java
// Service handles business logic
public class AuthService { }

// Repository handles data access
public interface UserRepository { }

// Controller handles HTTP
@RestController
public class AuthController { }
```

**O**pen/Closed
```java
// Can extend without modifying
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
```

**L**iskov Substitution
```java
// All repositories can substitute JpaRepository
public interface UserRepository extends JpaRepository<User, String> { }
```

**I**nterface Segregation
```java
// Small, focused interfaces
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByColumn_ColumnIdOrderByOrderAsc(String columnId);
}
```

**D**ependency Inversion
```java
// Depend on abstractions (interfaces), not concrete classes
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;  // Interface
    private final JwtTokenProvider jwtTokenProvider;
}
```

### 3. Exception Handling

Centralized with `@RestControllerAdvice`:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(...) {
        // Handle and return appropriate response
    }
}
```

### 4. Validation

Jakarta Bean Validation:
```java
@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

### 5. Transaction Management

```java
@Transactional
public AuthResponse register(RegisterRequest request) {
    // All DB operations in one transaction
    // Auto rollback on exception
}
```

### 6. Logging & Monitoring

- Spring Boot Actuator endpoints
- Eureka dashboard for service health
- RabbitMQ management console
- Application logs with SLF4J

### 7. Configuration Management

Environment-based configuration:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
```

## 📈 Scalability Considerations

### 1. Horizontal Scaling

Each service can be scaled independently:
```yaml
docker-compose up --scale task-service=3
```

### 2. Database Scaling

- Read replicas for read-heavy services
- Connection pooling
- Proper indexing

### 3. Caching Strategy

Redis for:
- Session storage
- Notification temporary storage
- Frequently accessed data

### 4. Async Processing

RabbitMQ for:
- Non-blocking operations
- Peak load handling
- Retry mechanisms

## 🧪 Testing Strategy

### 1. Unit Tests
- Service layer testing
- Repository testing
- Mapper testing

### 2. Integration Tests
- API endpoint testing
- Database integration
- Message queue integration

### 3. Contract Tests
- DTO validation
- API contract verification

## 🚀 Deployment Architecture

```
┌─────────────────────────────────────────┐
│         Load Balancer / Nginx           │
└──────────────┬──────────────────────────┘
               │
     ┌─────────┴─────────┐
     │   API Gateway     │
     │   (Multiple)      │
     └─────────┬─────────┘
               │
    ┌──────────┴──────────┐
    │   Service Mesh      │
    │   Eureka Cluster    │
    └──────────┬──────────┘
               │
    ┌──────────┴──────────┐
    │  Microservices      │
    │  (Auto-scaled)      │
    └──────────┬──────────┘
               │
    ┌──────────┴──────────┐
    │  Database Cluster   │
    │  Redis Cluster      │
    │  RabbitMQ Cluster   │
    └─────────────────────┘
```

## 📚 Technology Decisions

| Component | Technology | Reason |
|-----------|-----------|---------|
| Framework | Spring Boot | Enterprise-ready, extensive ecosystem |
| Build Tool | Gradle | Flexible, fast, Kotlin DSL |
| Database | PostgreSQL | ACID, reliability, JSON support |
| Cache | Redis | Performance, pub/sub, data structures |
| Message Queue | RabbitMQ | Reliable, feature-rich, easy setup |
| Service Discovery | Eureka | Spring Cloud native, proven |
| API Gateway | Spring Cloud Gateway | Reactive, Spring native |
| Mapping | MapStruct | Performance, type-safety |
| Security | JWT | Stateless, scalable |
| Container | Docker | Portability, consistency |

## 🎓 Learning Resources

For developers working on this project:

1. **Spring Boot**: https://spring.io/projects/spring-boot
2. **Microservices Patterns**: https://microservices.io/patterns/
3. **DOMA**: Domain-Oriented Microservice Architecture
4. **Spring Cloud**: https://spring.io/projects/spring-cloud
5. **MapStruct**: https://mapstruct.org/
6. **JWT Best Practices**: https://tools.ietf.org/html/rfc8725

---

**Built with ❤️ following industry best practices**
