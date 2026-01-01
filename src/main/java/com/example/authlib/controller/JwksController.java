package com.example.authlib.controller;

import com.example.authlib.jwt.JwtKeyService;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwksController {

    private final JwtKeyService jwtKeyService;

    public JwksController(JwtKeyService jwtKeyService) {
        this.jwtKeyService = jwtKeyService;
    }

    @GetMapping(path = "/.well-known/jwks.json", produces = "application/json")
    public String jwks() {
        JWKSet set = new JWKSet(jwtKeyService.getPublicJwk());
        return set.toJSONObject().toString();
    }
}