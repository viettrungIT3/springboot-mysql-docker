package com.backend.backend.repository;

import com.backend.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    
    // Business logic methods
    List<Order> findByOrderDateBetween(OffsetDateTime startDate, OffsetDateTime endDate);
    List<Order> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
}
