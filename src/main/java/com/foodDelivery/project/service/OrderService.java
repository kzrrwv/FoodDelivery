package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderService {

    List<OrderToRetrieve> getOrders();

    void createOrder(OrderDTO orderDTO);

    List<OrderToRetrieve> findOrdersWithPageable(PageRequest of);

    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);

    OrderToRetrieve getOrderById(Long id);

    List<OrderToRetrieve> getAllOrdersForAdmin(PageRequest pageRequest);

    void updateOrderStatus(Long id, OrderStatus status, String comment);
}
