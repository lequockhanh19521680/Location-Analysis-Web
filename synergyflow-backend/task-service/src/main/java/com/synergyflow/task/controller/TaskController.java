package com.synergyflow.task.controller;

import com.synergyflow.common.dto.ApiResponse;
import com.synergyflow.task.dto.*;
import com.synergyflow.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping("/boards/channel/{channelId}")
    public ResponseEntity<ApiResponse<TaskBoardDto>> getOrCreateBoard(@PathVariable String channelId) {
        TaskBoardDto board = taskService.getOrCreateBoard(channelId);
        return ResponseEntity.ok(ApiResponse.success(board));
    }
    
    @PostMapping("/columns/{columnId}")
    public ResponseEntity<ApiResponse<TaskDto>> createTask(
            @PathVariable String columnId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskDto task = taskService.createTask(columnId, userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", task));
    }
    
    @GetMapping("/columns/{columnId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getColumnTasks(@PathVariable String columnId) {
        List<TaskDto> tasks = taskService.getColumnTasks(columnId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @PostMapping("/boards/channel/{channelId}/columns")
    public ResponseEntity<ApiResponse<TaskColumnDto>> createColumn(
            @PathVariable String channelId,
            @Valid @RequestBody CreateColumnRequest request) {
        TaskColumnDto column = taskService.createColumn(channelId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Column created successfully", column));
    }
    
    @GetMapping("/boards/channel/{channelId}/columns")
    public ResponseEntity<ApiResponse<List<TaskColumnDto>>> getBoardColumns(@PathVariable String channelId) {
        List<TaskColumnDto> columns = taskService.getBoardColumns(channelId);
        return ResponseEntity.ok(ApiResponse.success(columns));
    }
    
    @DeleteMapping("/columns/{columnId}")
    public ResponseEntity<ApiResponse<Void>> deleteColumn(@PathVariable String columnId) {
        taskService.deleteColumn(columnId);
        return ResponseEntity.ok(ApiResponse.success("Column deleted successfully", null));
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDto>> getTaskById(@PathVariable String taskId) {
        TaskDto task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskDto task = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }
    
    @PostMapping("/{taskId}/move")
    public ResponseEntity<ApiResponse<TaskDto>> moveTask(
            @PathVariable String taskId,
            @Valid @RequestBody MoveTaskRequest request) {
        TaskDto task = taskService.moveTask(taskId, request);
        return ResponseEntity.ok(ApiResponse.success("Task moved successfully", task));
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
    
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<TaskDto> tasks = taskService.getTasksByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getTasksByAssignee(@PathVariable String userId) {
        List<TaskDto> tasks = taskService.getTasksByAssignee(userId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
}
