import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.net.URI;

public class RestAuthInterceptor implements ClientHttpRequestInterceptor {

    private final GcpMetadataTokenProvider tokenProvider;
    private final String customHeaderName;

    // Default to X-GCP-Auth-Token if no property is provided
    public RestAuthInterceptor(GcpMetadataTokenProvider tokenProvider, 
                               @Value("${gcp.auth.header-name:X-GCP-Auth-Token}") String customHeaderName) {
        this.tokenProvider = tokenProvider;
        this.customHeaderName = customHeaderName;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();

        if ("https".equalsIgnoreCase(uri.getScheme()) || isInternalTraffic(uri)) {
            String audience = resolveAudience(uri);
            String token = tokenProvider.getToken(audience);

            if (token != null) {
                // Refactored: Set the custom header instead of Bearer Auth
                request.getHeaders().set(customHeaderName, token);
            }
        }

        return execution.execute(request, body);
    }

    private String resolveAudience(URI uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();

        StringBuilder aud = new StringBuilder(scheme).append("://").append(host);
        if (port != -1 && !isStandardPort(scheme, port)) {
            aud.append(":").append(port);
        }
        return aud.toString();
    }

    private boolean isStandardPort(String scheme, int port) {
        return ("http".equalsIgnoreCase(scheme) && port == 80) || 
               ("https".equalsIgnoreCase(scheme) && port == 443);
    }

    private boolean isInternalTraffic(URI uri) {
        return uri.getHost().endsWith(".local") || uri.getHost().endsWith(".svc.cluster.local");
    }
}
