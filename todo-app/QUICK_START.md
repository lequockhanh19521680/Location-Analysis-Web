# Quick Start Guide

Get the TODO list application running in 5 minutes!

## Prerequisites

- **Docker** and **Docker Compose** installed
- **Git** for cloning
- 8GB RAM recommended
- Ports available: 3000, 5432, 8080, 8081, 8082

## 🚀 Quick Start (Docker)

### 1. Navigate to the project

```bash
cd todo-app
```

### 2. Start all services

```bash
docker-compose up -d
```

This command will:
- Pull required Docker images
- Build custom images for each service
- Create a PostgreSQL database
- Start all microservices
- Initialize the database schema

### 3. Wait for services to be ready

```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

Wait until you see:
- `todo-postgres | database system is ready to accept connections`
- `todo-auth-service | Started AuthServiceApplication`
- `todo-todo-service | Todo Service running on port 8082`
- `todo-api-gateway | API Gateway running on port 8080`
- `todo-frontend | ready started server on 0.0.0.0:3000`

### 4. Access the application

Open your browser and go to: **http://localhost:3000**

## 🎯 First Steps

### Register a New Account

1. Go to http://localhost:3000
2. Click "Get Started" button
3. Fill in:
   - Username: `john_doe`
   - Email: `john@example.com`
   - Password: `password123`
4. Click "Register"

### Create Your First Todo

1. After registration, you'll be redirected to the dashboard
2. In the "Add New Task" section:
   - Title: `Complete the project`
   - Description: `Finish all features`
3. Click "Add Task"

### Manage Todos

- **Change Status**: Click the circle icon to cycle through:
  - Pending → In Progress → Completed → Pending
- **Filter**: Use filter buttons to view specific status
- **Delete**: Click the trash icon to remove a task

## 🔍 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | Web interface |
| API Gateway | http://localhost:8080 | API entry point |
| Auth Service | http://localhost:8081 | Authentication |
| Todo Service | http://localhost:8082 | Todo operations |
| PostgreSQL | localhost:5432 | Database |

## 🧪 Testing the API

### Register (via API Gateway)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "test123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "test@example.com",
    "password": "test123"
  }'
```

Save the token from the response.

### Create Todo

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "title": "My first todo",
    "description": "This is a test",
    "priority": "high"
  }'
```

### Get All Todos

```bash
curl http://localhost:8080/api/todos \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🛑 Stopping the Application

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (deletes data)
docker-compose down -v
```

## 🔄 Restarting Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart auth-service
```

## 📊 View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f auth-service
docker-compose logs -f todo-service
docker-compose logs -f api-gateway
docker-compose logs -f frontend
```

## 🐛 Troubleshooting

### Issue: Port already in use

**Error:** `Bind for 0.0.0.0:3000 failed: port is already allocated`

**Solution:**
```bash
# Find process using the port
lsof -i :3000  # On Mac/Linux
netstat -ano | findstr :3000  # On Windows

# Kill the process or change port in docker-compose.yml
```

### Issue: Services not starting

**Solution:**
```bash
# Rebuild images
docker-compose build --no-cache

# Start again
docker-compose up -d
```

### Issue: Database connection failed

**Solution:**
```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Restart database
docker-compose restart postgres

# Check database logs
docker-compose logs postgres
```

### Issue: Cannot connect to API

**Check if API Gateway is running:**
```bash
curl http://localhost:8080/health
```

**Expected response:**
```json
{
  "status": "ok",
  "service": "api-gateway",
  "timestamp": "2024-01-01T12:00:00.000Z"
}
```

### Issue: Frontend shows network error

**Solution:**
1. Ensure API Gateway is running: `docker-compose ps api-gateway`
2. Check if services are healthy: `docker-compose ps`
3. Verify environment variables in `docker-compose.yml`

## 🗄️ Database Access

### Using psql

```bash
docker exec -it todo-postgres psql -U postgres -d todo_db
```

### Common SQL Commands

```sql
-- List all users
SELECT id, username, email, role FROM users;

-- List all todos
SELECT id, title, status, priority FROM todos;

-- Count todos by status
SELECT status, COUNT(*) FROM todos GROUP BY status;

-- Exit psql
\q
```

## 🔧 Development Mode

### Run services locally (without Docker)

#### 1. Start PostgreSQL

```bash
docker run -d \
  --name todo-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=todo_db \
  -p 5432:5432 \
  postgres:15-alpine
```

#### 2. Apply database schema

```bash
psql -h localhost -U postgres -d todo_db -f schema.sql
```

#### 3. Start Auth Service

```bash
cd auth-service-spring
mvn spring-boot:run
```

#### 4. Start Todo Service

```bash
cd todo-service-go
go run main.go
```

#### 5. Start API Gateway

```bash
cd api-gateway
npm install
npm run dev
```

#### 6. Start Frontend

```bash
cd todo-frontend
npm install
npm run dev
```

## 📝 Environment Variables

Create a `.env` file in the `todo-app` directory:

```bash
cp .env.example .env
```

Edit values as needed for your environment.

## 🎓 Next Steps

1. **Explore the code**: Check out the source code in each service
2. **Read documentation**: See `README.md` for detailed information
3. **View architecture**: Check `ARCHITECTURE.md` for system design
4. **Customize**: Modify services to add new features
5. **Deploy**: Use Kubernetes or cloud platforms for production

## 💡 Tips

- **Use Docker Desktop**: Easier to manage containers
- **Check logs frequently**: Helps debug issues
- **Restart services**: If something doesn't work, try restarting
- **Clean up**: Run `docker-compose down -v` to start fresh

## 🆘 Getting Help

If you encounter issues:

1. Check the logs: `docker-compose logs -f`
2. Verify all services are running: `docker-compose ps`
3. Ensure ports are not in use: `lsof -i :3000`
4. Rebuild images: `docker-compose build --no-cache`
5. Check Docker resources (CPU, Memory)

## ✅ Success Checklist

- [ ] All services show "Up" status
- [ ] Can access http://localhost:3000
- [ ] Can register a new account
- [ ] Can login successfully
- [ ] Can create a todo
- [ ] Can see todos in dashboard
- [ ] Can update todo status
- [ ] Can delete a todo

---

**Happy coding! 🚀**
