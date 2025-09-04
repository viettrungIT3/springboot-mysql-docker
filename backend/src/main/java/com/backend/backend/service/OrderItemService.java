package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.orderitem.OrderItemCreateRequest;
import com.backend.backend.dto.orderitem.OrderItemResponse;
import com.backend.backend.dto.orderitem.OrderItemUpdateRequest;
import com.backend.backend.entity.Order;
import com.backend.backend.entity.OrderItem;
import com.backend.backend.entity.Product;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.OrderItemMapper;
import com.backend.backend.repository.OrderItemRepository;
import com.backend.backend.repository.OrderRepository;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.util.PageMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemService(OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Transactional
    public OrderItemResponse create(OrderItemCreateRequest request) {
        // Validate order exists
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + request.getOrderId()));

        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy sản phẩm với ID: " + request.getProductId()));

        // Check stock availability
        if (product.getQuantityInStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + product.getQuantityInStock());
        }

        OrderItem entity = orderItemMapper.toEntity(request);
        OrderItem saved = orderItemRepository.save(entity);

        // Update product stock
        product.setQuantityInStock(product.getQuantityInStock() - request.getQuantity());
        productRepository.save(product);

        // Recalculate order total
        recalculateOrderTotal(order);

        return orderItemMapper.toResponse(saved);
    }

    @Transactional
    public OrderItemResponse update(Long id, OrderItemUpdateRequest request) {
        OrderItem entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mặt hàng trong đơn với ID: " + id));

        Integer oldQuantity = entity.getQuantity();
        Product oldProduct = entity.getProduct();

        // Validate new order if being changed
        Order order = entity.getOrder();
        if (request.getOrderId() != null && !request.getOrderId().equals(entity.getOrder().getId())) {
            order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy đơn hàng với ID: " + request.getOrderId()));
        }

        // Validate new product if being changed
        Product product = oldProduct;
        if (request.getProductId() != null && !request.getProductId().equals(entity.getProduct().getId())) {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy sản phẩm với ID: " + request.getProductId()));
        }

        orderItemMapper.updateEntity(entity, request); // partial update
        OrderItem saved = orderItemRepository.save(entity);

        // Handle stock changes
        Integer newQuantity = saved.getQuantity();
        if (!oldProduct.getId().equals(product.getId())) {
            // Different product: revert old, deduct new
            oldProduct.setQuantityInStock(oldProduct.getQuantityInStock() + oldQuantity);
            productRepository.save(oldProduct);

            if (product.getQuantityInStock() < newQuantity) {
                throw new IllegalArgumentException(
                        "Không đủ hàng trong kho cho sản phẩm mới. Còn lại: " + product.getQuantityInStock());
            }
            product.setQuantityInStock(product.getQuantityInStock() - newQuantity);
            productRepository.save(product);
        } else if (!oldQuantity.equals(newQuantity)) {
            // Same product, different quantity
            int quantityDiff = newQuantity - oldQuantity;
            if (product.getQuantityInStock() < quantityDiff) {
                throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + product.getQuantityInStock());
            }
            product.setQuantityInStock(product.getQuantityInStock() - quantityDiff);
            productRepository.save(product);
        }

        // Recalculate order totals
        recalculateOrderTotal(entity.getOrder());
        if (!order.getId().equals(entity.getOrder().getId())) {
            recalculateOrderTotal(order);
        }

        return orderItemMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderItemResponse getById(Long id) {
        OrderItem entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mặt hàng trong đơn với ID: " + id));
        return orderItemMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> findAll() {
        return orderItemRepository.findAll().stream()
                .map(orderItemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderItemResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("id").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<OrderItem> result = orderItemRepository.findAll(pageable);

        return PageMapper.toPageResponse(result, orderItemMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        OrderItem entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mặt hàng trong đơn với ID: " + id));

        // Revert product stock
        Product product = entity.getProduct();
        product.setQuantityInStock(product.getQuantityInStock() + entity.getQuantity());
        productRepository.save(product);

        Order order = entity.getOrder();
        orderItemRepository.deleteById(id);

        // Recalculate order total
        recalculateOrderTotal(order);
    }

    private void recalculateOrderTotal(Order order) {
        BigDecimal total = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}
