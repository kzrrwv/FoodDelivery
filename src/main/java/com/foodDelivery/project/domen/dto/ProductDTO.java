package com.foodDelivery.project.domen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Positive
    private int price;

    @Positive
    private int amount;

    private int warehouse_id;

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public int getWarehouse_id() {
        return warehouse_id;
    }
}
