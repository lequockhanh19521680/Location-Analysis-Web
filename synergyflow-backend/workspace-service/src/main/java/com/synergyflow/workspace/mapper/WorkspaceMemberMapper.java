package com.synergyflow.workspace.mapper;

import com.synergyflow.workspace.dto.WorkspaceMemberDto;
import com.synergyflow.workspace.entity.WorkspaceMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkspaceMemberMapper {
    
    @Mapping(target = "workspaceId", source = "workspace.workspaceId")
    WorkspaceMemberDto toDto(WorkspaceMember member);
    
    @Mapping(target = "workspace", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    WorkspaceMember toEntity(WorkspaceMemberDto memberDto);
}
