package com.foodDelivery.project.domen.dto;

import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.PaymentMethod;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class OrderDTO {

    @Positive
    private int totalAmount;

    @PositiveOrZero
    private int deliveryFee;

    @NotNull
    private OrderStatus status;

    @Size(max = 500)
    private String comment;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdAt;

    @FutureOrPresent
    private LocalDateTime deliveredAt;

    @NotNull
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
