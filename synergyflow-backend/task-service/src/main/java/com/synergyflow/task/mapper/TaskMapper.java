package com.synergyflow.task.mapper;

import com.synergyflow.task.dto.TaskDto;
import com.synergyflow.task.entity.Task;
import com.synergyflow.task.entity.TaskAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    
    @Mapping(target = "columnId", source = "column.columnId")
    @Mapping(target = "assignedUserIds", expression = "java(mapAssignments(task))")
    TaskDto toDto(Task task);
    
    @Mapping(target = "column", ignore = true)
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskDto taskDto);
    
    default List<String> mapAssignments(Task task) {
        return task.getAssignments().stream()
                .map(TaskAssignment::getUserId)
                .collect(Collectors.toList());
    }
}
