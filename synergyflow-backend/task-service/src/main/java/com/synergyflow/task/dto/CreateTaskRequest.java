package com.synergyflow.task.dto;

import com.synergyflow.task.entity.Task.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    
    @NotBlank(message = "Task title is required")
    private String title;
    
    private String description;
    private LocalDateTime dueDate;
    
    @Builder.Default
    private Priority priority = Priority.MEDIUM;
    
    private List<String> assignedUserIds;
}
