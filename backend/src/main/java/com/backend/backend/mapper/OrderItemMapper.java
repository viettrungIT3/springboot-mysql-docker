package com.backend.backend.mapper;

import com.backend.backend.dto.orderitem.OrderItemCreateRequest;
import com.backend.backend.dto.orderitem.OrderItemResponse;
import com.backend.backend.dto.orderitem.OrderItemUpdateRequest;
import com.backend.backend.entity.OrderItem;
import com.backend.backend.entity.Order;
import com.backend.backend.entity.Product;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {

    // Create: DTO -> Entity
    @Mapping(target = "order", source = "orderId", qualifiedByName = "orderIdToOrder")
    @Mapping(target = "product", source = "productId", qualifiedByName = "productIdToProduct")
    @Mapping(target = "id", ignore = true)
    OrderItem toEntity(OrderItemCreateRequest request);

    // Read: Entity -> Response
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "totalPrice", source = ".", qualifiedByName = "calculateTotalPrice")
    OrderItemResponse toResponse(OrderItem entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "order", source = "orderId", qualifiedByName = "orderIdToOrder")
    @Mapping(target = "product", source = "productId", qualifiedByName = "productIdToProduct")
    void updateEntity(@MappingTarget OrderItem entity, OrderItemUpdateRequest request);

    // Helper methods
    @Named("orderIdToOrder")
    default Order orderIdToOrder(Long orderId) {
        if (orderId == null) return null;
        Order order = new Order();
        order.setId(orderId);
        return order;
    }

    @Named("productIdToProduct")
    default Product productIdToProduct(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }

    @Named("calculateTotalPrice")
    default BigDecimal calculateTotalPrice(OrderItem orderItem) {
        if (orderItem.getQuantity() == null || orderItem.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }
}
