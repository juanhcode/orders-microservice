package com.develop.orders_microservice.application.use_cases;

import com.develop.orders_microservice.domain.interfaces.PurchaseService;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.domain.models.Users;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public List<Purchase> getPurchasesByUserId(Users userId) {
        return purchaseRepository.findByUserId(userId);
    }

    @Override
    public void savePurchase(Purchase purchase) {
        purchaseRepository.save(purchase);
    }

    @Override
    public void deletePurchase(Integer purchaseId) {
        purchaseRepository.deleteById(purchaseId);
    }

    @Override
    public Optional<Purchase> getPurchaseById(Integer purchaseId) {
        return purchaseRepository.findById(purchaseId);
    }
}
