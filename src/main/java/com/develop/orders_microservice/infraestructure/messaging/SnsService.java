package com.develop.orders_microservice.infraestructure.messaging;

import com.develop.orders_microservice.infraestructure.configurations.SnsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.UUID;

@Service
public class SnsService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topicArn}")
    private String topicArn;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String publishMessage(String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .messageGroupId("order-group")
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();

        PublishResponse response = snsClient.publish(request);
        return response.messageId();
    }
}
