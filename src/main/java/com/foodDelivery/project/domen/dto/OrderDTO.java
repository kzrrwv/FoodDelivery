package com.foodDelivery.project.domen.dto;

import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.PaymentMethod;

import java.time.LocalDateTime;

public class OrderDTO {

    private int totalAmount;

    private int deliveryFee;

    private OrderStatus status;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    private PaymentMethod paymentMethod;

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
