package com.synergyflow.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDto {
    private String channelId;
    private String name;
    private String description;
    private String workspaceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
