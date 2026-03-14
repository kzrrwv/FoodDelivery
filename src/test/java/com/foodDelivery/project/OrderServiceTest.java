package com.foodDelivery.project;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.PaymentMethod;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.OrderRepository;
import com.foodDelivery.project.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_shouldSaveOrder() {

        OrderDTO dto = new OrderDTO();
        dto = mock(OrderDTO.class);

        when(dto.getTotalAmount()).thenReturn(100);
        when(dto.getDeliveryFee()).thenReturn(10);
        when(dto.getStatus()).thenReturn(OrderStatus.ON_THE_WAY);
        when(dto.getComment()).thenReturn("test");
        when(dto.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(dto.getDeliveredAt()).thenReturn(LocalDateTime.now());
        when(dto.getPaymentMethod()).thenReturn(PaymentMethod.CARD);

        service.createOrder(dto);

        verify(repository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrders_shouldReturnList() {

        Order order = new Order();
        order.setTotalAmount(100);
        order.setDeliveryFee(5);
        order.setStatus(OrderStatus.ON_THE_WAY);
        order.setComment("hello");

        when(repository.findAll()).thenReturn(List.of(order));

        List<OrderToRetrieve> result = service.getOrders();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getTotalAmount());
    }

    @Test
    void deleteOrder_shouldDelete() {

        Order order = new Order();
        order.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(order));

        service.deleteOrder(1);

        verify(repository).delete(order);
    }

    @Test
    void deleteOrder_shouldThrowException() {

        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> service.deleteOrder(1)
        );
    }
}
