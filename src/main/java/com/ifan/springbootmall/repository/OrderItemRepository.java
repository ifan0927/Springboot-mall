package com.ifan.springbootmall.repository;

import com.ifan.springbootmall.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
    @NonNull
    List<OrderItem> findByOrderId(@NonNull Long orderId);

    void  deleteByOrderId(@NonNull Long orderId);
}
