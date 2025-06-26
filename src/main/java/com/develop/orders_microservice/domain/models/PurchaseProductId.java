package com.develop.orders_microservice.domain.models;

import java.io.Serializable;
import java.util.Objects;

public class PurchaseProductId implements Serializable {
    private Integer purchaseId;
    private Integer productId;

    // Constructores, equals, hashCode
    public PurchaseProductId() {
    }

    public PurchaseProductId(Integer purchaseId, Integer productId) {
        this.purchaseId = purchaseId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseProductId that = (PurchaseProductId) o;
        return Objects.equals(purchaseId, that.purchaseId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId, productId);
    }
}
