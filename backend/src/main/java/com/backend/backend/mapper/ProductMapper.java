package com.backend.backend.mapper;

import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.product.ProductUpdateRequest;
import com.backend.backend.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Create: DTO -> Entity
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductCreateRequest request);

    // Read: Entity -> Response
    ProductResponse toResponse(Product entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Product entity, ProductUpdateRequest request);
}
