package com.ifan.springbootmall.repository;

import com.ifan.springbootmall.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @NonNull
    Page<Order> findAll(@NonNull Pageable pageable);

    @NonNull
    Page<Order> findByUserId(@NonNull Pageable pageable, @NonNull Long userId);

    @NonNull
    void deleteByOrderId(@NonNull Long orderId);

}
