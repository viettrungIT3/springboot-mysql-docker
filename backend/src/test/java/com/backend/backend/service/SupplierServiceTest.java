package com.backend.backend.service;

import com.backend.backend.dto.supplier.SupplierCreateRequest;
import com.backend.backend.dto.supplier.SupplierResponse;
import com.backend.backend.entity.Supplier;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.SupplierMapper;
import com.backend.backend.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService Unit Tests") 
class SupplierServiceTest {

    @Mock private SupplierRepository supplierRepository;
    @Mock private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier entity1;
    private SupplierCreateRequest createRequest;
    private SupplierResponse response1;

    @BeforeEach
    void setUp() {
        entity1 = Supplier.builder()
                .id(1L)
                .name("Công ty TNHH ABC")
                .contactInfo("123 Đường XYZ - Tel: 028-123-4567")
                .build();

        createRequest = new SupplierCreateRequest();
        createRequest.setName("Nhà cung cấp mới");
        createRequest.setContactInfo("789 Đường MNO - Email: info@supplier.com");

        response1 = SupplierResponse.builder()
                .id(1L)
                .name("Công ty TNHH ABC")
                .contactInfo("123 Đường XYZ - Tel: 028-123-4567")
                .build();
    }

    @Test
    @DisplayName("Should create supplier successfully")
    void create_shouldReturnMappedResponse() {
        // arrange
        Supplier toSave = Supplier.builder()
                .name("Nhà cung cấp mới")
                .contactInfo("789 Đường MNO - Email: info@supplier.com")
                .build();

        Supplier saved = Supplier.builder()
                .id(99L)
                .name("Nhà cung cấp mới")
                .contactInfo("789 Đường MNO - Email: info@supplier.com")
                .build();

        SupplierResponse expectedResponse = SupplierResponse.builder()
                .id(99L)
                .name("Nhà cung cấp mới")
                .contactInfo("789 Đường MNO - Email: info@supplier.com")
                .build();

        given(supplierMapper.toEntity(createRequest)).willReturn(toSave);
        given(supplierRepository.save(toSave)).willReturn(saved);
        given(supplierMapper.toResponse(saved)).willReturn(expectedResponse);

        // act
        SupplierResponse result = supplierService.create(createRequest);

        // assert
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getName()).isEqualTo("Nhà cung cấp mới");
        verify(supplierMapper).toEntity(createRequest);
        verify(supplierRepository).save(toSave);
        verify(supplierMapper).toResponse(saved);
    }

    @Test
    @DisplayName("Should get supplier by ID successfully")
    void getById_shouldReturnSupplierWhenExists() {
        // arrange
        given(supplierRepository.findById(1L)).willReturn(Optional.of(entity1));
        given(supplierMapper.toResponse(entity1)).willReturn(response1);

        // act
        SupplierResponse result = supplierService.getById(1L);

        // assert
        assertThat(result).isEqualTo(response1);
        verify(supplierRepository).findById(1L);
        verify(supplierMapper).toResponse(entity1);
    }

    @Test
    @DisplayName("Should throw exception when supplier not found by ID")
    void getById_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(supplierRepository.findById(999L)).willReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> supplierService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy nhà cung cấp với ID: 999");

        verify(supplierRepository).findById(999L);
        verifyNoInteractions(supplierMapper);
    }

    @Test
    @DisplayName("Should delete supplier successfully")
    void delete_shouldCallRepositoryDelete() {
        // arrange
        given(supplierRepository.existsById(1L)).willReturn(true);

        // act
        supplierService.delete(1L);

        // assert
        verify(supplierRepository).existsById(1L);
        verify(supplierRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent supplier")
    void delete_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(supplierRepository.existsById(999L)).willReturn(false);

        // act & assert
        assertThatThrownBy(() -> supplierService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy nhà cung cấp với ID: 999");

        verify(supplierRepository).existsById(999L);
        verify(supplierRepository, never()).deleteById(anyLong());
    }
}
