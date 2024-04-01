package com.foodDelivery.project.repository;

import com.foodDelivery.project.domen.model.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
            SELECT o
            FROM Order o
            WHERE o.user_id.id = :userId
            ORDER BY o.createdAt DESC
            """)
    List<Order> findOrdersByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT o
            FROM Order o
            WHERE o.user_id.id = :userId
            """)
    Page<Order> findOrdersByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
