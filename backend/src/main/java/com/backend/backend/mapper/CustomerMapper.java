package com.backend.backend.mapper;

import com.backend.backend.dto.customer.CustomerCreateRequest;
import com.backend.backend.dto.customer.CustomerResponse;
import com.backend.backend.dto.customer.CustomerUpdateRequest;
import com.backend.backend.entity.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Create: DTO -> Entity
    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerCreateRequest request);

    // Read: Entity -> Response
    CustomerResponse toResponse(Customer entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Customer entity, CustomerUpdateRequest request);
}
