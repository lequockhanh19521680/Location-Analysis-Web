package main

import (
	"log"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/lequockhanh19521680/todo-service/database"
	"github.com/lequockhanh19521680/todo-service/handlers"
	"github.com/lequockhanh19521680/todo-service/middleware"
)

func main() {
	// Initialize database
	if err := database.InitDB(); err != nil {
		log.Fatal("Failed to initialize database:", err)
	}

	// Create router
	r := gin.Default()

	// CORS middleware
	r.Use(func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Credentials", "true")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, accept, origin, Cache-Control, X-Requested-With")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT, DELETE")

		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}

		c.Next()
	})

	// Health check
	r.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok", "service": "todo-service"})
	})

	// API routes
	api := r.Group("/api/todos")
	api.Use(middleware.AuthMiddleware())
	{
		api.GET("", handlers.GetTodos)
		api.GET("/:id", handlers.GetTodoByID)
		api.POST("", handlers.CreateTodo)
		api.PUT("/:id", handlers.UpdateTodo)
		api.DELETE("/:id", handlers.DeleteTodo)
		api.GET("/stats", handlers.GetStats)
	}

	// Start server
	port := os.Getenv("PORT")
	if port == "" {
		port = "8082"
	}

	log.Printf("Todo Service running on port %s", port)
	if err := r.Run(":" + port); err != nil {
		log.Fatal("Failed to start server:", err)
	}
}
