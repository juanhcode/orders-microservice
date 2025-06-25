package com.develop.orders_microservice.infraestructure.clients;

import com.develop.orders_microservice.infraestructure.clients.models.Delivery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "delivery-microservice", url = "${feign.client.url3}")
public interface DeliveryClientRest {

    @GetMapping("/deliveries/{id}")
    Delivery getDeliveryById(@PathVariable Long id);

    @PostMapping("/deliveries")
    Delivery createDelivery(@RequestBody Delivery delivery);
}
