package com.foodDelivery.project.domen.responce;

import com.foodDelivery.project.domen.model.enums.OrderStatus;

public class OrderToRetrieve {

    private int totalAmount;

    private int deliveryFee;

    private OrderStatus status;

    private String comment;

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
}
