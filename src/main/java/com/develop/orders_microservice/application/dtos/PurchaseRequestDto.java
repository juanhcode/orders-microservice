package com.develop.orders_microservice.application.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDto {
    @NotNull(message = "User ID cannot be null")
    private Integer userId; // Cambiado de Long a Integer para coincidir con la entidad

    @NotNull(message = "Delivery address cannot be null")
    private String deliveryAddress;

    @NotNull(message = "Payment type ID cannot be null")
    private Integer paymentTypeId; // Cambiado de Long a Integer

    @NotNull(message = "Payment status ID cannot be null")
    private Integer paymentStatusId; // Cambiado de Long a Integer

    private String deliveryName;

    @NotEmpty(message = "Products list cannot be empty")
    private List<PurchaseProductDto> products;
}