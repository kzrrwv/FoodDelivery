package com.foodDelivery.project.domen.dto;

public class ProductAndAmount {
    private Long id;

    private int amount;

    public ProductAndAmount(Long id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public ProductAndAmount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
