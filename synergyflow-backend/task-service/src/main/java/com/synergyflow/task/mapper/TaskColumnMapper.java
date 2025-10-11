package com.synergyflow.task.mapper;

import com.synergyflow.task.dto.TaskColumnDto;
import com.synergyflow.task.entity.TaskColumn;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface TaskColumnMapper {
    
    @Mapping(target = "boardId", source = "board.boardId")
    TaskColumnDto toDto(TaskColumn taskColumn);
    
    @Mapping(target = "board", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TaskColumn toEntity(TaskColumnDto taskColumnDto);
}
