package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.product.ProductUpdateRequest;
import com.backend.backend.entity.Product;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.ProductMapper;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.util.PageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product entity1;
    private Product entity2;
    private ProductCreateRequest createRequest;
    private ProductUpdateRequest updateRequest;
    private ProductResponse response1;
    private ProductResponse response2;

    @BeforeEach
    void setUp() {
        // Test entities
        entity1 = Product.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Latest iPhone model with Pro features")
                .price(new BigDecimal("1299.99"))
                .quantityInStock(50)
                .build();

        entity2 = Product.builder()
                .id(2L)
                .name("iPhone 14")
                .description("Previous generation iPhone")
                .price(new BigDecimal("999.99"))
                .quantityInStock(30)
                .build();

        // Test requests
        createRequest = new ProductCreateRequest();
        createRequest.setName("MacBook Air");
        createRequest.setDescription("Lightweight laptop");
        createRequest.setPrice(new BigDecimal("1599.99"));
        createRequest.setQuantityInStock(25);

        updateRequest = new ProductUpdateRequest();
        updateRequest.setQuantityInStock(60); // partial update

        // Test responses
        response1 = ProductResponse.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Latest iPhone model with Pro features")
                .price(new BigDecimal("1299.99"))
                .quantityInStock(50)
                .build();

        response2 = ProductResponse.builder()
                .id(2L)
                .name("iPhone 14")
                .description("Previous generation iPhone")
                .price(new BigDecimal("999.99"))
                .quantityInStock(30)
                .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void create_shouldReturnMappedResponse() {
        // Arrange
        Product toSave = Product.builder()
                .name("MacBook Air")
                .description("Lightweight laptop")
                .price(new BigDecimal("1599.99"))
                .quantityInStock(25)
                .build();

        Product saved = Product.builder()
                .id(99L)
                .name("MacBook Air")
                .description("Lightweight laptop")
                .price(new BigDecimal("1599.99"))
                .quantityInStock(25)
                .build();

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(99L)
                .name("MacBook Air")
                .description("Lightweight laptop")
                .price(new BigDecimal("1599.99"))
                .quantityInStock(25)
                .build();

        given(productMapper.toEntity(createRequest)).willReturn(toSave);
        given(productRepository.save(toSave)).willReturn(saved);
        given(productMapper.toResponse(saved)).willReturn(expectedResponse);

        // Act
        ProductResponse result = productService.create(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getName()).isEqualTo("MacBook Air");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("1599.99"));
        assertThat(result.getQuantityInStock()).isEqualTo(25);

        verify(productMapper).toEntity(createRequest);
        verify(productRepository).save(toSave);
        verify(productMapper).toResponse(saved);
    }

    @Test
    @DisplayName("Should update product successfully with partial data")
    void update_shouldApplyPartialUpdateAndReturnResponse() {
        // Arrange
        Long id = 1L;
        
        given(productRepository.findById(id)).willReturn(Optional.of(entity1));
        
        // Mock updateEntity method - MapStruct void method
        doAnswer(invocation -> {
            Product entity = invocation.getArgument(0);
            ProductUpdateRequest request = invocation.getArgument(1);
            if (request.getQuantityInStock() != null) {
                entity.setQuantityInStock(request.getQuantityInStock());
            }
            return null;
        }).when(productMapper).updateEntity(any(Product.class), any(ProductUpdateRequest.class));

        Product savedEntity = Product.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Latest iPhone model with Pro features") 
                .price(new BigDecimal("1299.99"))
                .quantityInStock(60) // updated
                .build();

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Latest iPhone model with Pro features")
                .price(new BigDecimal("1299.99"))
                .quantityInStock(60)
                .build();

        given(productRepository.save(entity1)).willReturn(savedEntity);
        given(productMapper.toResponse(savedEntity)).willReturn(expectedResponse);

        // Act
        ProductResponse result = productService.update(id, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getQuantityInStock()).isEqualTo(60);
        
        verify(productRepository).findById(id);
        verify(productMapper).updateEntity(entity1, updateRequest);
        verify(productRepository).save(entity1);
        verify(productMapper).toResponse(savedEntity);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void update_whenNotFound_shouldThrow() {
        // Arrange
        Long nonExistentId = 999L;
        given(productRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.update(nonExistentId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy sản phẩm với ID: " + nonExistentId);

        verify(productRepository).findById(nonExistentId);
        verifyNoInteractions(productMapper);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void getById_shouldReturnResponse() {
        // Arrange
        given(productRepository.findById(2L)).willReturn(Optional.of(entity2));
        given(productMapper.toResponse(entity2)).willReturn(response2);

        // Act
        ProductResponse result = productService.getById(2L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("iPhone 14");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("999.99"));

        verify(productRepository).findById(2L);
        verify(productMapper).toResponse(entity2);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent product")
    void getById_whenNotFound_shouldThrow() {
        // Arrange
        Long nonExistentId = 999L;
        given(productRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy sản phẩm với ID: " + nonExistentId);

        verify(productRepository).findById(nonExistentId);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("Should return paged response with sorting and search")
    void list_shouldReturnPagedResponse_withSortingAndSearch() {
        // Arrange
        int page = 0, size = 2;
        String sort = "id,desc";
        String search = "iPhone";

        Sort expectedSort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page, size, expectedSort);
        
        Page<Product> pageData = new PageImpl<>(List.of(entity1, entity2), pageable, 2);

        given(productRepository.findByNameContainingIgnoreCase(search, pageable)).willReturn(pageData);
        given(productMapper.toResponse(entity1)).willReturn(response1);
        given(productMapper.toResponse(entity2)).willReturn(response2);

        // Mock PageMapper static method behavior
        PageResponse<ProductResponse> expectedPageResponse = new PageResponse<ProductResponse>();
        expectedPageResponse.setItems(List.of(response1, response2));
        expectedPageResponse.setPage(0);
        expectedPageResponse.setSize(2);
        expectedPageResponse.setTotalElements(2L);
        expectedPageResponse.setTotalPages(1);

        try (MockedStatic<PageMapper> pageMapperMock = mockStatic(PageMapper.class)) {
            pageMapperMock.when(() -> PageMapper.toPageResponse(eq(pageData), any()))
                    .thenReturn(expectedPageResponse);

            // Act
            PageResponse<ProductResponse> result = productService.list(page, size, sort, search);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getItems()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2L);
            assertThat(result.getPage()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(1);

            verify(productRepository).findByNameContainingIgnoreCase(search, pageable);
            pageMapperMock.verify(() -> PageMapper.toPageResponse(eq(pageData), any()));
        }
    }

    @Test
    @DisplayName("Should return paged response without search")
    void list_shouldReturnPagedResponse_withoutSearch() {
        // Arrange
        int page = 0, size = 10;
        String sort = "name,asc";
        
        Sort expectedSort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, expectedSort);
        
        Page<Product> pageData = new PageImpl<>(List.of(entity1, entity2), pageable, 2);

        given(productRepository.findAll(pageable)).willReturn(pageData);
        given(productMapper.toResponse(entity1)).willReturn(response1);
        given(productMapper.toResponse(entity2)).willReturn(response2);

        PageResponse<ProductResponse> expectedPageResponse = new PageResponse<ProductResponse>();
        expectedPageResponse.setItems(List.of(response1, response2));
        expectedPageResponse.setPage(0);
        expectedPageResponse.setSize(10);
        expectedPageResponse.setTotalElements(2L);
        expectedPageResponse.setTotalPages(1);

        try (MockedStatic<PageMapper> pageMapperMock = mockStatic(PageMapper.class)) {
            pageMapperMock.when(() -> PageMapper.toPageResponse(eq(pageData), any()))
                    .thenReturn(expectedPageResponse);

            // Act
            PageResponse<ProductResponse> result = productService.list(page, size, sort, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getItems()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2L);

            verify(productRepository).findAll(pageable);
            verify(productRepository, never()).findByNameContainingIgnoreCase(anyString(), any());
            pageMapperMock.verify(() -> PageMapper.toPageResponse(eq(pageData), any()));
        }
    }

    @Test
    @DisplayName("Should get all products successfully")
    void findAll_shouldReturnListOfProducts() {
        // Arrange
        List<Product> entities = List.of(entity1, entity2);
        given(productRepository.findAll()).willReturn(entities);
        given(productMapper.toResponse(entity1)).willReturn(response1);
        given(productMapper.toResponse(entity2)).willReturn(response2);

        // Act
        List<ProductResponse> result = productService.findAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15 Pro");
        assertThat(result.get(1).getName()).isEqualTo("iPhone 14");

        verify(productRepository).findAll();
        verify(productMapper).toResponse(entity1);
        verify(productMapper).toResponse(entity2);
    }

    @Test
    @DisplayName("Should delete product successfully when exists")
    void delete_shouldDeleteWhenExists() {
        // Arrange
        Long id = 1L;
        given(productRepository.existsById(id)).willReturn(true);
        willDoNothing().given(productRepository).deleteById(id);

        // Act
        productService.delete(id);

        // Assert
        verify(productRepository).existsById(id);
        verify(productRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void delete_whenNotExists_shouldThrow() {
        // Arrange
        Long nonExistentId = 999L;
        given(productRepository.existsById(nonExistentId)).willReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> productService.delete(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy sản phẩm với ID: " + nonExistentId);

        verify(productRepository).existsById(nonExistentId);
        verify(productRepository, never()).deleteById(nonExistentId);
    }
}
