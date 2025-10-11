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
@Table(name = "task_columns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskColumn {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "column_id")
    private String columnId;
    
    @Column(nullable = false)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private TaskBoard board;
    
    @Column(name = "column_order", nullable = false)
    private Integer order;
    
    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
