package com.incubyte.backend.dto;

import java.math.BigDecimal;

public class VehicleResponse {
    private Long id;
    private String make;
    private String model;
    private String category;
    private BigDecimal price;
    private Integer quantity;

    public VehicleResponse() {
    }

    public VehicleResponse(Long id, String make, String model, String category, BigDecimal price, Integer quantity) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
