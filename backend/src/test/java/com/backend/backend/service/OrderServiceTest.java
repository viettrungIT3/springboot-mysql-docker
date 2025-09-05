package com.backend.backend.service;

import com.backend.backend.dto.customer.CustomerResponse;
import com.backend.backend.dto.order.OrderCreateRequest;
import com.backend.backend.dto.order.OrderResponse;
import com.backend.backend.entity.Customer;
import com.backend.backend.entity.Order;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.OrderMapper;
import com.backend.backend.repository.CustomerRepository;
import com.backend.backend.repository.OrderRepository;
import com.backend.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Order entity1;
    private Customer customer;
    private OrderCreateRequest createRequest;
    private OrderResponse response1;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("Nguyễn Văn A")
                .contactInfo("Email: a@example.com")
                .build();

        entity1 = Order.builder()
                .id(1L)
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(BigDecimal.valueOf(1500.00))
                .items(new ArrayList<>())
                .build();

        createRequest = new OrderCreateRequest();
        createRequest.setCustomerId(1L);

        response1 = OrderResponse.builder()
                .id(1L)
                .customer(CustomerResponse.builder().id(1L).name("Nguyễn Văn A").build())
                .orderDate(entity1.getOrderDate())
                .totalAmount(BigDecimal.valueOf(1500.00))
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should create order successfully")
    void create_shouldReturnMappedResponse() {
        // arrange
        Order toSave = Order.builder()
                .customer(customer)
                .orderDate(null) // will be set in service
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();

        Order saved = Order.builder()
                .id(99L)
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        OrderResponse expectedResponse = OrderResponse.builder()
                .id(99L)
                .customer(CustomerResponse.builder().id(1L).name("Nguyễn Văn A").build())
                .orderDate(saved.getOrderDate())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        given(orderMapper.toEntity(createRequest)).willReturn(toSave);
        given(orderRepository.save(any(Order.class))).willReturn(saved);
        given(orderMapper.toResponse(saved)).willReturn(expectedResponse);

        // act
        OrderResponse result = orderService.create(createRequest);

        // assert
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        verify(orderMapper).toEntity(createRequest);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toResponse(saved);
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void getById_shouldReturnOrderWhenExists() {
        // arrange
        given(orderRepository.findById(1L)).willReturn(Optional.of(entity1));
        given(orderMapper.toResponse(entity1)).willReturn(response1);

        // act
        OrderResponse result = orderService.getById(1L);

        // assert
        assertThat(result).isEqualTo(response1);
        verify(orderRepository).findById(1L);
        verify(orderMapper).toResponse(entity1);
    }

    @Test
    @DisplayName("Should throw exception when order not found by ID")
    void getById_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> orderService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy đơn hàng với ID: 999");

        verify(orderRepository).findById(999L);
        verifyNoInteractions(orderMapper);
    }

    @Test
    @DisplayName("Should delete order successfully")
    void delete_shouldCallRepositoryDelete() {
        // arrange
        given(orderRepository.existsById(1L)).willReturn(true);

        // act
        orderService.delete(1L);

        // assert
        verify(orderRepository).existsById(1L);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent order")
    void delete_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(orderRepository.existsById(999L)).willReturn(false);

        // act & assert
        assertThatThrownBy(() -> orderService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy đơn hàng với ID: 999");

        verify(orderRepository).existsById(999L);
        verify(orderRepository, never()).deleteById(anyLong());
    }
}
