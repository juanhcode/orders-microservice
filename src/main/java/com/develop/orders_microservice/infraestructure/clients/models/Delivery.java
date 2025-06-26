package com.develop.orders_microservice.infraestructure.clients.models;

import lombok.Data;

@Data
public class Delivery {
    private Long id;
    private Boolean delivered;
    private Long statusId;
    private String status;
    private Long userId;
}
