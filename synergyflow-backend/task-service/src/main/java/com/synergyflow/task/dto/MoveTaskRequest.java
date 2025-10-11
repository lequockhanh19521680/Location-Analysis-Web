package com.synergyflow.task.dto;

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
public class MoveTaskRequest {
    
    @NotBlank(message = "Target column ID is required")
    private String targetColumnId;
    
    @NotNull(message = "New order is required")
    private Integer newOrder;
}
