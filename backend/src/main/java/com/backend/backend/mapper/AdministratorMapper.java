package com.backend.backend.mapper;

import com.backend.backend.dto.administrator.AdministratorCreateRequest;
import com.backend.backend.dto.administrator.AdministratorResponse;
import com.backend.backend.dto.administrator.AdministratorUpdateRequest;
import com.backend.backend.entity.Administrator;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AdministratorMapper {

    // Create: DTO -> Entity
    @Mapping(target = "id", ignore = true)
    Administrator toEntity(AdministratorCreateRequest request);

    // Read: Entity -> Response (exclude password)
    AdministratorResponse toResponse(Administrator entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Administrator entity, AdministratorUpdateRequest request);
}
