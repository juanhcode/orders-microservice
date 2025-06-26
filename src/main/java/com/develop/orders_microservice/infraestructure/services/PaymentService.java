package com.develop.orders_microservice.infraestructure.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;


@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        System.out.println("Stripe Secret Key: " + stripeSecretKey);
        Stripe.apiKey = stripeSecretKey;
        System.out.println("=== Stripe API Key ===");
        System.out.println("Key loaded: " + stripeSecretKey.substring(0, 12) + "...");
        System.out.println("Key length: " + stripeSecretKey.length() + " characters");
    }



    /**
     * Crea una sesión de pago simple en Stripe
     * @param amount Monto en centavos (ej: 1000 = $10.00)
     * @param currency Código de moneda (ej: "usd", "eur")
     * @param successUrl URL a la que redirigir tras pago exitoso
     * @param cancelUrl URL a la que redirigir si se cancela
     * @return URL del checkout de Stripe
     */
    public String createSimplePayment(long amount, String currency, String successUrl, String cancelUrl)
            throws StripeException {

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency.toLowerCase())
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Pago único")
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /**
     * Verifica el estado de un pago
     * @param sessionId ID de la sesión de Stripe
     * @return "paid" si fue exitoso, otro estado si no
     */
    public String checkPaymentStatus(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return session.getPaymentStatus();
    }
}