package com.foodDelivery.project.domen.dto;

import com.foodDelivery.project.domen.model.OrderItem;
import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

@Schema(title = "endpoint для заказов")
@Tag(name = "OrderDTO", description = "")
public class OrderDTO {
    //добавить два поля из reviewDTO
    @Positive
    private int totalAmount;

    @PositiveOrZero
    private int deliveryFee;

    @NotNull
    private OrderStatus status;

    @Size(max = 500)
    private String comment;

    @Positive
    private int rating;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdAt;

    @FutureOrPresent
    private LocalDateTime deliveredAt;

    @NotNull
    private PaymentMethod paymentMethod;

    private List<OrderItem> orderItems;

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

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
