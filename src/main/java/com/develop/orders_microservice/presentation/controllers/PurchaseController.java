package com.develop.orders_microservice.presentation.controllers;

import com.develop.orders_microservice.domain.interfaces.PurchaseService;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.domain.models.Users;
import com.develop.orders_microservice.presentation.exceptions.BadRequestException;
import com.develop.orders_microservice.presentation.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {
    @Lazy
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPurchasesByUserId(@PathVariable Integer userId) {
        Users user = new Users();
        user.setId(userId);

        List<Purchase> purchases = purchaseService.getPurchasesByUserId(user);
        return ResponseEntity.ok(purchases);
    }

    @PostMapping
    public ResponseEntity<?> savePurchase(@Valid @RequestBody Purchase purchase, BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException("Invalid purchase data: " + result.getFieldErrors());
        }
        purchaseService.savePurchase(purchase);
        return ResponseEntity.ok().body(Map.of("message", "Purchase saved successfully", "purchaseId", purchase.getOrderId()));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updatePurchase(@Valid @PathVariable Integer orderId, @RequestBody Purchase purchase, BindingResult result) {
        Optional<Purchase> purchaseOptional = purchaseService.getPurchaseById(orderId);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase with id: " + orderId + " not found");
        } else if (result.hasErrors()) {
            throw new BadRequestException("Invalid purchase data: " + result.getFieldErrors());
        }
        purchase.setOrderId(orderId);
        purchaseService.savePurchase(purchase);
        return ResponseEntity.ok(purchase);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deletePurchase(@PathVariable Integer orderId) {
        Optional<Purchase> purchaseOptional = purchaseService.getPurchaseById(orderId);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase with id: " + orderId + " not found");
        }
        purchaseService.deletePurchase(orderId);
        return ResponseEntity.ok().build();
    }
}
