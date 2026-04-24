package com.foodDelivery.project.domen.dto;

import com.foodDelivery.project.domen.model.Product;

import java.util.List;

public class WarehouseDTO {
    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
