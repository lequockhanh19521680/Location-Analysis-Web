package com.synergyflow.reporting.controller;

import com.synergyflow.common.dto.ApiResponse;
import com.synergyflow.reporting.dto.ProjectStatisticsDto;
import com.synergyflow.reporting.dto.TaskPerformanceDto;
import com.synergyflow.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportingController {
    
    private final ReportingService reportingService;
    
    @GetMapping("/workspaces/{workspaceId}/statistics")
    public ResponseEntity<ApiResponse<ProjectStatisticsDto>> getProjectStatistics(
            @PathVariable String workspaceId) {
        ProjectStatisticsDto statistics = reportingService.getProjectStatistics(workspaceId);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/users/{userId}/performance")
    public ResponseEntity<ApiResponse<TaskPerformanceDto>> getUserPerformance(
            @PathVariable String userId) {
        TaskPerformanceDto performance = reportingService.getUserPerformance(userId);
        return ResponseEntity.ok(ApiResponse.success(performance));
    }
}
