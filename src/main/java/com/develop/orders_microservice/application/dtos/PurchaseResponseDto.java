package com.develop.orders_microservice.application.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseResponseDto {
    private Long orderId; // Cambiado de Integer a Long para coincidir con la entidad
    private Long userId; // Cambiado de Long a Integer para coincidir con la entidad
    private String deliveryAddress;
    private Integer paymentTypeId; // Cambiado de Long a Integer
    private Integer paymentStatusId; // Cambiado de Long a Integer
    private long deliveryId;
    private String deliveryName;
    private BigDecimal total;
}
