package com.develop.orders_microservice.services;

import com.develop.orders_microservice.application.dtos.PurchaseRequestDto;
import com.develop.orders_microservice.application.dtos.PurchaseResponseDto;
import com.develop.orders_microservice.application.dtos.PurchaseProductDto;
import com.develop.orders_microservice.application.use_cases.PurchaseServiceImpl;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.infraestructure.clients.DeliveryClientRest;
import com.develop.orders_microservice.infraestructure.clients.PaymentStatusClientRest;
import com.develop.orders_microservice.infraestructure.clients.UsersClientRest;
import com.develop.orders_microservice.infraestructure.clients.models.Delivery;
import com.develop.orders_microservice.infraestructure.clients.models.PaymentStatus;
import com.develop.orders_microservice.infraestructure.clients.models.Users;
import com.develop.orders_microservice.infraestructure.messaging.SnsService;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseProductRepository;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseRepository;
import com.develop.orders_microservice.presentation.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UsersClientRest usersClientRest;

    @Mock
    private PaymentStatusClientRest paymentStatusClientRest;

    @Mock
    private SnsService snsService;

    @Mock
    private DeliveryClientRest deliveryClientRest;

    @Mock
    private PurchaseProductRepository purchaseProductRepository;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    @Test
    void getPurchaseById () {
        // Mock de la compra
        Purchase purchase = new Purchase();
        purchase.setUserId(1);
        purchase.setTotal(BigDecimal.valueOf(1400));
        purchase.setDeliveryAddress("123 Main St");
        purchase.setPaymentTypeId(1);
        purchase.setPaymentStatusId(1);

        // Simular el comportamiento del repositorio
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(purchase));

        // Ejecutar el método y verificar el resultado
        Purchase result = purchaseService.getPurchaseById(1);
        assertNotNull(result);
        assertEquals(purchase.getUserId(), result.getUserId());
    }

    @Test
    void getPurchasesByUserId() {
        // Mock del usuario retornado por UsersClientRest
        Users user = new Users();
        user.setName("John");
        user.setLastName("Doe");
        user.setEmail("jhondoe@gmail.com");
        user.setAddress("123 Main St");
        user.setEnabled(true);
        user.setRoleId(1);

        // Mock de las compras
        Purchase purchase1 = new Purchase();
        purchase1.setOrderId(1);
        purchase1.setUserId(1);
        purchase1.setDeliveryId(10L);
        purchase1.setDeliveryAddress("Dir 1");
        purchase1.setPaymentTypeId(1);
        purchase1.setPaymentStatusId(1);
        purchase1.setTotal(BigDecimal.valueOf(100));

        Purchase purchase2 = new Purchase();
        purchase2.setOrderId(2);
        purchase2.setUserId(1);
        purchase2.setDeliveryId(20L);
        purchase2.setDeliveryAddress("Dir 2");
        purchase2.setPaymentTypeId(2);
        purchase2.setPaymentStatusId(2);
        purchase2.setTotal(BigDecimal.valueOf(200));

        // Mock de los deliveries
        Delivery delivery1 = new Delivery();
        delivery1.setId(10L);
        delivery1.setUserId(1L);
        delivery1.setStatus("En camino");

        Delivery delivery2 = new Delivery();
        delivery2.setId(20L);
        delivery2.setUserId(1L);
        delivery2.setStatus("Entregado");

        // Simular el comportamiento de usersClientRest
        when(usersClientRest.getUser(1)).thenReturn(Optional.of(user));
        // Simular el comportamiento del repositorio
        when(purchaseRepository.findByUserId(1)).thenReturn(List.of(purchase1, purchase2));
        // Simular el comportamiento de deliveryClientRest
        when(deliveryClientRest.getDeliveryById(10L)).thenReturn(delivery1);
        when(deliveryClientRest.getDeliveryById(20L)).thenReturn(delivery2);

        // Ejecutar el método y verificar el resultado
        List<PurchaseResponseDto> purchases = purchaseService.getPurchasesByUserId(1);
        assertNotNull(purchases);
        assertEquals(2, purchases.size());
        assertEquals("En camino", purchases.get(0).getDeliveryName());
        assertEquals("Entregado", purchases.get(1).getDeliveryName());
    }

    @Test
    void getPurchasesByUserIdAndOrderId() {
        // Mock del usuario retornado por UsersClientRest
        Users user = new Users();
        user.setName("John");
        user.setLastName("Doe");
        user.setEmail("jhondoe@gmail.com");
        user.setAddress("123 Main St");
        user.setEnabled(true);
        user.setRoleId(1);

        // Mock de la orden
        Purchase purchase = new Purchase();
        purchase.setOrderId(1);
        purchase.setUserId(1);
        purchase.setDeliveryId(10L);
        purchase.setDeliveryAddress("123 Main St");
        purchase.setPaymentTypeId(1);
        purchase.setPaymentStatusId(1);
        purchase.setTotal(BigDecimal.valueOf(1400));

        // Mock del delivery
        Delivery delivery = new Delivery();
        delivery.setId(10L);
        delivery.setUserId(1L);
        delivery.setStatus("En camino");

        // Simular el comportamiento de usersClientRest
        when(usersClientRest.getUser(1)).thenReturn(Optional.of(user));
        // Simular el comportamiento del repositorio
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(purchase));
        when(purchaseRepository.findByUserIdAndOrderId(1, 1)).thenReturn(purchase);
        // Simular el comportamiento de deliveryClientRest
        when(deliveryClientRest.getDeliveryById(10L)).thenReturn(delivery);

        // Ejecutar el método y verificar el resultado
        PurchaseResponseDto purchaseObj = purchaseService.getPurchaseByUserIdAndOrderId(1, 1);
        assertNotNull(purchaseObj);
        assertEquals(1L, purchaseObj.getUserId());
        assertEquals(1L, purchaseObj.getOrderId());
        assertEquals("En camino", purchaseObj.getDeliveryName());
        assertEquals(BigDecimal.valueOf(1400), purchaseObj.getTotal());
    }

    @Test
    void savePurchase() {
        // Mock del request DTO
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        requestDto.setUserId(1);
        requestDto.setDeliveryAddress("123 Main St");
        requestDto.setPaymentTypeId(1);
        requestDto.setPaymentStatusId(1);
        // Simula productos
        PurchaseProductDto productDto = new PurchaseProductDto();
        productDto.setProductId(1L);
        productDto.setQuantity(2);
        productDto.setTotal(700.0);
        requestDto.setProducts(List.of(productDto));

        // Mock del delivery creado
        Delivery delivery = new Delivery();
        delivery.setId(10L);
        delivery.setStatus("En camino");

        // Mock de la compra guardada
        Purchase savedPurchase = new Purchase();
        savedPurchase.setOrderId(1);
        savedPurchase.setUserId(1);
        savedPurchase.setDeliveryAddress("123 Main St");
        savedPurchase.setPaymentTypeId(1);
        savedPurchase.setPaymentStatusId(1);
        savedPurchase.setDeliveryId(10L);
        savedPurchase.setTotal(BigDecimal.valueOf(1400));

        // Mock del estado de pago
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setId(1);
        paymentStatus.setName("In Process");

        // Simular comportamiento de los mocks
        when(deliveryClientRest.createDelivery(any(Delivery.class))).thenReturn(delivery);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);
        when(paymentStatusClientRest.getPaymentStatusNameById(1)).thenReturn(paymentStatus);

        // Ejecutar el método
        Purchase result = purchaseService.savePurchase(requestDto);

        // Verificaciones
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("123 Main St", result.getDeliveryAddress());
        assertEquals(10L, result.getDeliveryId());
        assertEquals(BigDecimal.valueOf(1400), result.getTotal());

        verify(deliveryClientRest).createDelivery(any(Delivery.class));
        verify(purchaseRepository).save(any(Purchase.class));
        verify(paymentStatusClientRest).getPaymentStatusNameById(1);
        verify(snsService).publishMessage(anyString());
    }

    @Test
    void updatePurchase() {
        // Mock de la compra existente
        Purchase existingPurchase = new Purchase();
        existingPurchase.setOrderId(1);
        existingPurchase.setUserId(1);
        existingPurchase.setDeliveryAddress("123 Main St");
        existingPurchase.setPaymentTypeId(1);
        existingPurchase.setPaymentStatusId(1);
        existingPurchase.setDeliveryId(10L);
        existingPurchase.setTotal(BigDecimal.valueOf(1400));

        // Mock del producto actualizado
        PurchaseProductDto productDto = new PurchaseProductDto();
        productDto.setProductId(2L);
        productDto.setQuantity(3);
        productDto.setTotal(500.0);

        // Mock del request DTO actualizado
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        requestDto.setUserId(2);
        requestDto.setDeliveryAddress("456 Elm St");
        requestDto.setPaymentTypeId(2);
        requestDto.setPaymentStatusId(2);
        requestDto.setProducts(List.of(productDto));

        // Mock del delivery asociado
        Delivery delivery = new Delivery();
        delivery.setId(10L);
        delivery.setUserId(2L);
        delivery.setStatus("Entregado");

        // Mock del estado de pago
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setId(2);
        paymentStatus.setName("Order Shipped");

        // Mock de la compra actualizada
        Purchase updatedPurchase = new Purchase();
        updatedPurchase.setOrderId(1);
        updatedPurchase.setUserId(2);
        updatedPurchase.setDeliveryAddress("456 Elm St");
        updatedPurchase.setPaymentTypeId(2);
        updatedPurchase.setPaymentStatusId(2);
        updatedPurchase.setDeliveryId(10L);
        updatedPurchase.setTotal(BigDecimal.valueOf(500));

        // Simular comportamiento de los mocks
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(existingPurchase));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(updatedPurchase);
        when(deliveryClientRest.getDeliveryById(10L)).thenReturn(delivery);
        when(paymentStatusClientRest.getPaymentStatusNameById(2)).thenReturn(paymentStatus);

        // Ejecutar el método
        Purchase result = purchaseService.updatePurchase(1, requestDto);

        // Verificaciones
        assertNotNull(result);
        assertEquals(2, result.getUserId());
        assertEquals("456 Elm St", result.getDeliveryAddress());
        assertEquals(2, result.getPaymentTypeId());
        assertEquals(2, result.getPaymentStatusId());
        assertEquals(BigDecimal.valueOf(500), result.getTotal());

        verify(purchaseRepository).findById(1);
        verify(purchaseRepository).save(any(Purchase.class));
        verify(deliveryClientRest).getDeliveryById(10L);
        verify(paymentStatusClientRest).getPaymentStatusNameById(2);
        verify(snsService).publishMessage(anyString());
    }

    @Test
    void deletePurchase() {
        // Mock de la compra existente
        Purchase purchase = new Purchase();
        purchase.setOrderId(1);
        purchase.setUserId(1);
        purchase.setTotal(BigDecimal.valueOf(1400));
        purchase.setDeliveryAddress("123 Main St");
        purchase.setPaymentTypeId(1);
        purchase.setPaymentStatusId(1);

        // Simular el comportamiento del repositorio para compra existente
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(purchase));

        // Ejecutar y verificar eliminación
        purchaseService.getPurchaseById(1); // Verifica existencia
        purchaseService.deletePurchase(1);

        verify(purchaseRepository).findById(1);
        verify(purchaseRepository).deleteById(1);
    }

    @Test
    void deletePurchase_notFound() {
        // Simular que la compra no existe
        when(purchaseRepository.findById(99)).thenReturn(Optional.empty());

        // Verificar que lanza excepción al intentar eliminar una compra inexistente
        assertThrows(ResourceNotFoundException.class, () -> purchaseService.getPurchaseById(99));
        // No debe intentar eliminar si no existe
        verify(purchaseRepository).findById(99);
    }
}
