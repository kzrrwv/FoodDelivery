package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderService {

    List<OrderToRetrieve> getOrders();

    void createOrder(OrderDTO orderDTO);

    List<OrderToRetrieve> findOrdersWithPageable(PageRequest of);

    OrderDTO updateOrder(int id, OrderDTO orderDTO);

    void deleteOrder(int id);
}
