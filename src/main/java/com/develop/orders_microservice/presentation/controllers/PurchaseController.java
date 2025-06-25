package com.develop.orders_microservice.presentation.controllers;

import com.develop.orders_microservice.application.dtos.PurchaseRequestDto;
import com.develop.orders_microservice.application.dtos.PurchaseResponseDto;
import com.develop.orders_microservice.domain.interfaces.PurchaseService;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.domain.models.PurchaseProduct;
import com.develop.orders_microservice.infraestructure.messaging.SnsService;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseProductRepository;
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
    private final PurchaseProductRepository purchaseProductRepository;

    public PurchaseController(PurchaseService purchaseService, SnsService snsService, PurchaseProductRepository purchaseProductRepository) {
        this.purchaseService = purchaseService;
        this.purchaseProductRepository = purchaseProductRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPurchasesByUserId(@PathVariable Integer userId) {
        List<PurchaseResponseDto> purchases = purchaseService.getPurchasesByUserId(userId);
        if (purchases.isEmpty()) {
            throw new ResourceNotFoundException("No purchases found for user with id: " + userId);
        }
        return ResponseEntity.ok(purchases);
    }

    @GetMapping
    public ResponseEntity<?> getAllPurchases() {
        List<PurchaseResponseDto> purchases = purchaseService.getAllPurchases();
        if (purchases.isEmpty()) {
            throw new ResourceNotFoundException("No purchases found");
        }
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{userId}/{orderId}")
    public ResponseEntity<?> getPurchasesByUserIdAndOrderId(@PathVariable Integer userId, @PathVariable Integer orderId) {
        PurchaseResponseDto purchases = purchaseService.getPurchaseByUserIdAndOrderId(userId, orderId);
        if (purchases == null) {
            throw new ResourceNotFoundException("No purchases found for user with id: " + userId + " and order id: " + orderId);
        }
        return ResponseEntity.ok(purchases);
    }

    // Cambiar el método savePurchase
    @PostMapping
    public ResponseEntity<?> savePurchase(@Valid @RequestBody PurchaseRequestDto purchaseRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException("Invalid data");
        }
        Purchase purchase = purchaseService.savePurchase(purchaseRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Purchase saved successfully",
                        "purchaseId", purchase.getOrderId()
                ));
    }

    @GetMapping("/{orderId}/products")
    public ResponseEntity<?> getPurchaseProducts(@PathVariable Integer orderId) {
        List<PurchaseProduct> products = purchaseProductRepository.findByPurchaseId(Long.valueOf(orderId));
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updatePurchase(
            @PathVariable Integer orderId,
            @Valid @RequestBody PurchaseRequestDto purchaseRequest,
            BindingResult result) {

        if (result.hasErrors()) {
            throw new BadRequestException("Invalid data");
        }

        // Verificar que la orden existe
        Purchase existingPurchase = purchaseService.getPurchaseById(orderId);

        // Actualizar la orden
        Purchase updatedPurchase = purchaseService.updatePurchase(orderId, purchaseRequest);

        return ResponseEntity.ok(Map.of(
                "message", "Purchase updated successfully",
                "purchaseId", updatedPurchase.getOrderId()
        ));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deletePurchase(@PathVariable Integer orderId) {
        purchaseService.getPurchaseById(orderId);
        purchaseService.deletePurchase(orderId);
        return ResponseEntity.noContent().build();
    }
}
