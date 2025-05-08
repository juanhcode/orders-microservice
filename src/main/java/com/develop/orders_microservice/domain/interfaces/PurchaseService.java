package com.develop.orders_microservice.domain.interfaces;

import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.domain.models.Users;

import java.util.List;
import java.util.Optional;

public interface PurchaseService {
    List<Purchase> getPurchasesByUserId(Users userId);
    void savePurchase(Purchase purchase);
    void deletePurchase(Integer purchaseId);
    Optional<Purchase> getPurchaseById(Integer purchaseId);
}
