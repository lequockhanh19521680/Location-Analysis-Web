package com.synergyflow.workspace.entity;

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
@Table(name = "workspaces")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "workspace_id")
    private String workspaceId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "owner_id", nullable = false)
    private String ownerId;
    
    @Column(length = 1000)
    private String description;
    
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Channel> channels = new ArrayList<>();
    
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkspaceMember> members = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
