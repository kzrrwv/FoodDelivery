package com.foodDelivery.project.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.foodDelivery.project.controller.OrderController;
import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.PaymentMethod;
import com.foodDelivery.project.service.OrderService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;

    @Autowired
    ObjectMapper objectMapper;

    private OrderDTO validOrder() {
        OrderDTO dto = new OrderDTO();
        dto.setTotalAmount(100);
        dto.setDeliveryFee(20);
        dto.setStatus(OrderStatus.CONFIRMED);
        dto.setComment("test");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setDeliveredAt(LocalDateTime.now().plusHours(1));
        dto.setPaymentMethod(PaymentMethod.CARD);
        return dto;
    }

    @Test
    void addOrder_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder())))
                .andExpect(status().isCreated());
    }

    @Test
    void getOrders_shouldReturnOk() throws Exception {
        when(orderService.findOrdersWithPageable(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk());
    }

    @Test
    void updateOrder_shouldReturnOk() throws Exception {
        when(orderService.updateOrder(anyLong(), any()))
                .thenReturn(validOrder());

        mockMvc.perform(put("/order/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder())))
                .andExpect(status().isOk());
    }
}