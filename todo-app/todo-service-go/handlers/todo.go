package handlers

import (
	"database/sql"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/lequockhanh19521680/todo-service/database"
	"github.com/lequockhanh19521680/todo-service/models"
)

func GetTodos(c *gin.Context) {
	userID := c.GetInt64("userId")
	status := c.Query("status")
	priority := c.Query("priority")

	query := `SELECT id, user_id, title, description, status, priority, due_date, created_at, updated_at 
			  FROM todos WHERE user_id = $1`
	args := []interface{}{userID}
	argCount := 1

	if status != "" {
		argCount++
		query += " AND status = $" + strconv.Itoa(argCount)
		args = append(args, status)
	}

	if priority != "" {
		argCount++
		query += " AND priority = $" + strconv.Itoa(argCount)
		args = append(args, priority)
	}

	query += " ORDER BY created_at DESC"

	rows, err := database.DB.Query(query, args...)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch todos"})
		return
	}
	defer rows.Close()

	var todos []models.Todo
	for rows.Next() {
		var todo models.Todo
		err := rows.Scan(&todo.ID, &todo.UserID, &todo.Title, &todo.Description,
			&todo.Status, &todo.Priority, &todo.DueDate, &todo.CreatedAt, &todo.UpdatedAt)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to scan todo"})
			return
		}
		todos = append(todos, todo)
	}

	c.JSON(http.StatusOK, todos)
}

func GetTodoByID(c *gin.Context) {
	userID := c.GetInt64("userId")
	todoID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid todo ID"})
		return
	}

	var todo models.Todo
	query := `SELECT id, user_id, title, description, status, priority, due_date, created_at, updated_at 
			  FROM todos WHERE id = $1 AND user_id = $2`

	err = database.DB.QueryRow(query, todoID, userID).Scan(
		&todo.ID, &todo.UserID, &todo.Title, &todo.Description,
		&todo.Status, &todo.Priority, &todo.DueDate, &todo.CreatedAt, &todo.UpdatedAt)

	if err == sql.ErrNoRows {
		c.JSON(http.StatusNotFound, gin.H{"error": "Todo not found"})
		return
	} else if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch todo"})
		return
	}

	c.JSON(http.StatusOK, todo)
}

func CreateTodo(c *gin.Context) {
	userID := c.GetInt64("userId")
	var req models.CreateTodoRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if req.Status == "" {
		req.Status = "pending"
	}
	if req.Priority == "" {
		req.Priority = "medium"
	}

	var todo models.Todo
	query := `INSERT INTO todos (user_id, title, description, status, priority, due_date, created_at, updated_at)
			  VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
			  RETURNING id, user_id, title, description, status, priority, due_date, created_at, updated_at`

	now := time.Now()
	err := database.DB.QueryRow(query, userID, req.Title, req.Description, req.Status,
		req.Priority, req.DueDate, now, now).Scan(
		&todo.ID, &todo.UserID, &todo.Title, &todo.Description,
		&todo.Status, &todo.Priority, &todo.DueDate, &todo.CreatedAt, &todo.UpdatedAt)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create todo"})
		return
	}

	c.JSON(http.StatusCreated, todo)
}

func UpdateTodo(c *gin.Context) {
	userID := c.GetInt64("userId")
	todoID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid todo ID"})
		return
	}

	var req models.UpdateTodoRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Check if todo exists and belongs to user
	var exists bool
	err = database.DB.QueryRow("SELECT EXISTS(SELECT 1 FROM todos WHERE id = $1 AND user_id = $2)",
		todoID, userID).Scan(&exists)
	if err != nil || !exists {
		c.JSON(http.StatusNotFound, gin.H{"error": "Todo not found"})
		return
	}

	query := "UPDATE todos SET updated_at = $1"
	args := []interface{}{time.Now()}
	argCount := 1

	if req.Title != nil {
		argCount++
		query += ", title = $" + strconv.Itoa(argCount)
		args = append(args, *req.Title)
	}
	if req.Description != nil {
		argCount++
		query += ", description = $" + strconv.Itoa(argCount)
		args = append(args, *req.Description)
	}
	if req.Status != nil {
		argCount++
		query += ", status = $" + strconv.Itoa(argCount)
		args = append(args, *req.Status)
	}
	if req.Priority != nil {
		argCount++
		query += ", priority = $" + strconv.Itoa(argCount)
		args = append(args, *req.Priority)
	}
	if req.DueDate != nil {
		argCount++
		query += ", due_date = $" + strconv.Itoa(argCount)
		args = append(args, *req.DueDate)
	}

	argCount++
	query += " WHERE id = $" + strconv.Itoa(argCount) + " AND user_id = $" + strconv.Itoa(argCount+1)
	args = append(args, todoID, userID)

	_, err = database.DB.Exec(query, args...)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update todo"})
		return
	}

	// Fetch updated todo
	var todo models.Todo
	selectQuery := `SELECT id, user_id, title, description, status, priority, due_date, created_at, updated_at 
					FROM todos WHERE id = $1`
	err = database.DB.QueryRow(selectQuery, todoID).Scan(
		&todo.ID, &todo.UserID, &todo.Title, &todo.Description,
		&todo.Status, &todo.Priority, &todo.DueDate, &todo.CreatedAt, &todo.UpdatedAt)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch updated todo"})
		return
	}

	c.JSON(http.StatusOK, todo)
}

func DeleteTodo(c *gin.Context) {
	userID := c.GetInt64("userId")
	todoID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid todo ID"})
		return
	}

	result, err := database.DB.Exec("DELETE FROM todos WHERE id = $1 AND user_id = $2", todoID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete todo"})
		return
	}

	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		c.JSON(http.StatusNotFound, gin.H{"error": "Todo not found"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Todo deleted successfully"})
}

func GetStats(c *gin.Context) {
	userID := c.GetInt64("userId")

	var total, pending, inProgress, completed int
	database.DB.QueryRow("SELECT COUNT(*) FROM todos WHERE user_id = $1", userID).Scan(&total)
	database.DB.QueryRow("SELECT COUNT(*) FROM todos WHERE user_id = $1 AND status = 'pending'", userID).Scan(&pending)
	database.DB.QueryRow("SELECT COUNT(*) FROM todos WHERE user_id = $1 AND status = 'in_progress'", userID).Scan(&inProgress)
	database.DB.QueryRow("SELECT COUNT(*) FROM todos WHERE user_id = $1 AND status = 'completed'", userID).Scan(&completed)

	c.JSON(http.StatusOK, gin.H{
		"total":       total,
		"pending":     pending,
		"in_progress": inProgress,
		"completed":   completed,
	})
}
