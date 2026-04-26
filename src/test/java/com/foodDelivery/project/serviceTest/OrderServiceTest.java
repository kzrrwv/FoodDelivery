package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.repository.OrderRepository;
import com.foodDelivery.project.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderServiceImpl service;

    @Test
    void createOrder_shouldSaveOrder() {
        OrderDTO dto = new OrderDTO();
        dto.setTotalAmount(100);
        dto.setDeliveryFee(20);

        service.createOrder(dto);

        verify(repository).save(any(Order.class));
    }

    @Test
    void getOrders_shouldMapCorrectly() {
        Order order = new Order();
        order.setTotalAmount(150);
        order.setDeliveryFee(30);
        order.setComment("test");

        when(repository.findAll()).thenReturn(List.of(order));

        List<OrderToRetrieve> result = service.getOrders();

        assertEquals(1, result.size());
        assertEquals(150, result.get(0).getTotalAmount());
        assertEquals("test", result.get(0).getComment());
    }

    @Test
    void pageable_shouldReturnData() {
        Order order = new Order();
        order.setTotalAmount(200);

        Page<Order> page = new PageImpl<>(List.of(order));

        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        List<OrderToRetrieve> result =
                service.findOrdersWithPageable(PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void updateOrder_shouldChangeValues() {
        Order order = new Order();
        order.setTotalAmount(100);

        when(repository.findById(1L)).thenReturn(Optional.of(order));
        when(repository.save(any())).thenReturn(order);

        OrderDTO dto = new OrderDTO();
        dto.setTotalAmount(999);
        dto.setDeliveryFee(50);

        OrderDTO result = service.updateOrder(1L, dto);

        assertEquals(999, result.getTotalAmount());
        assertEquals(50, result.getDeliveryFee());
    }

    @Test
    void deleteOrder_shouldCallDelete() {
        Order order = new Order();

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        service.deleteOrder(1L);

        verify(repository).delete(order);
    }
}