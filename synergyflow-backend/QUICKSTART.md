# SynergyFlow - Quick Start Guide

## 🚀 5-Minute Setup

### Option 1: Docker Compose (Recommended)

```bash
# 1. Clone repository
git clone https://github.com/lequockhanh19521680/Location-Analysis-Web.git
cd Location-Analysis-Web/synergyflow-backend

# 2. Start all services
docker-compose up -d

# 3. Wait for services to start (~2 minutes)
docker-compose logs -f

# 4. Verify services are running
curl http://localhost:8761  # Eureka Dashboard
```

**That's it!** All services are now running.

### Option 2: Local Development

```bash
# Prerequisites
java -version  # Should be 17+
docker --version  # For infrastructure only

# 1. Start infrastructure
docker-compose up postgres-auth postgres-workspace postgres-task redis rabbitmq -d

# 2. Build project
./gradlew build -x test

# 3. Start services (in separate terminals)
./gradlew :service-discovery:bootRun  # Terminal 1
./gradlew :api-gateway:bootRun        # Terminal 2
./gradlew :auth-service:bootRun       # Terminal 3
./gradlew :workspace-service:bootRun  # Terminal 4
./gradlew :task-service:bootRun       # Terminal 5
```

## 📋 Testing the APIs

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "test@example.com",
      "fullName": "Test User"
    }
  }
}
```

**Save the `accessToken` for next requests!**

### 2. Create a Workspace

```bash
# Replace {TOKEN} with your accessToken
curl -X POST http://localhost:8080/api/workspaces \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "name": "My First Workspace",
    "description": "Testing workspace"
  }'
```

### 3. Create a Channel

```bash
# Replace {TOKEN} and {WORKSPACE_ID}
curl -X POST http://localhost:8080/api/workspaces/{WORKSPACE_ID}/channels \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "name": "General",
    "description": "General discussion"
  }'
```

### 4. Create Task Board

```bash
# Replace {TOKEN} and {CHANNEL_ID}
curl -X GET http://localhost:8080/api/tasks/boards/channel/{CHANNEL_ID} \
  -H "Authorization: Bearer {TOKEN}"
```

### 5. Create Task Column

```bash
curl -X POST http://localhost:8080/api/tasks/boards/channel/{CHANNEL_ID}/columns \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "title": "To Do"
  }'
```

### 6. Create a Task

```bash
# Replace {TOKEN} and {COLUMN_ID}
curl -X POST http://localhost:8080/api/tasks/columns/{COLUMN_ID} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "title": "My First Task",
    "description": "Testing task creation",
    "priority": "HIGH",
    "dueDate": "2025-10-15T10:00:00"
  }'
```

## 🎯 Service URLs

Once running, access:

- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **API Gateway**: http://localhost:8080
- **PostgreSQL**: localhost:5433 (auth), 5434 (workspace), 5435 (task)
- **Redis**: localhost:6379

## 📊 Monitoring

### Check Service Health

```bash
# Eureka
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f auth-service
docker-compose logs -f task-service
```

### Database Access

```bash
# Connect to Auth DB
docker exec -it synergyflow-postgres-auth-1 psql -U postgres -d synergyflow_auth

# List tables
\dt

# View users
SELECT * FROM users;
```

## 🧹 Cleanup

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose down -v

# Clean build artifacts
./gradlew clean
```

## 🐛 Troubleshooting

### Services won't start

```bash
# Check if ports are in use
lsof -i :8080  # API Gateway
lsof -i :8761  # Eureka
lsof -i :5432  # PostgreSQL

# Kill processes using the ports
kill -9 <PID>
```

### Database connection errors

```bash
# Ensure PostgreSQL containers are running
docker ps | grep postgres

# Restart specific database
docker-compose restart postgres-auth
```

### Build errors

```bash
# Clean and rebuild
./gradlew clean build -x test

# Check Java version
java -version  # Should be 17 or higher
```

### Out of memory

```bash
# Increase Docker memory in Docker Desktop settings
# Minimum: 4GB RAM

# Or set Java heap size
export JAVA_OPTS="-Xmx512m"
```

