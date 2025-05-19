package com.develop.orders_microservice.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseDto {
    private Integer OrderId;
    private Integer userId;
    private String paymentStatusName;
}
