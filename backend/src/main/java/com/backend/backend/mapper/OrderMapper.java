package com.backend.backend.mapper;

import com.backend.backend.dto.order.OrderCreateRequest;
import com.backend.backend.dto.order.OrderResponse;
import com.backend.backend.dto.order.OrderUpdateRequest;
import com.backend.backend.entity.Order;
import com.backend.backend.entity.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Create: DTO -> Entity 
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "customerIdToCustomer")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true) // Items will be handled separately
    @Mapping(target = "totalAmount", ignore = true) // Will be calculated
    Order toEntity(OrderCreateRequest request);

    // Read: Entity -> Response
    OrderResponse toResponse(Order entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "customerIdToCustomer")
    @Mapping(target = "items", ignore = true) // Don't update items via order update
    @Mapping(target = "totalAmount", ignore = true) // Will be recalculated
    void updateEntity(@MappingTarget Order entity, OrderUpdateRequest request);

    // Helper method
    @Named("customerIdToCustomer")
    default Customer customerIdToCustomer(Long customerId) {
        if (customerId == null) return null;
        Customer customer = new Customer();
        customer.setId(customerId);
        return customer;
    }
}
