package com.develop.orders_microservice.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseProductDto {
    private Long productId;
    private Integer quantity;
    private Double total;
}