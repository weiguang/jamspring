package com.okayjam.web.common.configuration.http;

import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Generic HTTP client configuration properties and factory methods
 * <p>
 * Uses Apache HttpClient 5 as the underlying HTTP client to support connection pooling and advanced features.
 * <p>
 * Configuration example in application.yml:
 * <pre>
 * http:
 *   client:
 *     connect-timeout: 10    # Connect timeout in seconds (default: 10)
 *     read-timeout: 30       # Read timeout in seconds (default: 30)
 *     # Connection pool settings
 *     pool:
 *       max-total: 200       # Max total connections in pool (default: 200)
 *       max-per-route: 50    # Max connections per route (default: 50)
 *       validate-after-inactivity: 2  # Validate connection after inactivity in seconds (default: 2)
 * </pre>
 *
 * @author JamChen jamchen@tencent.com
 * @date 2025/12/18
 **/
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "http.client")
public class HttpClientFactory {

    // ===================== Timeout Settings =====================

    /**
     * Connect timeout in seconds (default: 30s)
     */
    private int connectTimeout = 30;

    /**
     * Read timeout in seconds (default: 120s)
     * Also known as socket timeout or response timeout
     */
    private int readTimeout = 120;

    /**
     * Maximum total connections in the pool (default: 200)
     */
    private int maxTotal = 200;

    /**
     * Maximum connections per route/host (default: 100)
     */
    private int maxPerRoute = 100;

    /**
     * Validate connection after inactivity in seconds (default: 2s)
     * Helps detect stale connections
     */
    private int validateAfterInactivity = 2;

    // ===================== Factory Methods =====================

    /**
     * Create a Jackson message converter that supports both application/json and text/plain content types.
     * This is useful when the server returns JSON data with text/plain content type.
     *
     * @return MappingJackson2HttpMessageConverter configured with multiple media types
     */
    public static MappingJackson2HttpMessageConverter createTextPlainJsonConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN,
                new MediaType("text", "plain", StandardCharsets.UTF_8)
        ));
        return converter;
    }

    /**
     * Create a configured Apache HttpClient 5 with connection pooling
     *
     * @return configured CloseableHttpClient
     */
    public CloseableHttpClient createHttpClient() {
        // Connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(connectTimeout))
                .setSocketTimeout(Timeout.ofSeconds(readTimeout))
                .setValidateAfterInactivity(Timeout.ofSeconds(validateAfterInactivity))
                .build();

        // Connection pool manager
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(maxTotal)
                .setMaxConnPerRoute(maxPerRoute)
                .setDefaultConnectionConfig(connectionConfig)
                .build();

        // Request configuration
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(connectTimeout))
                .setResponseTimeout(Timeout.ofSeconds(readTimeout))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                // Automatically retry on IO exceptions
                .evictExpiredConnections()
                .evictIdleConnections(Timeout.ofMinutes(5))
                .build();

        log.debug("Created Apache HttpClient5 with pool: maxTotal={}, maxPerRoute={}, timeout: connect={}s, read={}s",
                maxTotal, maxPerRoute, connectTimeout, readTimeout);

        return httpClient;
    }

    /**
     * Create HttpComponentsClientHttpRequestFactory for Spring RestClient
     *
     * @return configured HttpComponentsClientHttpRequestFactory
     */
    public HttpComponentsClientHttpRequestFactory createRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(createHttpClient());
    }

    /**
     * Create a pre-configured RestClient.Builder with common settings:
     * - Apache HttpClient 5 with connection pooling
     * - Timeout configuration
     * - Text/plain JSON converter support
     * - Logging interceptor
     *
     * @param baseUrl the base URL for the RestClient
     * @param logPrefix the log prefix for HttpLoggingInterceptor
     * @return RestClient.Builder with common configurations applied
     */
    public RestClient.Builder createRestClientBuilder(String baseUrl, String logPrefix) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(createRequestFactory())
                // Add message converter that supports text/plain JSON responses
                .messageConverters(converters -> converters.add(0, createTextPlainJsonConverter()))
                // Add logging interceptor (should be last to log all headers including auth)
                .requestInterceptor(new HttpLoggingInterceptor(logPrefix));
    }


}
