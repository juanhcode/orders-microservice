package com.develop.orders_microservice.infraestructure.configurations;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest; // Use jakarta.servlet.http for Spring Boot 3+
import java.util.Enumeration;

@Configuration
public class FeignConfig implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    if (name.equalsIgnoreCase(AUTHORIZATION_HEADER)) {
                        String headerValue = request.getHeader(name);
                        template.header(name, headerValue);
                        break;
                    }
                }
            }
        }
    }
}
