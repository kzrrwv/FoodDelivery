package com.foodDelivery.project.repository;

import com.foodDelivery.project.domen.model.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = "SELECT * FROM orders WHERE user_id = :id ORDER BY createdAt DESC", nativeQuery = true)
    List<Order> getOrdersByUserIdOrderByCreatedAtDesc(@Param("id") int id);
}