## 📱 Postman Collection

### Import to Postman

1. Create new Collection: "SynergyFlow API"
2. Add Environment variables:
   - `baseUrl`: `http://localhost:8080`
   - `accessToken`: (will be set after login)
   - `workspaceId`: (will be set after creating workspace)
   - `channelId`: (will be set after creating channel)
   - `columnId`: (will be set after creating column)

3. Add Authorization to Collection:
   - Type: Bearer Token
   - Token: `{{accessToken}}`

### Sample Request Sequence

1. **POST** Register: `{{baseUrl}}/api/auth/register`
2. **POST** Login: `{{baseUrl}}/api/auth/login`
3. **POST** Create Workspace: `{{baseUrl}}/api/workspaces`
4. **POST** Create Channel: `{{baseUrl}}/api/workspaces/{{workspaceId}}/channels`
5. **GET** Get Board: `{{baseUrl}}/api/tasks/boards/channel/{{channelId}}`
6. **POST** Create Column: `{{baseUrl}}/api/tasks/boards/channel/{{channelId}}/columns`
7. **POST** Create Task: `{{baseUrl}}/api/tasks/columns/{{columnId}}`

## 🔧 Development Tips

### Hot Reload

```bash
# Use Spring DevTools for auto-restart
# Already configured in build.gradle

# Or use gradlew with continuous build
./gradlew :auth-service:bootRun --continuous
```

### Debug Mode

```bash
# IntelliJ IDEA
# Right-click on Application.java → Debug

# Command line
./gradlew :auth-service:bootRun --debug-jvm
```

### Database GUI

**DBeaver** (Free):
1. Download: https://dbeaver.io/
2. New Connection → PostgreSQL
3. Host: localhost
4. Port: 5433 (auth) / 5434 (workspace) / 5435 (task)
5. Database: synergyflow_auth / synergyflow_workspace / synergyflow_task
6. User: postgres
7. Password: postgres

**pgAdmin** (Alternative):
```bash
docker run -p 5050:80 \
  -e 'PGADMIN_DEFAULT_EMAIL=admin@admin.com' \
  -e 'PGADMIN_DEFAULT_PASSWORD=admin' \
  dpage/pgadmin4
```

## 📈 Performance Testing

### Load Testing with Apache Bench

```bash
# Install Apache Bench
sudo apt-get install apache2-utils

# Test login endpoint (100 requests, 10 concurrent)
ab -n 100 -c 10 -H "Content-Type: application/json" \
  -p login.json http://localhost:8080/api/auth/login

# login.json:
{"email":"test@example.com","password":"password123"}
```

### Monitor Resource Usage

```bash
# Docker stats
docker stats

# Specific service
docker stats synergyflow-auth-service-1
```

## 🎓 Next Steps

1. **Read Documentation**:
   - [README.md](README.md) - Overview
   - [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - API Reference
   - [ARCHITECTURE_DESIGN.md](ARCHITECTURE_DESIGN.md) - Architecture Details

2. **Explore Services**:
   - Try creating multiple workspaces
   - Add members to workspaces
   - Create tasks and move them between columns
   - Test WebSocket notifications

3. **Customize**:
   - Modify JWT expiration times
   - Add custom fields to entities
   - Implement additional endpoints
   - Add your own microservice

4. **Deploy**:
   - Set up CI/CD pipeline
   - Deploy to AWS/GCP/Azure
   - Configure production database
   - Set up monitoring (Prometheus/Grafana)

## 💡 Pro Tips

- Use `.env` file for environment variables in development
- Enable Spring DevTools for faster development
- Use Eureka dashboard to monitor service health
- Check RabbitMQ management for message queues
- Use Redis CLI to inspect cached data
- Enable SQL logging to debug queries

## 🆘 Getting Help

- Check logs: `docker-compose logs -f [service-name]`
- Review API Documentation: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- Check Eureka: http://localhost:8761
- Verify database: Connect with DBeaver/pgAdmin

---

**Happy Coding! 🚀**
