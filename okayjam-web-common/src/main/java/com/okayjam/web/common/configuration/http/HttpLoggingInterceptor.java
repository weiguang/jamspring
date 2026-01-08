package com.okayjam.web.common.configuration.http;

import com.okayjam.web.common.util.HttpUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * Generic HTTP request/response logging interceptor
 * Used for logging RestClient requests and responses
 *
 * @author JamChen jamchen@tencent.com
 *         2025/12/18
 **/
@Slf4j
public class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Trace ID MDC Key
     */
    private static final String TRACE_ID = HttpUtil.TRACE_ID;

    /**
     * Request ID header key
     */
    private static final String REQUEST_ID = HttpUtil.REQUEST_ID;

    /**
     * Log prefix to distinguish different HTTP clients
     */
    private final String logPrefix;

    public HttpLoggingInterceptor() {
        this.logPrefix = "HTTP";
    }

    public HttpLoggingInterceptor(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    @NotNull
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        // Add Request ID header
        addRequestId(request);

        // Log request
        logRequest(request, body);

        long startTime = System.currentTimeMillis();

        // Execute request
        ClientHttpResponse response = execution.execute(request, body);

        long duration = System.currentTimeMillis() - startTime;

        // Read response body (need to wrap as repeatable response)
        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());

        // Log response
        logResponse(response, responseBody, duration);

        // Return wrapped response (since body has been read)
        return new BufferedClientHttpResponse(response, responseBody);
    }

    /**
     * Add Request ID to request header
     */
    private void addRequestId(HttpRequest request) {
        String requestId = MDC.get(TRACE_ID);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString().replaceAll("-", "");
            MDC.put(TRACE_ID, requestId);
        }
        request.getHeaders().add(REQUEST_ID, requestId);
    }

    /**
     * Log request
     */
    private void logRequest(HttpRequest request, byte[] body) {
        String bodyStr = (body != null && body.length > 0)
                ? new String(body, StandardCharsets.UTF_8)
                : "";
        log.info("[{}] >>> Request: {} {} | Body: {}",
                logPrefix, request.getMethod(), request.getURI(), bodyStr);
    }

    /**
     * Log response
     */
    private void logResponse(ClientHttpResponse response, byte[] responseBody, long duration)
            throws IOException {
        String responseBodyStr = (responseBody != null && responseBody.length > 0)
                ? new String(responseBody, StandardCharsets.UTF_8)
                : "";
        log.info("[{}] <<< Response: {} (Cost: {}ms) | Body: {}",
                logPrefix, response.getStatusCode().value(), duration, responseBodyStr);
    }

    /**
     * Wrapped response to support repeatable body reading
     */
    private record BufferedClientHttpResponse(ClientHttpResponse response, byte[] body) implements ClientHttpResponse {

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }
    }

}
