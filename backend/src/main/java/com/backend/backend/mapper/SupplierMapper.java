package com.backend.backend.mapper;

import com.backend.backend.dto.supplier.SupplierCreateRequest;
import com.backend.backend.dto.supplier.SupplierResponse;
import com.backend.backend.dto.supplier.SupplierUpdateRequest;
import com.backend.backend.entity.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    // Create: DTO -> Entity
    Supplier toEntity(SupplierCreateRequest request);

    // Read: Entity -> Response
    SupplierResponse toResponse(Supplier entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Supplier entity, SupplierUpdateRequest request);
}
