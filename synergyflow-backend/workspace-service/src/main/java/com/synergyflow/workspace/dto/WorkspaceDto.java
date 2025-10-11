package com.synergyflow.workspace.dto;

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
public class WorkspaceDto {
    private String workspaceId;
    private String name;
    private String description;
    private String ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChannelDto> channels;
    private List<WorkspaceMemberDto> members;
}
