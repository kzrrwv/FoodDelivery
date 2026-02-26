package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.OrderRepository;
import com.foodDelivery.project.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository repository;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OrderToRetrieve> getOrders(){
        List<Order> all = repository.findAll();

        if (all.isEmpty()) {
            log.debug("База данных пустая!");
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
        Order order = new Order();
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setStatus(orderDTO.getStatus());
        order.setComment(orderDTO.getComment());
        order.setCreatedAt(orderDTO.getCreatedAt());
        order.setDeliveredAt(orderDTO.getDeliveredAt());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        repository.save(order);
        log.info("Заказ успешно добавлен.");
    }

    @Override
    public List<OrderToRetrieve> findOrdersWithPageable(PageRequest of) {
        Page<Order> all = repository.findAll(of);
        List<Order> content = all.getContent();
        return null;
    }
}
