package com.develop.orders_microservice.presentation.controllers;

import com.develop.orders_microservice.application.use_cases.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-test-payment")
    public String createTestPayment(@RequestParam long amount, @RequestParam String currency) throws  StripeException {
        // URLs especiales de prueba de Stripe (funcionan sin frontend)
        String successUrl = "https://stripe.com/docs/testing#successful-payments";
        String cancelUrl = "https://stripe.com/docs/testing#failed-payments";

        return paymentService.createSimplePayment(amount, currency, successUrl, cancelUrl);
    }

    @GetMapping("/status")
    public String checkStatus(@RequestParam String sessionId) {
        try {
            return paymentService.checkPaymentStatus(sessionId);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/payment-details")
    public String getPaymentDetails(@RequestParam String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return session.toJson(); // Devuelve un Map para mayor flexibilidad
    }
}