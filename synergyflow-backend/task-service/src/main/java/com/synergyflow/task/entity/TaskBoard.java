package com.synergyflow.task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_boards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskBoard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "board_id")
    private String boardId;
    
    @Column(name = "channel_id", nullable = false, unique = true)
    private String channelId;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    @Builder.Default
    private List<TaskColumn> columns = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
