package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderToRetrieve>> getOrders(){
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable int id){
        return ResponseEntity.ok(new OrderDTO());
    }

    @PostMapping
    public ResponseEntity<Void> addOrder(@RequestBody OrderDTO orderDTO){
        orderService.saveOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
