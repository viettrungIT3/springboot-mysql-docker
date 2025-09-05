package com.backend.backend.service;

import com.backend.backend.dto.orderitem.OrderItemCreateRequest;
import com.backend.backend.dto.orderitem.OrderItemResponse;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.entity.Order;
import com.backend.backend.entity.OrderItem;
import com.backend.backend.entity.Product;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.OrderItemMapper;
import com.backend.backend.repository.OrderItemRepository;
import com.backend.backend.repository.OrderRepository;
import com.backend.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemService Unit Tests")
class OrderItemServiceTest {

    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItem entity1;
    private Order order;
    private Product product;
    private OrderItemCreateRequest createRequest;
    private OrderItemResponse response1;

    @BeforeEach
    void setUp() {
        order = Order.builder().id(1L).build();
        product = Product.builder().id(1L).name("Test Product").price(BigDecimal.valueOf(100)).build();

        entity1 = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .build();

        createRequest = new OrderItemCreateRequest();
        createRequest.setOrderId(1L);
        createRequest.setProductId(1L);
        createRequest.setQuantity(2);
        createRequest.setPrice(BigDecimal.valueOf(100));

        response1 = OrderItemResponse.builder()
                .id(1L)
                .orderId(1L)
                .product(ProductResponse.builder().id(1L).name("Test Product").build())
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @DisplayName("Should create order item successfully")
    void create_shouldReturnMappedResponse() {
        // arrange
        OrderItem toSave = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .build();

        OrderItem saved = OrderItem.builder()
                .id(99L)
                .order(order)
                .product(product)
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .build();

        OrderItemResponse expectedResponse = OrderItemResponse.builder()
                .id(99L)
                .orderId(1L)
                .product(ProductResponse.builder().id(1L).name("Test Product").build())
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .build();

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(orderItemMapper.toEntity(createRequest)).willReturn(toSave);
        given(orderItemRepository.save(any(OrderItem.class))).willReturn(saved);
        given(orderItemMapper.toResponse(saved)).willReturn(expectedResponse);

        // act
        OrderItemResponse result = orderItemService.create(createRequest);

        // assert
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getQuantity()).isEqualTo(2);
        verify(orderItemMapper).toEntity(createRequest);
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(orderItemMapper).toResponse(saved);
    }

    @Test
    @DisplayName("Should get order item by ID successfully")
    void getById_shouldReturnOrderItemWhenExists() {
        // arrange
        given(orderItemRepository.findById(1L)).willReturn(Optional.of(entity1));
        given(orderItemMapper.toResponse(entity1)).willReturn(response1);

        // act
        OrderItemResponse result = orderItemService.getById(1L);

        // assert
        assertThat(result).isEqualTo(response1);
        verify(orderItemRepository).findById(1L);
        verify(orderItemMapper).toResponse(entity1);
    }

    @Test
    @DisplayName("Should throw exception when order item not found by ID")
    void getById_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(orderItemRepository.findById(999L)).willReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> orderItemService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy mặt hàng trong đơn với ID: 999");

        verify(orderItemRepository).findById(999L);
        verifyNoInteractions(orderItemMapper);
    }

    @Test
    @DisplayName("Should delete order item successfully")
    void delete_shouldCallRepositoryDelete() {
        // arrange
        given(orderItemRepository.findById(1L)).willReturn(Optional.of(entity1));

        // act
        orderItemService.delete(1L);

        // assert
        verify(orderItemRepository).findById(1L);
        verify(orderItemRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent order item")
    void delete_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(orderItemRepository.findById(999L)).willReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> orderItemService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy mặt hàng trong đơn với ID: 999");

        verify(orderItemRepository).findById(999L);
        verify(orderItemRepository, never()).deleteById(anyLong());
    }
}
