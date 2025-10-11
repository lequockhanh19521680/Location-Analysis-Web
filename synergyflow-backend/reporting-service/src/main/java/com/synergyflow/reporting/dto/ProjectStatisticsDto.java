package com.synergyflow.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatisticsDto {
    private String workspaceId;
    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int todoTasks;
    private int overdueTasksCount;
    private double completionPercentage;
    private Map<String, Integer> tasksByStatus;
    private Map<String, Integer> tasksByPriority;
    private Map<String, Integer> tasksByAssignee;
}
