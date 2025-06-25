package com.develop.orders_microservice.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PurchaseDto {
    private Integer orderId;
    private Integer userId;
    private String paymentStatusName;
    private String statusDeliveryName;
    private List<Integer> productIds;
}
