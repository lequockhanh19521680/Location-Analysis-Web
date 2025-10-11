package com.synergyflow.workspace.mapper;

import com.synergyflow.workspace.dto.ChannelDto;
import com.synergyflow.workspace.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChannelMapper {
    
    @Mapping(target = "workspaceId", source = "workspace.workspaceId")
    ChannelDto toDto(Channel channel);
    
    @Mapping(target = "workspace", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Channel toEntity(ChannelDto channelDto);
}
