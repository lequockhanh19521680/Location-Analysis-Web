# SynergyFlow API Documentation

## Base URLs

When using API Gateway:
- **Production**: `http://localhost:8080`
- **Direct Service Access** (Development only):
  - Auth Service: `http://localhost:8081`
  - Workspace Service: `http://localhost:8082`
  - Task Service: `http://localhost:8083`
  - Notification Service: `http://localhost:8084`
  - Reporting Service: `http://localhost:8085`

## Authentication

All endpoints (except auth endpoints) require a JWT access token in the Authorization header:

```
Authorization: Bearer {accessToken}
```

The API Gateway automatically extracts user information from the token and adds it to request headers:
- `X-User-Id`: User's ID
- `X-User-Email`: User's email

## Standard Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "status": 400,
  "message": "Error message",
  "error": "Bad Request",
  "timestamp": "2025-10-11T10:00:00",
  "path": "/api/endpoint"
}
```

## Auth Service API

### 1. Register User

**Endpoint**: `POST /api/auth/register`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "fullName": "Nguyen Van A"
}
```

**Response**: `201 Created`
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "fullName": "Nguyen Van A",
      "avatarUrl": null,
      "createdAt": "2025-10-11T10:00:00"
    }
  }
}
```

### 2. Login

**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response**: `200 OK` (same as register response)

### 3. Google OAuth Login

**Endpoint**: `POST /api/auth/oauth/google`

**Request Body**:
```json
{
  "googleId": "110248495115914366573",
  "email": "user@gmail.com",
  "fullName": "Nguyen Van A",
  "avatarUrl": "https://lh3.googleusercontent.com/..."
}
```

**Response**: `200 OK` (same as register response)

### 4. Refresh Access Token

**Endpoint**: `POST /api/auth/refresh`

**Request Body**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response**: `200 OK`
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "new-access-token",
    "refreshToken": "new-refresh-token",
    "user": { ... }
  }
}
```

### 5. Get User Profile

**Endpoint**: `GET /api/auth/users/{userId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "avatarUrl": "https://...",
    "createdAt": "2025-10-11T10:00:00"
  }
}
```

### 6. Update User Profile

**Endpoint**: `PUT /api/auth/users/{userId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "fullName": "Nguyen Van B",
  "avatarUrl": "https://new-avatar.com/image.jpg"
}
```

**Response**: `200 OK`

---

## Workspace Service API

### 1. Create Workspace

**Endpoint**: `POST /api/workspaces`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "name": "My Project Workspace",
  "description": "Workspace for project management"
}
```

**Response**: `201 Created`
```json
{
  "success": true,
  "message": "Workspace created successfully",
  "data": {
    "workspaceId": "workspace-uuid",
    "name": "My Project Workspace",
    "description": "Workspace for project management",
    "ownerId": "user-id",
    "createdAt": "2025-10-11T10:00:00",
    "updatedAt": "2025-10-11T10:00:00",
    "channels": [],
    "members": [
      {
        "id": "member-uuid",
        "workspaceId": "workspace-uuid",
        "userId": "user-id",
        "role": "OWNER",
        "joinedAt": "2025-10-11T10:00:00"
      }
    ]
  }
}
```

### 2. Get User's Workspaces

**Endpoint**: `GET /api/workspaces`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "workspaceId": "workspace-uuid",
      "name": "My Project Workspace",
      ...
    }
  ]
}
```

### 3. Get Workspace Details

