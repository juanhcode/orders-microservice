package com.develop.orders_microservice.infraestructure.configurations;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Value("${jwt.secret.key}")
            private String jwtSecretKey;

            @Override
            public void apply(RequestTemplate template) {
                if (!jwtSecretKey.isEmpty()) {
                    template.header("Authorization", "Bearer " + jwtSecretKey);
                } else {
                    throw new IllegalArgumentException("JWT secret key is not provided");
                }
            }
        };
    }
}
