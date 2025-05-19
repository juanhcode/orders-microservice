package com.develop.orders_microservice.infraestructure.clients;

import com.develop.orders_microservice.infraestructure.clients.models.Users;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "users-microservice", url = "${feign.client.url2}")
public interface UsersClientRest {
    @GetMapping("/users/{id}")
    Optional<Users> getUser(@PathVariable Integer id);
}
