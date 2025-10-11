package com.synergyflow.workspace.mapper;

import com.synergyflow.workspace.dto.WorkspaceDto;
import com.synergyflow.workspace.entity.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ChannelMapper.class, WorkspaceMemberMapper.class})
public interface WorkspaceMapper {
    
    @Mapping(target = "workspaceId", source = "workspaceId")
    WorkspaceDto toDto(Workspace workspace);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Workspace toEntity(WorkspaceDto workspaceDto);
}
