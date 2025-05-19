package com.develop.orders_microservice.infraestructure.clients;

import com.develop.orders_microservice.infraestructure.clients.models.PaymentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-status-microservice", url = "${feign.client.url1}")
public interface PaymentStatusClientRest {
    @GetMapping("/payment-status/{paymentStatusId}")
    PaymentStatus getPaymentStatusNameById(@PathVariable Integer paymentStatusId);
}