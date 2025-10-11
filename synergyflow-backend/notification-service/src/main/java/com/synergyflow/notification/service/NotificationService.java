package com.synergyflow.notification.service;

import com.synergyflow.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, NotificationDto> redisTemplate;
    
    private static final String NOTIFICATION_KEY_PREFIX = "notifications:";
    
    @RabbitListener(queues = "task.notifications")
    public void handleTaskNotification(TaskNotificationMessage message) {
        NotificationDto notification = NotificationDto.builder()
                .id(UUID.randomUUID().toString())
                .userId(message.userId)
                .message(buildNotificationMessage(message.type, message.taskId))
                .type(message.type)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        // Store in Redis
        storeNotification(notification);
        
        // Send real-time notification via WebSocket
        sendRealtimeNotification(notification);
    }
    
    public void storeNotification(NotificationDto notification) {
        String key = NOTIFICATION_KEY_PREFIX + notification.getUserId();
        redisTemplate.opsForList().leftPush(key, notification);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
    
    public void sendRealtimeNotification(NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
                notification.getUserId(),
                "/queue/notifications",
                notification
        );
    }
    
    public List<NotificationDto> getUserNotifications(String userId, int limit) {
        String key = NOTIFICATION_KEY_PREFIX + userId;
        List<NotificationDto> notifications = redisTemplate.opsForList().range(key, 0, limit - 1);
        return notifications != null ? notifications : new ArrayList<>();
    }
    
    public void markAsRead(String userId, String notificationId) {
        List<NotificationDto> notifications = getUserNotifications(userId, 100);
        notifications.stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .ifPresent(n -> n.setRead(true));
    }
    
    private String buildNotificationMessage(String type, String taskId) {
        return switch (type) {
            case "TASK_ASSIGNED" -> "You have been assigned to a new task";
            case "TASK_UPDATED" -> "A task you're assigned to has been updated";
            case "TASK_DUE_SOON" -> "A task deadline is approaching";
            default -> "New notification";
        };
    }
    
    // Inner class for task notification message
    public static class TaskNotificationMessage {
        public String userId;
        public String taskId;
        public String type;
    }
}
