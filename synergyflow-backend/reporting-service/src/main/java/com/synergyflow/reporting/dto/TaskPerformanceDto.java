package com.synergyflow.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPerformanceDto {
    private String userId;
    private int totalAssignedTasks;
    private int completedTasks;
    private int onTimeTasks;
    private int lateTasks;
    private double averageCompletionTime;
    private double performanceScore;
}
