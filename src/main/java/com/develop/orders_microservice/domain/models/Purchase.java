package com.develop.orders_microservice.domain.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @NotNull
    private Users userId;

    @Column(name = "delivery_address")
    @NotNull
    @NotBlank
    private String deliveryAddress;

    @ManyToOne()
    @JoinColumn(name = "payment_type_id")
    @NotNull
    private PaymentType paymentTypeId;

    @ManyToOne()
    @JoinColumn(name = "payment_status_id")
    @NotNull
    private PaymentStatus paymentStatusId;

    @NotNull
    private BigDecimal total;
}
