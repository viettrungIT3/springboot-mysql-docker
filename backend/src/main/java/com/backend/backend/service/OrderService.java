package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.order.OrderCreateRequest;
import com.backend.backend.dto.order.OrderResponse;
import com.backend.backend.dto.order.OrderUpdateRequest;
import com.backend.backend.entity.Order;
import com.backend.backend.entity.OrderItem;
import com.backend.backend.entity.Product;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.OrderMapper;
import com.backend.backend.repository.CustomerRepository;
import com.backend.backend.repository.OrderRepository;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.util.PageMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
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
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Không tìm thấy sản phẩm với ID: " + itemRequest.getProductId()));

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
    public OrderResponse update(Long id, OrderUpdateRequest request) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        // Validate new customer if being changed
        if (request.getCustomerId() != null && !request.getCustomerId().equals(entity.getCustomer().getId())) {
            customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy khách hàng với ID: " + request.getCustomerId()));
        }

        orderMapper.updateEntity(entity, request); // partial update
        Order saved = orderRepository.save(entity);
        return orderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));
        return orderMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("orderDate").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Order> result = orderRepository.findAll(pageable);

        return PageMapper.toPageResponse(result, orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + customerId);
        }

        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        // Revert stock for all items
        for (OrderItem item : entity.getItems()) {
            Product product = item.getProduct();
            product.setQuantityInStock(product.getQuantityInStock() + item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderResponse addItem(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

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
}
