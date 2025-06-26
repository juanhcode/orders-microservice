package com.develop.orders_microservice.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "purchase_product")
@IdClass(PurchaseProductId.class) // Para clave compuesta
public class PurchaseProduct {
    @Id
    @Column(name = "purchase_id")
    private Long purchaseId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @NotNull
    private Integer quantity;

    @NotNull
    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;
}