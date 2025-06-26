package com.develop.orders_microservice.domain.interfaces;

import com.develop.orders_microservice.application.dtos.PurchaseRequestDto;
import com.develop.orders_microservice.application.dtos.PurchaseResponseDto;
import com.develop.orders_microservice.domain.models.Purchase;

import java.util.List;

public interface PurchaseService {
    List<PurchaseResponseDto> getPurchasesByUserId(Integer userId);
    List<PurchaseResponseDto> getAllPurchases();
    PurchaseResponseDto getPurchaseByUserIdAndOrderId(Integer userId, Integer orderId);
    Purchase savePurchase(PurchaseRequestDto purchaseRequest);
    void deletePurchase(Integer purchaseId);
    Purchase getPurchaseById(Integer purchaseId);
    Purchase updatePurchase(Integer orderId, PurchaseRequestDto purchaseRequest);
}
