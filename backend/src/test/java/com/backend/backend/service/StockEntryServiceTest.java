package com.backend.backend.service;

import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.stockentry.StockEntryCreateRequest;
import com.backend.backend.dto.stockentry.StockEntryResponse;
import com.backend.backend.dto.supplier.SupplierResponse;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.repository.SupplierRepository;
import com.backend.backend.entity.Product;
import com.backend.backend.entity.StockEntry;
import com.backend.backend.entity.Supplier;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.StockEntryMapper;
import com.backend.backend.repository.StockEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockEntryService Unit Tests")
class StockEntryServiceTest {

    @Mock private StockEntryRepository stockEntryRepository;
    @Mock private StockEntryMapper stockEntryMapper;
    @Mock private ProductRepository productRepository;
    @Mock private SupplierRepository supplierRepository;

    @InjectMocks
    private StockEntryService stockEntryService;

    private StockEntry entity1;
    private Product product;
    private Supplier supplier;
    private StockEntryCreateRequest createRequest;
    private StockEntryResponse response1;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        supplier = Supplier.builder()
                .id(1L)
                .name("Test Supplier")
                .build();

        entity1 = StockEntry.builder()
                .id(1L)
                .product(product)
                .supplier(supplier)
                .quantity(100)
                .entryDate(OffsetDateTime.now())
                .build();

        createRequest = new StockEntryCreateRequest();
        createRequest.setProductId(1L);
        createRequest.setSupplierId(1L);
        createRequest.setQuantity(200);

        response1 = StockEntryResponse.builder()
                .id(1L)
                .product(ProductResponse.builder().id(1L).name("Test Product").build())
                .supplier(SupplierResponse.builder().id(1L).name("Test Supplier").build())
                .quantity(100)
                .entryDate(entity1.getEntryDate())
                .build();
    }

    @Test
    @DisplayName("Should create stock entry successfully")
    void create_shouldReturnMappedResponse() {
        // arrange
        StockEntry toSave = StockEntry.builder()
                .product(product)
                .supplier(supplier)
                .quantity(200)
                .build();

        StockEntry saved = StockEntry.builder()
                .id(99L)
                .product(product)
                .supplier(supplier)
                .quantity(200)
                .entryDate(OffsetDateTime.now())
                .build();

        StockEntryResponse expectedResponse = StockEntryResponse.builder()
                .id(99L)
                .product(ProductResponse.builder().id(1L).name("Test Product").build())
                .supplier(SupplierResponse.builder().id(1L).name("Test Supplier").build())
                .quantity(200)
                .entryDate(saved.getEntryDate())
                .build();

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(supplierRepository.findById(1L)).willReturn(Optional.of(supplier));
        given(stockEntryMapper.toEntity(createRequest)).willReturn(toSave);
        given(stockEntryRepository.save(any(StockEntry.class))).willReturn(saved);
        given(stockEntryMapper.toResponse(saved)).willReturn(expectedResponse);

        // act
        StockEntryResponse result = stockEntryService.create(createRequest);

        // assert
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getQuantity()).isEqualTo(200);
        verify(stockEntryMapper).toEntity(createRequest);
        verify(stockEntryRepository).save(any(StockEntry.class));
        verify(stockEntryMapper).toResponse(saved);
    }

    @Test
    @DisplayName("Should get stock entry by ID successfully")
    void getById_shouldReturnStockEntryWhenExists() {
        // arrange
        given(stockEntryRepository.findById(1L)).willReturn(Optional.of(entity1));
        given(stockEntryMapper.toResponse(entity1)).willReturn(response1);

        // act
        StockEntryResponse result = stockEntryService.getById(1L);

        // assert
        assertThat(result).isEqualTo(response1);
        verify(stockEntryRepository).findById(1L);
        verify(stockEntryMapper).toResponse(entity1);
    }

    @Test
    @DisplayName("Should throw exception when stock entry not found by ID")
    void getById_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(stockEntryRepository.findById(999L)).willReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> stockEntryService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy phiếu nhập kho với ID: 999");

        verify(stockEntryRepository).findById(999L);
        verifyNoInteractions(stockEntryMapper);
    }

    @Test
    @DisplayName("Should delete stock entry successfully")
    void delete_shouldCallRepositoryDelete() {
        // arrange
        given(stockEntryRepository.existsById(1L)).willReturn(true);

        // act
        stockEntryService.delete(1L);

        // assert
        verify(stockEntryRepository).existsById(1L);
        verify(stockEntryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent stock entry")
    void delete_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(stockEntryRepository.existsById(999L)).willReturn(false);

        // act & assert
        assertThatThrownBy(() -> stockEntryService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy phiếu nhập kho với ID: 999");

        verify(stockEntryRepository).existsById(999L);
        verify(stockEntryRepository, never()).deleteById(anyLong());
    }
}
