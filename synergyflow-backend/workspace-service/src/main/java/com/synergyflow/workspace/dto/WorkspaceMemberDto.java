package com.synergyflow.workspace.dto;

import com.synergyflow.workspace.entity.WorkspaceMember.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMemberDto {
    private String id;
    private String workspaceId;
    private String userId;
    private MemberRole role;
    private LocalDateTime joinedAt;
}
