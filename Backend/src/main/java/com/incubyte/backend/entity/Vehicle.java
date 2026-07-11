package com.incubyte.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private String category;
    private BigDecimal price;
    private Integer quantity;

    public Vehicle() {
    }

    public Vehicle(Long id, String make, String model, String category, BigDecimal price, Integer quantity) {
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

    public static VehicleBuilder builder() {
        return new VehicleBuilder();
    }

    public static class VehicleBuilder {
        private Long id;
        private String make;
        private String model;
        private String category;
        private BigDecimal price;
        private Integer quantity;

        VehicleBuilder() {
        }

        public VehicleBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public VehicleBuilder make(String make) {
            this.make = make;
            return this;
        }

        public VehicleBuilder model(String model) {
            this.model = model;
            return this;
        }

        public VehicleBuilder category(String category) {
            this.category = category;
            return this;
        }

        public VehicleBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public VehicleBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Vehicle build() {
            return new Vehicle(id, make, model, category, price, quantity);
        }
    }
}
