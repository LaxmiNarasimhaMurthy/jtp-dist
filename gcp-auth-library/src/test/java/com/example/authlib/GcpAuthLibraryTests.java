import com.google.auth.oauth2.IdToken;
import com.google.auth.oauth2.IdTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GcpMetadataTokenProviderTest {

    private GcpMetadataTokenProvider tokenProvider;
    private MockedStatic<IdTokenProvider> mockedIdTokenProvider;
    private IdTokenProvider mockProviderInstance;

    @BeforeEach
    void setUp() {
        tokenProvider = new GcpMetadataTokenProvider();
        // 1. Mock the static defaultInstance() call
        mockedIdTokenProvider = mockStatic(IdTokenProvider.class);
        mockProviderInstance = mock(IdTokenProvider.class);
        mockedIdTokenProvider.when(IdTokenProvider::defaultInstance).thenReturn(mockProviderInstance);
    }

    @AfterEach
    void tearDown() {
        // Static mocks must be closed to avoid leaking into other tests
        mockedIdTokenProvider.close();
    }

    @Test
    void testGetToken_WithFreshFetch() throws IOException {
        String audience = "https://target-service.com";
        String expectedTokenValue = "mock-jwt-token";

        // 2. Create a mock IdToken that matches your library's expiry logic
        IdToken mockIdToken = mock(IdToken.class);
        when(mockIdToken.getTokenValue()).thenReturn(expectedTokenValue);
        // Set expiry far in the future (current time + 3600s)
        when(mockIdToken.getExpirationTimeSeconds()).thenReturn((System.currentTimeMillis() / 1000) + 3600);

        // 3. Stub the provider to return our mock token
        when(mockProviderInstance.idTokenWithAudience(eq(audience), anyList()))
                .thenReturn(mockIdToken);

        // Act
        String resultToken = tokenProvider.getToken(audience);

        // Assert
        assertNotNull(resultToken);
        assertEquals(expectedTokenValue, resultToken);
        
        // Verify that it actually called the metadata provider
        verify(mockProviderInstance, times(1)).idTokenWithAudience(eq(audience), anyList());
    }

    @Test
    void testGetToken_ReturnsFromCache() throws IOException {
        String audience = "https://cached-service.com";
        IdToken mockIdToken = mock(IdToken.class);
        when(mockIdToken.getTokenValue()).thenReturn("cached-token");
        when(mockIdToken.getExpirationTimeSeconds()).thenReturn((System.currentTimeMillis() / 1000) + 3600);

        when(mockProviderInstance.idTokenWithAudience(eq(audience), anyList())).thenReturn(mockIdToken);

        // Call twice
        tokenProvider.getToken(audience);
        tokenProvider.getToken(audience);

        // Verify provider was only called ONCE due to Caffeine cache
        verify(mockProviderInstance, times(1)).idTokenWithAudience(eq(audience), anyList());
    }
}
