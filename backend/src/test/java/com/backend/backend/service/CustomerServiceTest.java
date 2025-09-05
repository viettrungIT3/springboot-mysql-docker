package com.backend.backend.service;

import com.backend.backend.dto.customer.CustomerCreateRequest;
import com.backend.backend.dto.customer.CustomerResponse;
import com.backend.backend.entity.Customer;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.CustomerMapper;
import com.backend.backend.repository.CustomerRepository;
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
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer entity1;
    private CustomerCreateRequest createRequest;
    private CustomerResponse response1;

    @BeforeEach
    void setUp() {
        entity1 = Customer.builder()
                .id(1L)
                .name("Nguyễn Văn A")
                .contactInfo("Email: a@example.com, Phone: 0123456789")
                .build();

        createRequest = new CustomerCreateRequest();
        createRequest.setName("Lê Văn C");
        createRequest.setContactInfo("Email: c@example.com, Phone: 0111222333");

        response1 = CustomerResponse.builder()
                .id(1L)
                .name("Nguyễn Văn A")
                .contactInfo("Email: a@example.com, Phone: 0123456789")
                .build();
    }

    @Test
    @DisplayName("Should create customer successfully")
    void create_shouldReturnMappedResponse() {
        // arrange
        Customer toSave = Customer.builder()
                .name("Lê Văn C")
                .contactInfo("Email: c@example.com, Phone: 0111222333")
                .build();

        Customer saved = Customer.builder()
                .id(99L)
                .name("Lê Văn C")
                .contactInfo("Email: c@example.com, Phone: 0111222333")
                .build();

        CustomerResponse expectedResponse = CustomerResponse.builder()
                .id(99L)
                .name("Lê Văn C")
                .contactInfo("Email: c@example.com, Phone: 0111222333")
                .build();

        given(customerMapper.toEntity(createRequest)).willReturn(toSave);
        given(customerRepository.save(toSave)).willReturn(saved);
        given(customerMapper.toResponse(saved)).willReturn(expectedResponse);

        // act
        CustomerResponse result = customerService.create(createRequest);

        // assert
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getName()).isEqualTo("Lê Văn C");
        verify(customerMapper).toEntity(createRequest);
        verify(customerRepository).save(toSave);
        verify(customerMapper).toResponse(saved);
    }

    @Test
    @DisplayName("Should get customer by ID successfully")
    void getById_shouldReturnCustomerWhenExists() {
        // arrange
        given(customerRepository.findById(1L)).willReturn(Optional.of(entity1));
        given(customerMapper.toResponse(entity1)).willReturn(response1);

        // act
        CustomerResponse result = customerService.getById(1L);

        // assert
        assertThat(result).isEqualTo(response1);
        verify(customerRepository).findById(1L);
        verify(customerMapper).toResponse(entity1);
    }

    @Test
    @DisplayName("Should throw exception when customer not found by ID")
    void getById_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(customerRepository.findById(999L)).willReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> customerService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy khách hàng với ID: 999");

        verify(customerRepository).findById(999L);
        verifyNoInteractions(customerMapper);
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void delete_shouldCallRepositoryDelete() {
        // arrange
        given(customerRepository.existsById(1L)).willReturn(true);

        // act
        customerService.delete(1L);

        // assert
        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent customer")
    void delete_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(customerRepository.existsById(999L)).willReturn(false);

        // act & assert
        assertThatThrownBy(() -> customerService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy khách hàng với ID: 999");

        verify(customerRepository).existsById(999L);
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
