package com.develop.orders_microservice.configurations;

import com.develop.orders_microservice.infraestructure.configurations.FeignConfig;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;

class FeignConfigTest {

    @Test
    void testApplyAddsAuthorizationHeader() {
        // Mock HttpServletRequest
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Enumeration<String> headerNames = Collections.enumeration(Collections.singleton("Authorization"));
        Mockito.when(request.getHeaderNames()).thenReturn(headerNames);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer test-token");

        // Mock ServletRequestAttributes
        ServletRequestAttributes attributes = Mockito.mock(ServletRequestAttributes.class);
        Mockito.when(attributes.getRequest()).thenReturn(request);

        // Set attributes in RequestContextHolder
        RequestContextHolder.setRequestAttributes(attributes);

        // Test FeignConfig
        FeignConfig feignConfig = new FeignConfig();
        RequestTemplate template = new RequestTemplate();
        feignConfig.apply(template);

        assertTrue(template.headers().containsKey("Authorization"));
        assertEquals("Bearer test-token", template.headers().get("Authorization").iterator().next());
    }
}