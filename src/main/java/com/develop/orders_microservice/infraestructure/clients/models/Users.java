package com.develop.orders_microservice.infraestructure.clients.models;

import lombok.Data;

@Data
public class Users {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String address;
    private boolean enabled;
    private Integer roleId;
}
