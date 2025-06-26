package com.develop.orders_microservice.repositories;

import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PurchaseRepositoryTests {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Test
    @DisplayName("Debe encontrar compras por userId")
    void testFindByUserId() {
        // Arrange
        Purchase p1 = new Purchase();
        p1.setUserId(1);
        p1.setDeliveryAddress("Calle 1");
        p1.setPaymentTypeId(1);
        p1.setPaymentStatusId(1);
        p1.setDeliveryId(100L);
        p1.setTotal(BigDecimal.valueOf(50.0));

        Purchase p2 = new Purchase();
        p2.setUserId(2);
        p2.setDeliveryAddress("Calle 2");
        p2.setPaymentTypeId(1);
        p2.setPaymentStatusId(1);
        p2.setDeliveryId(101L);
        p2.setTotal(BigDecimal.valueOf(75.0));

        Purchase p3 = new Purchase();
        p3.setUserId(1);
        p3.setDeliveryAddress("Calle 3");
        p3.setPaymentTypeId(2);
        p3.setPaymentStatusId(2);
        p3.setDeliveryId(102L);
        p3.setTotal(BigDecimal.valueOf(100.0));

        purchaseRepository.save(p1);
        purchaseRepository.save(p2);
        purchaseRepository.save(p3);

        // Act
        List<Purchase> result = purchaseRepository.findByUserId(1);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getUserId().equals(1));
    }

    @Test
    @DisplayName("Debe encontrar una compra por userId y orderId")
    void testFindByUserIdAndOrderId() {
        // Arrange
        Purchase p1 = new Purchase();
        p1.setUserId(1);
        p1.setDeliveryAddress("Calle 1");
        p1.setPaymentTypeId(1);
        p1.setPaymentStatusId(1);
        p1.setDeliveryId(100L);
        p1.setTotal(BigDecimal.valueOf(50.0));
        purchaseRepository.save(p1);

        Purchase p2 = new Purchase();
        p2.setUserId(2);
        p2.setDeliveryAddress("Calle 2");
        p2.setPaymentTypeId(1);
        p2.setPaymentStatusId(1);
        p2.setDeliveryId(101L);
        p2.setTotal(BigDecimal.valueOf(75.0));
        purchaseRepository.save(p2);

        // Act
        Purchase result = purchaseRepository.findByUserIdAndOrderId(p1.getUserId(), p1.getOrderId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(p1.getUserId());
        assertThat(result.getOrderId()).isEqualTo(p1.getOrderId());
    }

}
