package com.develop.orders_microservice.infraestructure.repositories;

import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.domain.models.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends CrudRepository <Purchase, Integer> {
    List<Purchase> findByUserId(Users userId);
}
