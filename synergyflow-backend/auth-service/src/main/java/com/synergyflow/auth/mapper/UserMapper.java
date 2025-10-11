package com.synergyflow.auth.mapper;

import com.synergyflow.auth.dto.UserDto;
import com.synergyflow.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDto toDto(User user);
    
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDto userDto);
}
