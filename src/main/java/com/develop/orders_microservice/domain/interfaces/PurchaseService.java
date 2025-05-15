package com.develop.orders_microservice.domain.interfaces;

import com.develop.orders_microservice.domain.models.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseService {
    List<Purchase> getPurchasesByUserId(Integer userId);
    void savePurchase(Purchase purchase);
    void deletePurchase(Integer purchaseId);
    Optional<Purchase> getPurchaseById(Integer purchaseId);
}
