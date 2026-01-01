package com.example.authlib.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtKeyService {

    private final RSAKey rsaJWK;

    public JwtKeyService() throws Exception {
        // Generate an RSA keypair on startup (in production load from keystore)
        this.rsaJWK = new RSAKeyGenerator(2048)
                .keyID(UUID.randomUUID().toString())
                .generate();
    }

    public RSAKey getPublicJwk() {
        return rsaJWK.toPublicJWK();
    }

    /**
     * Generate a signed JWT for subject with expirySeconds lifetime.
     */
    public String generateToken(String subject, long expirySeconds) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer("http://localhost:8080")
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(expirySeconds)))
                    .claim("scope", "api")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                    claims);

            RSAPrivateKey privateKey = rsaJWK.toPrivateKey();
            RSASSASigner signer = new RSASSASigner(privateKey);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create token", e);
        }
    }
}