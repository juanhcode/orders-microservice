package com.develop.orders_microservice.domain.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Purchase {
    @Valid
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id")
    @NotNull(message = "User ID cannot be null")
    private Integer userId;

    @Column(name = "delivery_address")
    @NotNull(message = "Delivery address cannot be null")
    @NotBlank(message = "Delivery address cannot be blank")
    private String deliveryAddress;

    @Column(name = "payment_type_id")
    @NotNull(message = "Payment type ID cannot be null")
    private Integer paymentTypeId;

    @Column(name = "payment_status_id")
    @NotNull(message = "Payment status ID cannot be null")
    private Integer paymentStatusId;

    @NotNull(message = "Total cannot be null")
    private BigDecimal total;
}
