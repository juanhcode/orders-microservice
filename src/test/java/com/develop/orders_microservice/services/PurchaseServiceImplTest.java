package com.develop.orders_microservice.services;

import com.develop.orders_microservice.application.dtos.PurchaseResponseDto;
import com.develop.orders_microservice.application.use_cases.PurchaseServiceImpl;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.infraestructure.clients.PaymentStatusClientRest;
import com.develop.orders_microservice.infraestructure.clients.UsersClientRest;
import com.develop.orders_microservice.infraestructure.clients.models.PaymentStatus;
import com.develop.orders_microservice.infraestructure.clients.models.Users;
import com.develop.orders_microservice.infraestructure.messaging.SnsService;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        // Simular el comportamiento de usersClientRest
        when(usersClientRest.getUser(1)).thenReturn(Optional.of(user));

        // Simular el comportamiento del repositorio
        when(purchaseRepository.findByUserId(1)).thenReturn(List.of(new Purchase(), new Purchase()));

        // Ejecutar el método y verificar el resultado
        List<PurchaseResponseDto> purchases = purchaseService.getPurchasesByUserId(1);
        assertNotNull(purchases);
        assertEquals(2, purchases.size());
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
        purchase.setUserId(1);
        purchase.setTotal(BigDecimal.valueOf(1400));
        purchase.setDeliveryAddress("123 Main St");
        purchase.setPaymentTypeId(1);
        purchase.setPaymentStatusId(1);

        // Simular el comportamiento de usersClientRest
        when(usersClientRest.getUser(1)).thenReturn(Optional.of(user));

        // Simular el comportamiento del repositorio
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(purchase));

        // Simular el comportamiento del repositorio
        when(purchaseRepository.findByUserIdAndOrderId(1, 1)).thenReturn((Purchase) List.of(new Purchase()));

        // Ejecutar el método y verificar el resultado
        PurchaseResponseDto purchaseObj = purchaseService.getPurchaseByUserIdAndOrderId(1, 1);
        assertNotNull(purchaseObj);
        assertEquals(1, purchaseObj.getUserId());

    }

    @Test
    void savePurchase() {
        // Mock de la compra
        Purchase purchase = new Purchase();
        purchase.setUserId(1);
        purchase.setTotal(BigDecimal.valueOf(1400));
        purchase.setDeliveryAddress("123 Main St");
        purchase.setPaymentTypeId(1);
        purchase.setPaymentStatusId(1);

        // Mock del estado de pago
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setId(1);
        paymentStatus.setName("In Process");

        // Simular el comportamiento de PaymentStatusClientRest
        when(paymentStatusClientRest.getPaymentStatusNameById(1)).thenReturn(paymentStatus);

        // Simular el comportamiento del repositorio
        when(purchaseRepository.save(purchase)).thenReturn(purchase);

//        // Ejecutar el método y verificar el resultado
//        purchaseService.savePurchase(purchase);

        // Verificar que el cliente de estado de pago fue llamado
        verify(paymentStatusClientRest).getPaymentStatusNameById(1);

        // Verificar que el servicio SNS publicó el mensaje
        verify(snsService).publishMessage(anyString());
    }

    @Test
    void updatePurchase() {
        // Mock de la compra
        Purchase purchase = new Purchase();
        purchase.setUserId(1);
        purchase.setTotal(BigDecimal.valueOf(1400));
        purchase.setDeliveryAddress("123 Main St");
        purchase.setPaymentTypeId(1);
        purchase.setPaymentStatusId(1);

        // Mock de la compra actualizada
        Purchase updatedPurchase = new Purchase();
        updatedPurchase.setUserId(1);
        updatedPurchase.setTotal(BigDecimal.valueOf(1500));
        updatedPurchase.setDeliveryAddress("456 Elm St");
        updatedPurchase.setPaymentTypeId(2);
        updatedPurchase.setPaymentStatusId(2);

        // Mock del estado de pago
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setId(2);
        paymentStatus.setName("Order Shipped");


        // Simular el comportamiento del repositorio
        when(purchaseRepository.findById(1)).thenReturn(Optional.of(updatedPurchase));
        when(purchaseRepository.save(updatedPurchase)).thenReturn(updatedPurchase);
        when(paymentStatusClientRest.getPaymentStatusNameById(2)).thenReturn(paymentStatus);

//        // Ejecutar el metodo
//        purchaseService.getPurchaseById(1); // Verificar existencia
//        purchaseService.savePurchase(updatedPurchase);

        // Verificar que el repositorio buscó la compra existente
        verify(purchaseRepository).findById(1);

        // Verificar que el repositorio guardó la compra actualizada
        verify(purchaseRepository).save(updatedPurchase);

        // Verificar que el cliente de estado de pago fue llamado
        verify(paymentStatusClientRest).getPaymentStatusNameById(2);

        // Verificar que el servicio SNS publicó el mensaje
        verify(snsService).publishMessage(anyString());
    }

    @Test
    void deletePurchase() {
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
        purchaseService.getPurchaseById(1); // Verificar existencia
        purchaseService.deletePurchase(1);

        // Verificar que el repositorio buscó la compra existente
        verify(purchaseRepository).findById(1);

        // Verificar que el repositorio eliminó la compra
        verify(purchaseRepository).deleteById(1);
    }
}
