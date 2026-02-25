package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.OrderRepository;
import com.foodDelivery.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository repository;

    @Autowired
    public OrderServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OrderToRetrieve> getOrders(){
        List<Order> all = repository.findAll();

        if (all.isEmpty()) {
            throw new BusinessException(
                    "Список заказов пуст",
                    HttpStatus.NOT_FOUND
            );
        }

        List<OrderToRetrieve> orderToRetrieves = new ArrayList<>();

        for(Order order : all){
            OrderToRetrieve orderToRetrieve = new OrderToRetrieve();
            orderToRetrieve.setComment(order.getComment());
            orderToRetrieve.setStatus(order.getStatus());
            orderToRetrieve.setDeliveryFee(order.getDeliveryFee());
            orderToRetrieve.setTotalAmount(order.getTotalAmount());
            orderToRetrieves.add(orderToRetrieve);
        }
        return orderToRetrieves;
    }

    @Override
    public void saveOrder(OrderDTO orderDTO){
        if (orderDTO.getTotalAmount() <= 0) {
            throw new BusinessException(
                    "Сумма заказа должна быть больше 0",
                    HttpStatus.BAD_REQUEST
            );
        }

        Order order = new Order();
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setStatus(orderDTO.getStatus());
        order.setComment(orderDTO.getComment());
        order.setCreatedAt(orderDTO.getCreatedAt());
        order.setDeliveredAt(orderDTO.getDeliveredAt());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        repository.save(order);
    }
}
