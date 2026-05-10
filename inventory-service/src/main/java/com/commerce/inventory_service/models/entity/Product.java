package com.commerce.inventory_service.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter @Setter
public class Product {

    @Id
    private Long id;

    private String name;

    // Types: "PERSIAN", "KILIM", "SHAG", "RUNNER", "OUTDOOR"
    private String type;
    private String primaryColor;

    private Integer lengthCm;
    private Integer widthCm;

    private BigDecimal price;
}
