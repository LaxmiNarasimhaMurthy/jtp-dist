package com.example.authlib.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/token", "/.well-known/**").permitAll()
                .anyRequest().authenticated()
            )
            // If you want this app to also validate JWTs for protected endpoints you can
            // configure resource server to use the local JWKS:
            // .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()))
            ;
        return http.build();
    }
}