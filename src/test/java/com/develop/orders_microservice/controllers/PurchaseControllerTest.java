package com.develop.orders_microservice.controllers;

import com.develop.orders_microservice.application.dtos.PurchaseRequestDto;
import com.develop.orders_microservice.application.dtos.PurchaseResponseDto;
import com.develop.orders_microservice.domain.interfaces.PurchaseService;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.domain.models.PurchaseProduct;
import com.stripe.exception.ApiException;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseProductRepository;
import com.develop.orders_microservice.infraestructure.services.PaymentService;
import com.develop.orders_microservice.presentation.controllers.PurchaseController;
import com.develop.orders_microservice.presentation.exceptions.BadRequestException;
import com.develop.orders_microservice.presentation.exceptions.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseControllerTest {
    @Mock
    private PurchaseService purchaseService;

    @Mock
    private PurchaseProductRepository purchaseProductRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PurchaseController purchaseController;

    @Test
    void getPurchasesByUserId_returnsPurchasesList() {
        Integer userId = 1;
        List<PurchaseResponseDto> mockPurchases = List.of(new PurchaseResponseDto(), new PurchaseResponseDto());

        when(purchaseService.getPurchasesByUserId(userId)).thenReturn(mockPurchases);

        ResponseEntity<?> response = purchaseController.getPurchasesByUserId(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPurchases, response.getBody());
        verify(purchaseService, times(1)).getPurchasesByUserId(userId);
    }

    @Test
    void getPurchasesByUserId_throwsResourceNotFoundException_whenNoPurchasesFound() {
        Integer userId = 99;
        when(purchaseService.getPurchasesByUserId(userId)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseController.getPurchasesByUserId(userId);
        });
    }

    @Test
    void getAllPurchases_returnsPurchasesList() {
        List<PurchaseResponseDto> mockPurchases = List.of(new PurchaseResponseDto(), new PurchaseResponseDto());
        when(purchaseService.getAllPurchases()).thenReturn(mockPurchases);

        ResponseEntity<?> response = purchaseController.getAllPurchases();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPurchases, response.getBody());
        verify(purchaseService, times(1)).getAllPurchases();
    }

    @Test
    void getAllPurchases_throwsResourceNotFoundException_whenNoPurchasesFound() {
        when(purchaseService.getAllPurchases()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseController.getAllPurchases();
        });
    }

    @Test
    void getPurchasesByUserIdAndOrderId_returnsPurchase() {
        Integer userId = 1;
        Integer orderId = 10;
        PurchaseResponseDto mockPurchase = new PurchaseResponseDto();

        when(purchaseService.getPurchaseByUserIdAndOrderId(userId, orderId)).thenReturn(mockPurchase);

        ResponseEntity<?> response = purchaseController.getPurchasesByUserIdAndOrderId(userId, orderId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPurchase, response.getBody());
        verify(purchaseService, times(1)).getPurchaseByUserIdAndOrderId(userId, orderId);
    }

    @Test
    void getPurchasesByUserIdAndOrderId_throwsResourceNotFoundException_whenNoPurchaseFound() {
        Integer userId = 1;
        Integer orderId = 99;

        when(purchaseService.getPurchaseByUserIdAndOrderId(userId, orderId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseController.getPurchasesByUserIdAndOrderId(userId, orderId);
        });
    }

    @Test
    void savePurchase_returnsCreatedResponse_whenValidRequest() {
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        Purchase mockPurchase = new Purchase();
        mockPurchase.setOrderId(123);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(purchaseService.savePurchase(requestDto)).thenReturn(mockPurchase);

        ResponseEntity<?> response = purchaseController.savePurchase(requestDto, bindingResult);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("message"));
        assertEquals(123, ((java.util.Map<?, ?>) response.getBody()).get("purchaseId"));
        verify(purchaseService, times(1)).savePurchase(requestDto);
    }

    @Test
    void savePurchase_throwsBadRequestException_whenInvalidRequest() {
        PurchaseRequestDto requestDto = new PurchaseRequestDto();

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            purchaseController.savePurchase(requestDto, bindingResult);
        });
        verify(purchaseService, never()).savePurchase(any());
    }

    @Test
    void getPurchaseProducts_returnsProductList() {
        Integer orderId = 1;
        List<PurchaseProduct> mockProducts = List.of(new PurchaseProduct(), new PurchaseProduct());

        when(purchaseProductRepository.findByPurchaseId(Long.valueOf(orderId))).thenReturn(mockProducts);

        ResponseEntity<?> response = purchaseController.getPurchaseProducts(orderId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockProducts, response.getBody());
        verify(purchaseProductRepository, times(1)).findByPurchaseId(Long.valueOf(orderId));
    }

    @Test
    void getPurchaseProducts_returnsEmptyList_whenNoProductsFound() {
        Integer orderId = 2;
        when(purchaseProductRepository.findByPurchaseId(Long.valueOf(orderId))).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = purchaseController.getPurchaseProducts(orderId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(purchaseProductRepository, times(1)).findByPurchaseId(Long.valueOf(orderId));
    }

    @Test
    void updatePurchase_returnsOkResponse_whenValidRequest() {
        Integer orderId = 1;
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        Purchase existingPurchase = new Purchase();
        Purchase updatedPurchase = new Purchase();
        updatedPurchase.setOrderId(1);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(purchaseService.getPurchaseById(orderId)).thenReturn(existingPurchase);
        when(purchaseService.updatePurchase(orderId, requestDto)).thenReturn(updatedPurchase);

        ResponseEntity<?> response = purchaseController.updatePurchase(orderId, requestDto, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("message"));
        assertEquals(1, ((java.util.Map<?, ?>) response.getBody()).get("purchaseId"));
        verify(purchaseService, times(1)).getPurchaseById(orderId);
        verify(purchaseService, times(1)).updatePurchase(orderId, requestDto);
    }

    @Test
    void updatePurchase_throwsBadRequestException_whenInvalidRequest() {
        Integer orderId = 1;
        PurchaseRequestDto requestDto = new PurchaseRequestDto();

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            purchaseController.updatePurchase(orderId, requestDto, bindingResult);
        });
        verify(purchaseService, never()).getPurchaseById(any());
        verify(purchaseService, never()).updatePurchase(anyInt(), any());
    }

    @Test
    void deletePurchase_returnsNoContent_whenPurchaseExists() {
        Integer orderId = 1;
        Purchase existingPurchase = new Purchase();

        when(purchaseService.getPurchaseById(orderId)).thenReturn(existingPurchase);
        doNothing().when(purchaseService).deletePurchase(orderId);

        ResponseEntity<?> response = purchaseController.deletePurchase(orderId);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(purchaseService, times(1)).getPurchaseById(orderId);
        verify(purchaseService, times(1)).deletePurchase(orderId);
    }

    @Test
    void deletePurchase_throwsResourceNotFoundException_whenPurchaseDoesNotExist() {
        Integer orderId = 99;
        when(purchaseService.getPurchaseById(orderId)).thenThrow(new ResourceNotFoundException("No purchase found"));

        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseController.deletePurchase(orderId);
        });
        verify(purchaseService, times(1)).getPurchaseById(orderId);
        verify(purchaseService, never()).deletePurchase(anyInt());
    }

    @Test
    void createTestPayment_returnsPaymentUrl_whenSuccess() throws StripeException {
        long amount = 1000L;
        String currency = "usd";
        String expectedUrl = "https://stripe.com/test-session";

        when(paymentService.createSimplePayment(
                eq(amount),
                eq(currency),
                anyString(),
                anyString()
        )).thenReturn(expectedUrl);

        String result = purchaseController.createTestPayment(amount, currency);

        assertEquals(expectedUrl, result);
        verify(paymentService, times(1)).createSimplePayment(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void createTestPayment_throwsStripeException_whenPaymentFails() throws StripeException {
        long amount = 1000L;
        String currency = "usd";

        when(paymentService.createSimplePayment(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(new ApiException("Error", "requestId", null, 400, null));

        assertThrows(StripeException.class, () -> {
            purchaseController.createTestPayment(amount, currency);
        });
        verify(paymentService, times(1)).createSimplePayment(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void checkStatus_returnsStatus_whenSuccess() throws StripeException{
        String sessionId = "sess_123";
        String expectedStatus = "paid";

        when(paymentService.checkPaymentStatus(sessionId)).thenReturn(expectedStatus);

        String result = purchaseController.checkStatus(sessionId);

        assertEquals(expectedStatus, result);
        verify(paymentService, times(1)).checkPaymentStatus(sessionId);
    }

    @Test
    void checkStatus_returnsErrorMessage_whenExceptionThrown() throws StripeException {
        String sessionId = "sess_456";
        when(paymentService.checkPaymentStatus(sessionId)).thenThrow(new RuntimeException("Service error"));

        String result = purchaseController.checkStatus(sessionId);

        assertTrue(result.startsWith("Error: "));
        assertTrue(result.contains("Service error"));
        verify(paymentService, times(1)).checkPaymentStatus(sessionId);
    }

    @Test
    void getPaymentDetails_returnsSessionJson_whenSuccess() throws StripeException {
        String sessionId = "sess_123";
        Session mockSession = mock(Session.class);
        String expectedJson = "{\"id\":\"sess_123\"}";

        try (MockedStatic<Session> sessionStatic = mockStatic(Session.class)) {
            sessionStatic.when(() -> Session.retrieve(sessionId)).thenReturn(mockSession);
            when(mockSession.toJson()).thenReturn(expectedJson);

            String result = purchaseController.getPaymentDetails(sessionId);

            assertEquals(expectedJson, result);
            sessionStatic.verify(() -> Session.retrieve(sessionId), times(1));
            verify(mockSession, times(1)).toJson();
        }
    }

    @Test
    void getPaymentDetails_throwsStripeException_whenStripeFails() throws StripeException {
        String sessionId = "sess_456";

        try (MockedStatic<Session> sessionStatic = mockStatic(Session.class)) {
            sessionStatic.when(() -> Session.retrieve(sessionId))
                    .thenThrow(new ApiException("Error", "req", null, 400, null));

            assertThrows(StripeException.class, () -> {
                purchaseController.getPaymentDetails(sessionId);
            });
            sessionStatic.verify(() -> Session.retrieve(sessionId), times(1));
        }
    }

}
