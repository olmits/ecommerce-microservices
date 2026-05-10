package com.commerce.inventory_service.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Setter @Getter
public class Inventory {

    @Id
    @Column(name = "product_id")
    private Long productId;
    private Integer stockQuantity;
}
