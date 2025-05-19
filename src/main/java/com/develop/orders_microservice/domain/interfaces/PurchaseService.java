package com.develop.orders_microservice.domain.interfaces;

import com.develop.orders_microservice.domain.models.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseService {
    List<Purchase> getPurchasesByUserId(Integer userId);
    List<Purchase> getPurchasesByUserIdAndOrderId(Integer userId, Integer orderId);
    void savePurchase(Purchase purchase);
    void deletePurchase(Integer purchaseId);
    Purchase getPurchaseById(Integer purchaseId);
}
