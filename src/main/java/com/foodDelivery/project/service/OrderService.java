package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;

import java.util.List;

public interface OrderService {

    List<OrderToRetrieve> getOrders();

    void saveOrder(OrderDTO orderDTO);
}
