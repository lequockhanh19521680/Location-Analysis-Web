package com.synergyflow.task.service;

import com.synergyflow.common.exception.BadRequestException;
import com.synergyflow.common.exception.ResourceNotFoundException;
import com.synergyflow.task.dto.*;
import com.synergyflow.task.entity.*;
import com.synergyflow.task.mapper.TaskBoardMapper;
import com.synergyflow.task.mapper.TaskColumnMapper;
import com.synergyflow.task.mapper.TaskMapper;
import com.synergyflow.task.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskBoardRepository boardRepository;
    private final TaskColumnRepository columnRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository assignmentRepository;
    private final TaskBoardMapper boardMapper;
    private final TaskColumnMapper columnMapper;
    private final TaskMapper taskMapper;
    private final RabbitTemplate rabbitTemplate;
    
    @Transactional
    public TaskBoardDto getOrCreateBoard(String channelId) {
        TaskBoard board = boardRepository.findByChannelId(channelId)
                .orElseGet(() -> {
                    TaskBoard newBoard = TaskBoard.builder()
                            .channelId(channelId)
                            .build();
                    return boardRepository.save(newBoard);
                });
        
        return boardMapper.toDto(board);
    }
    
    @Transactional
    public TaskColumnDto createColumn(String channelId, CreateColumnRequest request) {
        TaskBoard board = boardRepository.findByChannelId(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "channelId", channelId));
        
        int nextOrder = board.getColumns().size();
        
        TaskColumn column = TaskColumn.builder()
                .title(request.getTitle())
                .board(board)
                .order(nextOrder)
                .build();
        
        column = columnRepository.save(column);
        return columnMapper.toDto(column);
    }
    
    public List<TaskColumnDto> getBoardColumns(String channelId) {
        TaskBoard board = boardRepository.findByChannelId(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "channelId", channelId));
        
        List<TaskColumn> columns = columnRepository.findByBoard_BoardIdOrderByOrderAsc(board.getBoardId());
        return columns.stream()
                .map(columnMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteColumn(String columnId) {
        TaskColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column", "id", columnId));
        
        columnRepository.delete(column);
    }
    
    @Transactional
    public TaskDto createTask(String columnId, String userId, CreateTaskRequest request) {
        TaskColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column", "id", columnId));
        
        int nextOrder = column.getTasks().size();
        
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .priority(request.getPriority())
                .status(Task.Status.TODO)
                .column(column)
                .creatorId(userId)
                .order(nextOrder)
                .build();
        
        task = taskRepository.save(task);
        
        // Assign users if provided
        if (request.getAssignedUserIds() != null && !request.getAssignedUserIds().isEmpty()) {
            final Task savedTask = task;
            request.getAssignedUserIds().forEach(assignedUserId -> {
                TaskAssignment assignment = TaskAssignment.builder()
                        .task(savedTask)
                        .userId(assignedUserId)
                        .build();
                assignmentRepository.save(assignment);
                
                // Send notification via RabbitMQ
                sendTaskNotification(assignedUserId, savedTask.getTaskId(), "TASK_ASSIGNED");
            });
        }
        
        return taskMapper.toDto(task);
    }
    
    public List<TaskDto> getColumnTasks(String columnId) {
        List<Task> tasks = taskRepository.findByColumn_ColumnIdOrderByOrderAsc(columnId);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public TaskDto getTaskById(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        return taskMapper.toDto(task);
    }
    
    @Transactional
    public TaskDto updateTask(String taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        
        // Update assignments
        if (request.getAssignedUserIds() != null) {
            // Remove existing assignments
            assignmentRepository.deleteAll(task.getAssignments());
            task.getAssignments().clear();
            
            // Add new assignments
            request.getAssignedUserIds().forEach(userId -> {
                TaskAssignment assignment = TaskAssignment.builder()
                        .task(task)
                        .userId(userId)
                        .build();
                assignmentRepository.save(assignment);
            });
        }
        
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }
    
    @Transactional
    public TaskDto moveTask(String taskId, MoveTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        TaskColumn targetColumn = columnRepository.findById(request.getTargetColumnId())
                .orElseThrow(() -> new ResourceNotFoundException("Column", "id", request.getTargetColumnId()));
        
        TaskColumn sourceColumn = task.getColumn();
        
        // If moving to a different column
        if (!sourceColumn.getColumnId().equals(targetColumn.getColumnId())) {
            // Remove from source column and reorder
            List<Task> sourceTasks = taskRepository.findByColumn_ColumnIdOrderByOrderAsc(sourceColumn.getColumnId());
            sourceTasks.remove(task);
            reorderTasks(sourceTasks);
            
            // Add to target column
            task.setColumn(targetColumn);
        }
        
        // Reorder tasks in target column
        List<Task> targetTasks = taskRepository.findByColumn_ColumnIdOrderByOrderAsc(targetColumn.getColumnId());
        targetTasks.remove(task);
        targetTasks.add(request.getNewOrder(), task);
        reorderTasks(targetTasks);
        
        task.setOrder(request.getNewOrder());
        task = taskRepository.save(task);
        
        return taskMapper.toDto(task);
    }
    
    @Transactional
    public void deleteTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        taskRepository.delete(task);
    }
    
    public List<TaskDto> getTasksByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Task> tasks = taskRepository.findTasksInDateRange(start, end);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<TaskDto> getTasksByAssignee(String userId) {
        List<Task> tasks = taskRepository.findByAssignedUserId(userId);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
    
    private void reorderTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setOrder(i);
            taskRepository.save(tasks.get(i));
        }
    }
    
    private void sendTaskNotification(String userId, String taskId, String type) {
        try {
            TaskNotificationMessage message = new TaskNotificationMessage(userId, taskId, type);
            rabbitTemplate.convertAndSend("task.notifications", message);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
    
    // Inner class for notification message
    private static class TaskNotificationMessage {
        public String userId;
        public String taskId;
        public String type;
        
        public TaskNotificationMessage(String userId, String taskId, String type) {
            this.userId = userId;
            this.taskId = taskId;
            this.type = type;
        }
    }
}
