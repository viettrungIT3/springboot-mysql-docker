package com.backend.backend.mapper;

import com.backend.backend.dto.user.UserCreateRequest;
import com.backend.backend.dto.user.UserResponse;
import com.backend.backend.dto.user.UserUpdateRequest;
import com.backend.backend.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Create: DTO -> Entity
    User toEntity(UserCreateRequest request);

    // Read: Entity -> Response (exclude password)
    UserResponse toResponse(User entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget User entity, UserUpdateRequest request);
}
