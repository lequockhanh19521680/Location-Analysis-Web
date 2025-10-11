package com.synergyflow.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkspaceRequest {
    
    @NotBlank(message = "Workspace name is required")
    private String name;
    
    private String description;
}
