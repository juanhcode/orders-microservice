package com.develop.orders_microservice.repositories;

import com.develop.orders_microservice.domain.models.PurchaseProduct;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PurchaseProductRepositoryTests {

    @Autowired
    private PurchaseProductRepository purchaseProductRepository;

    @Test
    void testFindByPurchaseId_returnsCorrectProducts() {
        // Arrange
        PurchaseProduct product1 = new PurchaseProduct();
        product1.setPurchaseId(1L);
        product1.setQuantity(1);
        product1.setTotal(BigDecimal.valueOf(100));
        product1.setProductId(10L);

        PurchaseProduct product2 = new PurchaseProduct();
        product2.setPurchaseId(1L);
        product2.setQuantity(2);
        product2.setTotal(BigDecimal.valueOf(200));
        product2.setProductId(11L);

        PurchaseProduct product3 = new PurchaseProduct();
        product3.setPurchaseId(2L);
        product3.setQuantity(3);
        product3.setTotal(BigDecimal.valueOf(300));
        product3.setProductId(12L);

        purchaseProductRepository.save(product1);
        purchaseProductRepository.save(product2);
        purchaseProductRepository.save(product3);

        // Act
        List<PurchaseProduct> result = purchaseProductRepository.findByPurchaseId(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("purchaseId").containsOnly(1L);
    }

    @Test
    void testDeleteByPurchaseId_deletesCorrectProducts() {
        // Arrange
        PurchaseProduct product1 = new PurchaseProduct();
        product1.setPurchaseId(1L);
        product1.setQuantity(1);
        product1.setTotal(BigDecimal.valueOf(100));
        product1.setProductId(10L);

        PurchaseProduct product2 = new PurchaseProduct();
        product2.setPurchaseId(1L);
        product2.setQuantity(2);
        product2.setTotal(BigDecimal.valueOf(200));
        product2.setProductId(11L);

        PurchaseProduct product3 = new PurchaseProduct();
        product3.setPurchaseId(2L);
        product3.setQuantity(3);
        product3.setTotal(BigDecimal.valueOf(300));
        product3.setProductId(12L);

        purchaseProductRepository.save(product1);
        purchaseProductRepository.save(product2);
        purchaseProductRepository.save(product3);

        // Act
        purchaseProductRepository.deleteByPurchaseId(1L);

        // Assert
        List<PurchaseProduct> remaining = (List<PurchaseProduct>) purchaseProductRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getPurchaseId()).isEqualTo(2L);
    }
}
