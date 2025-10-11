package com.synergyflow.task.mapper;

import com.synergyflow.task.dto.TaskBoardDto;
import com.synergyflow.task.entity.TaskBoard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TaskColumnMapper.class})
public interface TaskBoardMapper {
    
    TaskBoardDto toDto(TaskBoard taskBoard);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TaskBoard toEntity(TaskBoardDto taskBoardDto);
}
