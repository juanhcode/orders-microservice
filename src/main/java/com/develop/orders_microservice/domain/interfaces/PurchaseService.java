package com.develop.orders_microservice.domain.interfaces;

import com.develop.orders_microservice.application.dtos.PurchaseRequestDto;
import com.develop.orders_microservice.domain.models.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseService {
    List<Purchase> getPurchasesByUserId(Integer userId);
    List<Purchase> getPurchasesByUserIdAndOrderId(Integer userId, Integer orderId);
    Purchase savePurchase(PurchaseRequestDto purchaseRequest);
    void deletePurchase(Integer purchaseId);
    Purchase getPurchaseById(Integer purchaseId);

    Purchase updatePurchase(Integer orderId, PurchaseRequestDto purchaseRequest);
}