**Endpoint**: `GET /api/workspaces/{workspaceId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 4. Update Workspace

**Endpoint**: `PUT /api/workspaces/{workspaceId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "name": "Updated Workspace Name",
  "description": "Updated description"
}
```

**Response**: `200 OK`

### 5. Delete Workspace

**Endpoint**: `DELETE /api/workspaces/{workspaceId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 6. Create Channel

**Endpoint**: `POST /api/workspaces/{workspaceId}/channels`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "name": "General",
  "description": "General discussion channel"
}
```

**Response**: `201 Created`
```json
{
  "success": true,
  "message": "Channel created successfully",
  "data": {
    "channelId": "channel-uuid",
    "name": "General",
    "description": "General discussion channel",
    "workspaceId": "workspace-uuid",
    "createdAt": "2025-10-11T10:00:00",
    "updatedAt": "2025-10-11T10:00:00"
  }
}
```

### 7. Get Workspace Channels

**Endpoint**: `GET /api/workspaces/{workspaceId}/channels`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 8. Delete Channel

**Endpoint**: `DELETE /api/workspaces/channels/{channelId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 9. Add Member to Workspace

**Endpoint**: `POST /api/workspaces/{workspaceId}/members`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "userId": "user-id-to-add",
  "role": "MEMBER"
}
```

**Roles**: `OWNER`, `ADMIN`, `MEMBER`

**Response**: `201 Created`

### 10. Get Workspace Members

**Endpoint**: `GET /api/workspaces/{workspaceId}/members`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 11. Remove Member

**Endpoint**: `DELETE /api/workspaces/{workspaceId}/members/{memberId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

---

## Task Service API

### 1. Get or Create Task Board for Channel

**Endpoint**: `GET /api/tasks/boards/channel/{channelId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`
```json
{
  "success": true,
  "data": {
    "boardId": "board-uuid",
    "channelId": "channel-uuid",
    "columns": [],
    "createdAt": "2025-10-11T10:00:00",
    "updatedAt": "2025-10-11T10:00:00"
  }
}
```

### 2. Create Column in Board

**Endpoint**: `POST /api/tasks/boards/channel/{channelId}/columns`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "title": "To Do"
}
```

**Response**: `201 Created`
```json
{
  "success": true,
  "message": "Column created successfully",
  "data": {
    "columnId": "column-uuid",
    "title": "To Do",
    "boardId": "board-uuid",
    "order": 0,
    "tasks": [],
    "createdAt": "2025-10-11T10:00:00",
    "updatedAt": "2025-10-11T10:00:00"
  }
}
```

### 3. Get Board Columns

**Endpoint**: `GET /api/tasks/boards/channel/{channelId}/columns`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 4. Delete Column

**Endpoint**: `DELETE /api/tasks/columns/{columnId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 5. Create Task

**Endpoint**: `POST /api/tasks/columns/{columnId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "title": "Implement login feature",
  "description": "Create login page with form validation and error handling",
  "dueDate": "2025-10-15T10:00:00",
  "priority": "HIGH",
  "assignedUserIds": ["user-id-1", "user-id-2"]
}
```

**Priority**: `LOW`, `MEDIUM`, `HIGH`, `URGENT`

**Response**: `201 Created`
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "taskId": "task-uuid",
    "title": "Implement login feature",
    "description": "Create login page with form validation and error handling",
    "dueDate": "2025-10-15T10:00:00",
    "priority": "HIGH",
    "status": "TODO",
    "columnId": "column-uuid",
    "creatorId": "user-id",
    "order": 0,
    "assignedUserIds": ["user-id-1", "user-id-2"],
    "createdAt": "2025-10-11T10:00:00",
    "updatedAt": "2025-10-11T10:00:00"
  }
}
```

### 6. Get Column Tasks

**Endpoint**: `GET /api/tasks/columns/{columnId}/tasks`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 7. Get Task Details

**Endpoint**: `GET /api/tasks/{taskId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 8. Update Task

**Endpoint**: `PUT /api/tasks/{taskId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "dueDate": "2025-10-20T10:00:00",
  "priority": "URGENT",
  "status": "IN_PROGRESS",
  "assignedUserIds": ["user-id-1", "user-id-2", "user-id-3"]
}
```

**Status**: `TODO`, `IN_PROGRESS`, `DONE`, `BLOCKED`

**Response**: `200 OK`

### 9. Move Task (Drag & Drop)

**Endpoint**: `POST /api/tasks/{taskId}/move`

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "targetColumnId": "target-column-uuid",
  "newOrder": 2
}
```

**Response**: `200 OK`
```json
{
  "success": true,
  "message": "Task moved successfully",
  "data": {
    "taskId": "task-uuid",
    "columnId": "target-column-uuid",
    "order": 2,
    ...
  }
}
```

### 10. Delete Task

**Endpoint**: `DELETE /api/tasks/{taskId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 11. Get Tasks by Date Range (Calendar View)

**Endpoint**: `GET /api/tasks/calendar?start={startDate}&end={endDate}`

**Headers**: `Authorization: Bearer {accessToken}`

**Query Parameters**:
- `start`: ISO 8601 datetime (e.g., `2025-10-01T00:00:00`)
- `end`: ISO 8601 datetime (e.g., `2025-10-31T23:59:59`)

**Response**: `200 OK`

### 12. Get Tasks by Assignee

**Endpoint**: `GET /api/tasks/assigned/{userId}`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

---

## Notification Service API

### 1. Get User Notifications

**Endpoint**: `GET /api/notifications?limit={limit}`

**Headers**: `Authorization: Bearer {accessToken}`

**Query Parameters**:
- `limit`: Number of notifications to retrieve (default: 50)

**Response**: `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "id": "notification-uuid",
      "userId": "user-id",
      "message": "You have been assigned to a new task",
      "type": "TASK_ASSIGNED",
      "read": false,
      "createdAt": "2025-10-11T10:00:00"
    }
  ]
}
```

### 2. Mark Notification as Read

**Endpoint**: `PUT /api/notifications/{notificationId}/read`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`

### 3. WebSocket Connection for Real-time Notifications

**WebSocket Endpoint**: `ws://localhost:8084/ws`

**Connection Example (JavaScript)**:
```javascript
const socket = new SockJS('http://localhost:8084/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({
  Authorization: 'Bearer ' + accessToken
}, function(frame) {
  console.log('Connected: ' + frame);
  
  // Subscribe to user's notification queue
  stompClient.subscribe('/user/queue/notifications', function(notification) {
    const data = JSON.parse(notification.body);
    console.log('New notification:', data);
    // Handle notification in UI
  });
});
```

**Notification Types**:
- `TASK_ASSIGNED`: When user is assigned to a task
- `TASK_UPDATED`: When a task user is assigned to is updated
- `TASK_DUE_SOON`: When a task deadline is approaching

---

## Reporting Service API

### 1. Get Workspace Statistics

**Endpoint**: `GET /api/reports/workspaces/{workspaceId}/statistics`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`
```json
{
  "success": true,
  "data": {
    "workspaceId": "workspace-uuid",
    "totalTasks": 47,
    "completedTasks": 22,
    "inProgressTasks": 8,
    "todoTasks": 15,
    "overdueTasksCount": 3,
    "completionPercentage": 46.81,
    "tasksByStatus": {
      "TODO": 15,
      "IN_PROGRESS": 8,
      "DONE": 22,
      "BLOCKED": 2
    },
    "tasksByPriority": {
      "LOW": 10,
      "MEDIUM": 20,
      "HIGH": 12,
      "URGENT": 5
    },
    "tasksByAssignee": {
      "user1": 15,
      "user2": 12,
      "user3": 20
    }
  }
}
```

### 2. Get User Performance

**Endpoint**: `GET /api/reports/users/{userId}/performance`

**Headers**: `Authorization: Bearer {accessToken}`

**Response**: `200 OK`
```json
{
  "success": true,
  "data": {
    "userId": "user-id",
    "totalAssignedTasks": 25,
    "completedTasks": 20,
    "onTimeTasks": 18,
    "lateTasks": 2,
    "averageCompletionTime": 4.5,
    "performanceScore": 90.0
  }
}
```

---

## Rate Limiting

API Gateway implements rate limiting per user/IP:

- **Auth endpoints**: 10 requests/second, burst 20
- **Workspace endpoints**: 50 requests/second, burst 100
- **Task endpoints**: 50 requests/second, burst 100
- **Notification endpoints**: 100 requests/second, burst 200
- **Reporting endpoints**: 30 requests/second, burst 60

When rate limit is exceeded, you'll receive:
```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later."
}
```

---

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 400 | Bad Request - Invalid request data |
| 401 | Unauthorized - Missing or invalid authentication |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 429 | Too Many Requests - Rate limit exceeded |
| 500 | Internal Server Error - Server error |

---

## Postman Collection

You can import this API into Postman using the following collection structure:

1. Create environment variables:
   - `baseUrl`: `http://localhost:8080`
   - `accessToken`: Your JWT access token
   - `refreshToken`: Your JWT refresh token

2. Set Authorization header for all requests (except auth):
   - Type: Bearer Token
   - Token: `{{accessToken}}`

---

**Made with ❤️ for SynergyFlow**
