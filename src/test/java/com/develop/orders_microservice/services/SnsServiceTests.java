package com.develop.orders_microservice.services;

import com.develop.orders_microservice.infraestructure.messaging.SnsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class SnsServiceTests {

    private SnsClient snsClient;
    private SnsService snsService;

    @BeforeEach
    void setUp() {
        snsClient = Mockito.mock(SnsClient.class);
        snsService = new SnsService(snsClient);
        // Inyecta el topicArn simulado
        ReflectionTestUtils.setField(snsService, "topicArn", "arn:aws:sns:us-east-1:123456789012:my-topic");
    }

    @Test
    void testPublishMessage() {
        // Simula la respuesta de publish
        PublishResponse mockResponse = PublishResponse.builder().messageId("test-message-id").build();
        Mockito.when(snsClient.publish(any(PublishRequest.class))).thenReturn(mockResponse);

        String result = snsService.publishMessage("mensaje de prueba");

        assertEquals("test-message-id", result);
        Mockito.verify(snsClient).publish(any(PublishRequest.class));
    }
}