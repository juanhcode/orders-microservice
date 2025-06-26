package com.develop.orders_microservice.infraestructure.repositories;

import com.develop.orders_microservice.domain.models.PurchaseProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseProductRepository extends CrudRepository<PurchaseProduct, Long> {
    List<PurchaseProduct> findByPurchaseId(Long purchaseId);
    void deleteByPurchaseId(Long purchaseId);
}