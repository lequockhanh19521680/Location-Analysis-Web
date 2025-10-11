package com.synergyflow.task.dto;

import com.synergyflow.task.entity.Task.Priority;
import com.synergyflow.task.entity.Task.Status;
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
public class TaskDto {
    private String taskId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private Status status;
    private String columnId;
    private String creatorId;
    private Integer order;
    private List<String> assignedUserIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
