package com.backend.backend.mapper;

import com.backend.backend.dto.stockentry.StockEntryCreateRequest;
import com.backend.backend.dto.stockentry.StockEntryResponse;
import com.backend.backend.dto.stockentry.StockEntryUpdateRequest;
import com.backend.backend.entity.StockEntry;
import com.backend.backend.entity.Product;
import com.backend.backend.entity.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, SupplierMapper.class})
public interface StockEntryMapper {

    // Create: DTO -> Entity (cần custom mapping cho relationships)
    @Mapping(target = "product", source = "productId", qualifiedByName = "productIdToProduct")
    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "supplierIdToSupplier")
    @Mapping(target = "id", ignore = true)
    StockEntry toEntity(StockEntryCreateRequest request);

    // Read: Entity -> Response (sử dụng nested mappers)
    StockEntryResponse toResponse(StockEntry entity);

    // Update (partial): chỉ set các field != null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "product", source = "productId", qualifiedByName = "productIdToProduct")
    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "supplierIdToSupplier")
    void updateEntity(@MappingTarget StockEntry entity, StockEntryUpdateRequest request);

    // Helper methods để map IDs to entities
    @Named("productIdToProduct")
    default Product productIdToProduct(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }

    @Named("supplierIdToSupplier")
    default Supplier supplierIdToSupplier(Long supplierId) {
        if (supplierId == null) return null;
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        return supplier;
    }
}
