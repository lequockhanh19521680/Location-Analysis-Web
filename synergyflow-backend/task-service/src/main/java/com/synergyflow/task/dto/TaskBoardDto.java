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
public class TaskBoardDto {
    private String boardId;
    private String channelId;
    private List<TaskColumnDto> columns;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
