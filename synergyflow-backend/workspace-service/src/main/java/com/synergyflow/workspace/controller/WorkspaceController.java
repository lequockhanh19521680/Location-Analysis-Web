package com.synergyflow.workspace.controller;

import com.synergyflow.common.dto.ApiResponse;
import com.synergyflow.workspace.dto.*;
import com.synergyflow.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    
    private final WorkspaceService workspaceService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceDto>> createWorkspace(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateWorkspaceRequest request) {
        WorkspaceDto workspace = workspaceService.createWorkspace(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workspace created successfully", workspace));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkspaceDto>>> getUserWorkspaces(
            @RequestHeader("X-User-Id") String userId) {
        List<WorkspaceDto> workspaces = workspaceService.getUserWorkspaces(userId);
        return ResponseEntity.ok(ApiResponse.success(workspaces));
    }
    
    @GetMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceDto>> getWorkspaceById(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId) {
        WorkspaceDto workspace = workspaceService.getWorkspaceById(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(workspace));
    }
    
    @PutMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceDto>> updateWorkspace(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateWorkspaceRequest request) {
        WorkspaceDto workspace = workspaceService.updateWorkspace(workspaceId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Workspace updated successfully", workspace));
    }
    
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkspace(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId) {
        workspaceService.deleteWorkspace(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success("Workspace deleted successfully", null));
    }
    
    @PostMapping("/{workspaceId}/channels")
    public ResponseEntity<ApiResponse<ChannelDto>> createChannel(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateChannelRequest request) {
        ChannelDto channel = workspaceService.createChannel(workspaceId, userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Channel created successfully", channel));
    }
    
    @GetMapping("/{workspaceId}/channels")
    public ResponseEntity<ApiResponse<List<ChannelDto>>> getWorkspaceChannels(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId) {
        List<ChannelDto> channels = workspaceService.getWorkspaceChannels(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(channels));
    }
    
    @DeleteMapping("/channels/{channelId}")
    public ResponseEntity<ApiResponse<Void>> deleteChannel(
            @PathVariable String channelId,
            @RequestHeader("X-User-Id") String userId) {
        workspaceService.deleteChannel(channelId, userId);
        return ResponseEntity.ok(ApiResponse.success("Channel deleted successfully", null));
    }
    
    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<WorkspaceMemberDto>> addMember(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddMemberRequest request) {
        WorkspaceMemberDto member = workspaceService.addMember(workspaceId, userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member added successfully", member));
    }
    
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceMemberDto>>> getWorkspaceMembers(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId) {
        List<WorkspaceMemberDto> members = workspaceService.getWorkspaceMembers(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }
    
    @DeleteMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String workspaceId,
            @PathVariable String memberId,
            @RequestHeader("X-User-Id") String userId) {
        workspaceService.removeMember(workspaceId, memberId, userId);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", null));
    }
}
