package com.synergyflow.notification.controller;

import com.synergyflow.common.dto.ApiResponse;
import com.synergyflow.notification.dto.NotificationDto;
import com.synergyflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "50") int limit) {
        List<NotificationDto> notifications = notificationService.getUserNotifications(userId, limit);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }
}
