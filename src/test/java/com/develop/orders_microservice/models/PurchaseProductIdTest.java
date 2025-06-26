package com.develop.orders_microservice.models;

import com.develop.orders_microservice.domain.models.PurchaseProductId;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PurchaseProductIdTest {

    @Test
    void testEqualsAndHashCode() {
        PurchaseProductId id1 = new PurchaseProductId(1L, 2L);
        PurchaseProductId id2 = new PurchaseProductId(1L, 2L);
        PurchaseProductId id3 = new PurchaseProductId(2L, 3L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1, id3);
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        PurchaseProductId id = new PurchaseProductId();
        assertNotNull(id);
    }
}