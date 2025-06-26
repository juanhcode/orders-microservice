package com.develop.orders_microservice.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseProductDto {
    private Long productId;
    private Integer quantity;
    private Double total;
}