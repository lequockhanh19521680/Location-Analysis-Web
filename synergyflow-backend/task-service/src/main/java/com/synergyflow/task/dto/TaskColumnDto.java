package com.synergyflow.task.dto;

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
public class TaskColumnDto {
    private String columnId;
    private String title;
    private String boardId;
    private Integer order;
    private List<TaskDto> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
