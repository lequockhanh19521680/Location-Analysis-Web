package com.synergyflow.workspace.dto;

import com.synergyflow.workspace.entity.WorkspaceMember.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Role is required")
    private MemberRole role;
}
