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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public void createOrder(OrderDTO orderDTO){
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
    //получить список заказов по пользователю поменять
    public List<OrderToRetrieve> getOrders(){
        List<Order> all = repository.findAll();
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
    public OrderToRetrieve getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Заказ не найден",
                        HttpStatus.NOT_FOUND
                ));

        OrderToRetrieve dto = new OrderToRetrieve();
        dto.setComment(order.getComment());
        dto.setStatus(order.getStatus());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setTotalAmount(order.getTotalAmount());

        return dto;
    }

    @Override
    //переделать заказы по пользователю поиск
    public List<OrderToRetrieve> findOrdersWithPageable(PageRequest of) {
        Page<Order> page = repository.findAll(of);

        List<OrderToRetrieve> result = new ArrayList<>();

        for (Order order : page.getContent()) {
            OrderToRetrieve dto = new OrderToRetrieve();
            dto.setComment(order.getComment());
            dto.setStatus(order.getStatus());
            dto.setDeliveryFee(order.getDeliveryFee());
            dto.setTotalAmount(order.getTotalAmount());
            result.add(dto);
        }

        return result;
    }

    @Override
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    //сделать проверку является ли заказ пользователя
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Заказ не найден",
                        HttpStatus.NOT_FOUND
                ));

        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setStatus(orderDTO.getStatus());
        order.setComment(orderDTO.getComment());

        Order saved = repository.save(order);

        OrderDTO dto = new OrderDTO();
        dto.setTotalAmount(saved.getTotalAmount());
        dto.setDeliveryFee(saved.getDeliveryFee());
        dto.setStatus(saved.getStatus());
        dto.setComment(saved.getComment());

        return dto;
    }

    @Override
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')'")
    public void deleteOrder(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Заказ не найден", HttpStatus.NOT_FOUND));

        repository.delete(order);

        log.info("Заказ с id {} успешно удален.", id);
    }
}
