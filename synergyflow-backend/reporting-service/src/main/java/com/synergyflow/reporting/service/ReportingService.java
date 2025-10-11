package com.synergyflow.reporting.service;

import com.synergyflow.reporting.dto.ProjectStatisticsDto;
import com.synergyflow.reporting.dto.TaskPerformanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportingService {
    
    // In a real implementation, this would fetch data from Task Service via Feign Client
    // For now, we'll provide simulated analytics
    
    public ProjectStatisticsDto getProjectStatistics(String workspaceId) {
        // Simulated data - in production, fetch from Task Service
        Map<String, Integer> tasksByStatus = new HashMap<>();
        tasksByStatus.put("TODO", 15);
        tasksByStatus.put("IN_PROGRESS", 8);
        tasksByStatus.put("DONE", 22);
        tasksByStatus.put("BLOCKED", 2);
        
        Map<String, Integer> tasksByPriority = new HashMap<>();
        tasksByPriority.put("LOW", 10);
        tasksByPriority.put("MEDIUM", 20);
        tasksByPriority.put("HIGH", 12);
        tasksByPriority.put("URGENT", 5);
        
        Map<String, Integer> tasksByAssignee = new HashMap<>();
        tasksByAssignee.put("user1", 15);
        tasksByAssignee.put("user2", 12);
        tasksByAssignee.put("user3", 20);
        
        int totalTasks = 47;
        int completedTasks = 22;
        
        return ProjectStatisticsDto.builder()
                .workspaceId(workspaceId)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(8)
                .todoTasks(15)
                .overdueTasksCount(3)
                .completionPercentage((double) completedTasks / totalTasks * 100)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .tasksByAssignee(tasksByAssignee)
                .build();
    }
    
    public TaskPerformanceDto getUserPerformance(String userId) {
        // Simulated data - in production, fetch from Task Service
        return TaskPerformanceDto.builder()
                .userId(userId)
                .totalAssignedTasks(25)
                .completedTasks(20)
                .onTimeTasks(18)
                .lateTasks(2)
                .averageCompletionTime(4.5)
                .performanceScore(90.0)
                .build();
    }
}
