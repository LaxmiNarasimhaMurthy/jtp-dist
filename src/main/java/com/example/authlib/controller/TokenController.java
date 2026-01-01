package com.example.authlib.controller;

import com.example.authlib.jwt.JwtKeyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TokenController {

    private final JwtKeyService jwtKeyService;

    public TokenController(JwtKeyService jwtKeyService) {
        this.jwtKeyService = jwtKeyService;
    }

    // Simple token endpoint. Accepts JSON with "sub" (subject); returns OAuth2-like token response.
    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> token(@RequestBody Map<String, String> body) {
        String sub = body.getOrDefault("sub", "user");
        long expiresIn = 3600;
        String token = jwtKeyService.generateToken(sub, expiresIn);
        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", expiresIn
        );
    }
}