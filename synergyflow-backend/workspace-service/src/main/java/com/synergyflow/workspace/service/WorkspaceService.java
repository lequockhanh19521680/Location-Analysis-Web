package com.synergyflow.workspace.service;

import com.synergyflow.common.exception.BadRequestException;
import com.synergyflow.common.exception.ResourceNotFoundException;
import com.synergyflow.common.exception.UnauthorizedException;
import com.synergyflow.workspace.dto.*;
import com.synergyflow.workspace.entity.Channel;
import com.synergyflow.workspace.entity.Workspace;
import com.synergyflow.workspace.entity.WorkspaceMember;
import com.synergyflow.workspace.entity.WorkspaceMember.MemberRole;
import com.synergyflow.workspace.mapper.ChannelMapper;
import com.synergyflow.workspace.mapper.WorkspaceMapper;
import com.synergyflow.workspace.mapper.WorkspaceMemberMapper;
import com.synergyflow.workspace.repository.ChannelRepository;
import com.synergyflow.workspace.repository.WorkspaceMemberRepository;
import com.synergyflow.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    
    private final WorkspaceRepository workspaceRepository;
    private final ChannelRepository channelRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceMapper workspaceMapper;
    private final ChannelMapper channelMapper;
    private final WorkspaceMemberMapper memberMapper;
    
    @Transactional
    public WorkspaceDto createWorkspace(String userId, CreateWorkspaceRequest request) {
        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ownerId(userId)
                .build();
        
        workspace = workspaceRepository.save(workspace);
        
        // Add owner as member
        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .workspace(workspace)
                .userId(userId)
                .role(MemberRole.OWNER)
                .build();
        
        memberRepository.save(ownerMember);
        
        return workspaceMapper.toDto(workspace);
    }
    
    public List<WorkspaceDto> getUserWorkspaces(String userId) {
        List<Workspace> workspaces = workspaceRepository.findByMemberUserId(userId);
        return workspaces.stream()
                .map(workspaceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public WorkspaceDto getWorkspaceById(String workspaceId, String userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));
        
        checkMemberAccess(workspaceId, userId);
        
        return workspaceMapper.toDto(workspace);
    }
    
    @Transactional
    public WorkspaceDto updateWorkspace(String workspaceId, String userId, CreateWorkspaceRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));
        
        checkOwnerAccess(workspace, userId);
        
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        
        workspace = workspaceRepository.save(workspace);
        return workspaceMapper.toDto(workspace);
    }
    
    @Transactional
    public void deleteWorkspace(String workspaceId, String userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));
        
        checkOwnerAccess(workspace, userId);
        
        workspaceRepository.delete(workspace);
    }
    
    @Transactional
    public ChannelDto createChannel(String workspaceId, String userId, CreateChannelRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));
        
        checkAdminAccess(workspaceId, userId);
        
        Channel channel = Channel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .workspace(workspace)
                .build();
        
        channel = channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }
    
    public List<ChannelDto> getWorkspaceChannels(String workspaceId, String userId) {
        checkMemberAccess(workspaceId, userId);
        
        List<Channel> channels = channelRepository.findByWorkspace_WorkspaceId(workspaceId);
        return channels.stream()
                .map(channelMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteChannel(String channelId, String userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel", "id", channelId));
        
        checkAdminAccess(channel.getWorkspace().getWorkspaceId(), userId);
        
        channelRepository.delete(channel);
    }
    
    @Transactional
    public WorkspaceMemberDto addMember(String workspaceId, String userId, AddMemberRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));
        
        checkAdminAccess(workspaceId, userId);
        
        if (memberRepository.existsByWorkspace_WorkspaceIdAndUserId(workspaceId, request.getUserId())) {
            throw new BadRequestException("User is already a member of this workspace");
        }
        
        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .userId(request.getUserId())
                .role(request.getRole())
                .build();
        
        member = memberRepository.save(member);
        return memberMapper.toDto(member);
    }
    
    public List<WorkspaceMemberDto> getWorkspaceMembers(String workspaceId, String userId) {
        checkMemberAccess(workspaceId, userId);
        
        List<WorkspaceMember> members = memberRepository.findByWorkspace_WorkspaceId(workspaceId);
        return members.stream()
                .map(memberMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void removeMember(String workspaceId, String memberId, String userId) {
        checkAdminAccess(workspaceId, userId);
        
        WorkspaceMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));
        
        if (member.getRole() == MemberRole.OWNER) {
            throw new BadRequestException("Cannot remove workspace owner");
        }
        
        memberRepository.delete(member);
    }
    
    private void checkMemberAccess(String workspaceId, String userId) {
        if (!memberRepository.existsByWorkspace_WorkspaceIdAndUserId(workspaceId, userId)) {
            throw new UnauthorizedException("You are not a member of this workspace");
        }
    }
    
    private void checkAdminAccess(String workspaceId, String userId) {
        WorkspaceMember member = memberRepository.findByWorkspace_WorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this workspace"));
        
        if (member.getRole() != MemberRole.OWNER && member.getRole() != MemberRole.ADMIN) {
            throw new UnauthorizedException("You need admin or owner privileges");
        }
    }
    
    private void checkOwnerAccess(Workspace workspace, String userId) {
        if (!workspace.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("Only workspace owner can perform this action");
        }
    }
}
