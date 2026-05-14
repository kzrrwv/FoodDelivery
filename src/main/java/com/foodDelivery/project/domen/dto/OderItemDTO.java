package com.foodDelivery.project.domen.dto;

import jakarta.validation.constraints.Positive;

public class OderItemDTO {
    @Positive
    private int price;

    @Positive
    private int quantity;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
