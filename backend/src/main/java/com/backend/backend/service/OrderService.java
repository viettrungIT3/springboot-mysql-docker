package com.backend.backend.service;

import com.backend.backend.config.CacheNames;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.order.OrderCreateRequest;
import com.backend.backend.dto.order.OrderResponse;
import com.backend.backend.dto.order.OrderUpdateRequest;
import com.backend.backend.entity.Order;
import com.backend.backend.entity.OrderItem;
import com.backend.backend.entity.Product;
import com.backend.backend.shared.domain.exception.OrderException;
import com.backend.backend.shared.domain.exception.CustomerException;
import com.backend.backend.shared.domain.exception.ProductException;
import com.backend.backend.mapper.OrderMapper;
import com.backend.backend.repository.CustomerRepository;
import com.backend.backend.repository.OrderRepository;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.util.PageMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.ORDER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_ID, key = "#result.id", condition = "#result != null"),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_CUSTOMER, key = "#request.customerId", condition = "#request.customerId != null")
    })
    public OrderResponse create(OrderCreateRequest request) {
        // Validate customer exists
        // Customer customer = customerRepository.findById(request.getCustomerId())
        // .orElseThrow(() -> new ResourceNotFoundException(
        // "Không tìm thấy khách hàng với ID: " + request.getCustomerId()));

        Order entity = orderMapper.toEntity(request);

        // Set order date if not provided
        if (entity.getOrderDate() == null) {
            entity.setOrderDate(OffsetDateTime.now());
        }

        // Initialize items list and total
        entity.setItems(new ArrayList<>());
        entity.setTotalAmount(BigDecimal.ZERO);

        // Save order first
        Order savedOrder = orderRepository.save(entity);

        // Process items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderCreateRequest.OrderItemCreateRequest itemRequest : request.getItems()) {
                // Validate product
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> ProductException.notFound(itemRequest.getProductId()));

                // Check stock
                if (product.getQuantityInStock() < itemRequest.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ hàng trong kho cho sản phẩm: " + product.getName() +
                            ". Còn lại: " + product.getQuantityInStock());
                }

                // Create order item
                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .quantity(itemRequest.getQuantity())
                        .price(product.getPrice()) // Use current product price
                        .build();

                savedOrder.getItems().add(orderItem);

                // Update product stock
                product.setQuantityInStock(product.getQuantityInStock() - itemRequest.getQuantity());
                productRepository.save(product);

                // Calculate total
                totalAmount = totalAmount
                        .add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            }

            // Update total amount
            savedOrder.setTotalAmount(totalAmount);
            savedOrder = orderRepository.save(savedOrder);
        }

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.ORDER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_ID, key = "#id"),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_CUSTOMER, allEntries = true) // Evict by customer if customer changes
    })
    public OrderResponse update(Long id, OrderUpdateRequest request) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> OrderException.notFound(id));

        // Validate new customer if being changed
        if (request.getCustomerId() != null && !request.getCustomerId().equals(entity.getCustomer().getId())) {
            customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> CustomerException.notFound(request.getCustomerId()));
        }

        orderMapper.updateEntity(entity, request); // partial update
        Order saved = orderRepository.save(entity);
        return orderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ORDER_BY_ID, key = "#id")
    public OrderResponse getById(Long id) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> OrderException.notFound(id));
        return orderMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(
        cacheNames = CacheNames.ORDER_LIST,
        key = "T(java.util.Objects).hash(#page,#size,#sort)"
    )
    public PageResponse<OrderResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("orderDate").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Order> result = orderRepository.findAll(pageable);

        return PageMapper.toPageResponse(result, orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ORDER_BY_CUSTOMER, key = "#customerId")
    public List<OrderResponse> findByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw CustomerException.notFound(customerId);
        }

        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.ORDER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_ID, key = "#id"),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_CUSTOMER, key = "#entity.customer.id", condition = "#entity != null")
    })
    public void delete(Long id) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> OrderException.notFound(id));

        // Revert stock for all items
        for (OrderItem item : entity.getItems()) {
            Product product = item.getProduct();
            product.setQuantityInStock(product.getQuantityInStock() + item.getQuantity());
            productRepository.save(product);
        }

        entity.delete();
        orderRepository.save(entity);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.ORDER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_ID, key = "#orderId"),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_CUSTOMER, allEntries = true) // Evict by customer if order changes
    })
    public OrderResponse addItem(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> OrderException.notFound(orderId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ProductException.notFound(productId));

        if (product.getQuantityInStock() < quantity) {
            throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + product.getQuantityInStock());
        }

        // Check if product already in order
        OrderItem existingItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update existing item
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item
            OrderItem newItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            order.getItems().add(newItem);
        }

        // Update stock
        product.setQuantityInStock(product.getQuantityInStock() - quantity);
        productRepository.save(product);

        // Recalculate total
        BigDecimal total = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }
    
    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * Calculate order total amount
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> OrderException.notFound(orderId));
        
        return order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get order statistics for a customer
     */
    @Transactional(readOnly = true)
    public CustomerOrderStats getCustomerOrderStats(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw CustomerException.notFound(customerId);
        }
        
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        
        long totalOrders = orders.size();
        BigDecimal totalSpent = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageOrderValue = totalOrders > 0 
                ? totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        
        return CustomerOrderStats.builder()
                .customerId(customerId)
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .averageOrderValue(averageOrderValue)
                .build();
    }
    
    /**
     * Get order statistics for all orders
     */
    @Transactional(readOnly = true)
    public OrderStats getOrderStats() {
        List<Order> allOrders = orderRepository.findAll();
        
        long totalOrders = allOrders.size();
        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageOrderValue = totalOrders > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        
        // Count orders by date range (last 30 days)
        OffsetDateTime thirtyDaysAgo = OffsetDateTime.now().minusDays(30);
        long recentOrders = allOrders.stream()
                .filter(order -> order.getOrderDate() != null && order.getOrderDate().isAfter(thirtyDaysAgo))
                .count();
        
        return OrderStats.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .recentOrders(recentOrders)
                .build();
    }
    
    /**
     * Find orders by date range
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> findOrdersByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }
    
    /**
     * Find orders by total amount range
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> findOrdersByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        List<Order> orders = orderRepository.findByTotalAmountBetween(minAmount, maxAmount);
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }
    
    /**
     * Remove item from order
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.ORDER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_ID, key = "#orderId"),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_CUSTOMER, allEntries = true)
    })
    public OrderResponse removeItem(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> OrderException.notFound(orderId));
        
        OrderItem itemToRemove = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> OrderItemException.notFoundInOrder(orderId, productId));
        
        // Release stock
        Product product = itemToRemove.getProduct();
        product.setQuantityInStock(product.getQuantityInStock() + itemToRemove.getQuantity());
        productRepository.save(product);
        
        // Remove item
        order.getItems().remove(itemToRemove);
        
        // Recalculate total
        BigDecimal total = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        
        Order saved = orderRepository.save(order);
        log.info("Removed product {} from order {} (ID: {})", product.getName(), orderId, productId);
        return orderMapper.toResponse(saved);
    }
    
    /**
     * Update item quantity in order
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.ORDER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_ID, key = "#orderId"),
        @CacheEvict(cacheNames = CacheNames.ORDER_BY_CUSTOMER, allEntries = true)
    })
    public OrderResponse updateItemQuantity(Long orderId, Long productId, Integer newQuantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> OrderException.notFound(orderId));
        
        OrderItem item = order.getItems().stream()
                .filter(orderItem -> orderItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> OrderItemException.notFoundInOrder(orderId, productId));
        
        Product product = item.getProduct();
        Integer oldQuantity = item.getQuantity();
        Integer quantityDifference = newQuantity - oldQuantity;
        
        // Check stock availability
        if (product.getQuantityInStock() < quantityDifference) {
            throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + product.getQuantityInStock() + 
                    ", yêu cầu thêm: " + quantityDifference);
        }
        
        // Update stock
        product.setQuantityInStock(product.getQuantityInStock() - quantityDifference);
        productRepository.save(product);
        
        // Update item quantity
        item.setQuantity(newQuantity);
        
        // Recalculate total
        BigDecimal total = order.getItems().stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        
        Order saved = orderRepository.save(order);
        log.info("Updated quantity for product {} in order {} from {} to {}", 
                product.getName(), orderId, oldQuantity, newQuantity);
        return orderMapper.toResponse(saved);
    }
    
    /**
     * Validate order before creation
     */
    @Transactional(readOnly = true)
    public void validateOrderData(OrderCreateRequest request) {
        if (request.getCustomerId() != null && !customerRepository.existsById(request.getCustomerId())) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + request.getCustomerId());
        }
        
        if (request.getItems() != null) {
            for (OrderCreateRequest.OrderItemCreateRequest item : request.getItems()) {
                if (!productRepository.existsById(item.getProductId())) {
                    throw new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + item.getProductId());
                }
                
                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
                }
            }
        }
    }
    
    // ==================== INNER CLASSES ====================
    
    @lombok.Data
    @lombok.Builder
    public static class CustomerOrderStats {
        private Long customerId;
        private long totalOrders;
        private BigDecimal totalSpent;
        private BigDecimal averageOrderValue;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class OrderStats {
        private long totalOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
        private long recentOrders;
    }
}
