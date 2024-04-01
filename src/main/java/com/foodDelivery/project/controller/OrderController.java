package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Tag(name = "Orders", description = "Работа с заказами")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Создать заказ")
    @ApiResponse(responseCode = "201", description = "Заказ создан")
    public ResponseEntity<Void> addOrder(@RequestBody @Valid OrderDTO orderDTO){
        orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Получить заказы пользователя")
    public ResponseEntity<List<OrderToRetrieve>> getOrders(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size){

        return ResponseEntity.ok(
                orderService.findOrdersWithPageable(PageRequest.of(page, size))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID")
    public ResponseEntity<OrderToRetrieve> getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить заказ")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody @Valid OrderDTO orderDTO){

        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
