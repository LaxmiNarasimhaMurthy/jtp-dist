package com.example.authlib.config;

import com.example.authlib.token.GcpMetadataTokenProvider;
import com.example.authlib.interceptor.RestAuthInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.config.BeanPostProcessor;

@AutoConfiguration
@ConditionalOnProperty(name = "gcp.auth.enabled", havingValue = "true", matchIfMissing = true)
public class GcpAuthAutoConfiguration {

    @Bean
    public GcpMetadataTokenProvider gcpMetadataTokenProvider() {
        return new GcpMetadataTokenProvider();
    }

    @Bean
    public RestAuthInterceptor restAuthInterceptor(GcpMetadataTokenProvider provider) {
        return new RestAuthInterceptor(provider);
    }

    // Automatically adds the interceptor to any RestTemplate found in the app
    @Bean
    public static BeanPostProcessor restTemplateInterceptorProcessor(RestAuthInterceptor interceptor) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof RestTemplate restTemplate) {
                    restTemplate.getInterceptors().add(interceptor);
                }
                return bean;
            }
        };
    }
}

com.example.authlib.config.GcpAuthAutoConfiguration
com.example.authlib.security.SpringSecurityConfig


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping("/{id}")
    // 'SCOPE_orders.read' matches the permission string in the JWT from Google
    @PreAuthorize("hasAuthority('SCOPE_orders.read')") 
    public Order getOrder(@PathVariable String id) {
        return new Order(id, "Completed");
    }
}


spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Google's public keys to verify the signature
          jwk-set-uri: "www.googleapis.com"
          # Validates that Google signed the token
          issuer-uri: "https://accounts.google.com"
