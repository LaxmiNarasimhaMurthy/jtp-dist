package com.example.authlib.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtKeyService {

    private final RSAKey rsaJWK;

    public JwtKeyService() throws JOSEException {
        // Generate a 2048-bit RSA keypair on startup.
        // In 2026, 2048-bit is the minimum recommended length for RS256.
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

            // Build JWS Header with the specific Key ID (kid)
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(rsaJWK.getKeyID())
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);

            // FIX: Pass the RSAKey object directly to the signer.
            // This is the preferred method in modern Nimbus versions.
            RSASSASigner signer = new RSASSASigner(rsaJWK);

            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign token with RSA key", e);
        }
    }
}
