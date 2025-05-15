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
    @NotNull
    private Integer userId;

    @Column(name = "delivery_address")
    @NotNull
    @NotBlank
    private String deliveryAddress;

    @Column(name = "payment_type_id")
    @NotNull
    private Integer paymentTypeId;

    @Column(name = "payment_status_id")
    @NotNull
    private Integer paymentStatusId;

    @NotNull
    private BigDecimal total;
}
