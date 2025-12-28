package com.example.authlib.token;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.google.auth.oauth2.IdToken;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class GcpMetadataTokenProvider {

    private static final long BUFFER_SECONDS = 30L;

    // Caffeine cache handles thread-safety and dynamic expiry per audience
    private final Cache<String, IdToken> tokenCache;

    public GcpMetadataTokenProvider() {
        this.tokenCache = Caffeine.newBuilder()
            .expireAfter(new IdTokenExpiryPolicy())
            .build();
    }

    /**
     * Gets a fresh OIDC ID Token for the given audience.
     * Caches the token based on the token's internal "exp" claim.
     */
    public String getToken(String audience) {
        IdToken idToken = tokenCache.get(audience, this::fetchTokenFromMetadata);
        return idToken != null ? idToken.getTokenValue() : null;
    }

    /**
     * Official Google library method to fetch tokens. 
     * Handles Metadata Server headers and Workload Identity automatically.
     */
    private IdToken fetchTokenFromMetadata(String audience) {
        try {
            return IdTokenProvider.defaultInstance()
                    .idTokenWithAudience(audience, Collections.emptyList());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to fetch ID token for audience: " + audience, e);
        }
    }

    /**
     * Custom Expiry Policy: Evicts the token from cache 30 seconds before it expires.
     */
    private static class IdTokenExpiryPolicy implements Expiry<String, IdToken> {
        @Override
        public long expireAfterCreate(String key, IdToken value, long currentTime) {
            long secondsToExpiry = (value.getExpirationTimeSeconds() - (System.currentTimeMillis() / 1000)) - BUFFER_SECONDS;
            return TimeUnit.SECONDS.toNanos(Math.max(secondsToExpiry, 0));
        }

        @Override public long expireAfterUpdate(String k, IdToken v, long t, long d) { return d; }
        @Override public long expireAfterRead(String k, IdToken v, long t, long d) { return d; }
    }
}
