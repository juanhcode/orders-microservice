package com.develop.orders_microservice.presentation.controllers;

import com.develop.orders_microservice.domain.interfaces.PurchaseService;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.infraestructure.messaging.SnsService;
import com.develop.orders_microservice.presentation.exceptions.BadRequestException;
import com.develop.orders_microservice.presentation.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders/purchases")
public class PurchaseController {
    @Lazy
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService, SnsService snsService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPurchasesByUserId(@PathVariable Integer userId) {
        List<Purchase> purchases = purchaseService.getPurchasesByUserId(userId);
        if (purchases.isEmpty()) {
            throw new ResourceNotFoundException("No purchases found for user with id: " + userId);
        }
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{userId}/{orderId}")
    public ResponseEntity<?> getPurchasesByUserIdAndOrderId(@PathVariable Integer userId, @PathVariable Integer orderId) {
        List<Purchase> purchases = purchaseService.getPurchasesByUserIdAndOrderId(userId, orderId);
        if (purchases.isEmpty()) {
            throw new ResourceNotFoundException("No purchases found for user with id: " + userId + " and order id: " + orderId);
        }
        return ResponseEntity.ok(purchases);
    }

    @PostMapping
    public ResponseEntity<?> savePurchase(@Valid @RequestBody Purchase purchase, BindingResult result) {
        purchaseService.savePurchase(purchase);
        return ResponseEntity.ok().body(Map.of("message", "Purchase saved successfully", "purchaseId", purchase.getOrderId()));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updatePurchase(@Valid @PathVariable Integer orderId, @RequestBody Purchase purchase, BindingResult result) {
        purchaseService.getPurchaseById(orderId);
        purchase.setOrderId(orderId);
        purchaseService.savePurchase(purchase);
        return ResponseEntity.ok(Map.of("message", "Purchase updated successfully", "purchaseId", purchase.getOrderId()));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deletePurchase(@PathVariable Integer orderId) {
        purchaseService.getPurchaseById(orderId);
        purchaseService.deletePurchase(orderId);
        return ResponseEntity.noContent().build();
    }
}
