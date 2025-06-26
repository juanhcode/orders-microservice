package com.develop.orders_microservice.services;

import com.develop.orders_microservice.infraestructure.services.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
        ReflectionTestUtils.setField(paymentService, "stripeSecretKey", "sk_test_1234567890");
    }

    @Test
    void testCreateSimplePayment() throws StripeException {
        try (MockedStatic<Session> sessionMockedStatic = Mockito.mockStatic(Session.class)) {
            Session mockSession = Mockito.mock(Session.class);
            Mockito.when(mockSession.getUrl()).thenReturn("https://checkout.stripe.com/test-session");
            sessionMockedStatic.when(() -> Session.create(Mockito.any(SessionCreateParams.class))).thenReturn(mockSession);

            String url = paymentService.createSimplePayment(1000, "usd", "http://success", "http://cancel");
            assertEquals("https://checkout.stripe.com/test-session", url);
        }
    }

    @Test
    void testCheckPaymentStatus() throws StripeException {
        try (MockedStatic<Session> sessionMockedStatic = Mockito.mockStatic(Session.class)) {
            Session mockSession = Mockito.mock(Session.class);
            Mockito.when(mockSession.getPaymentStatus()).thenReturn("paid");
            sessionMockedStatic.when(() -> Session.retrieve("test_id")).thenReturn(mockSession);

            String status = paymentService.checkPaymentStatus("test_id");
            assertEquals("paid", status);
        }
    }
}