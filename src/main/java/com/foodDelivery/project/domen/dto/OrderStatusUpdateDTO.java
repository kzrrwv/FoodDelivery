package com.foodDelivery.project.domen.dto;

import com.foodDelivery.project.domen.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateDTO {

    @NotNull
    private OrderStatus status;

    private String comment;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}