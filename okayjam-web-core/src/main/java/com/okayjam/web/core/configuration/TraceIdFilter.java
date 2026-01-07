package com.okayjam.web.core.configuration;

import com.okayjam.web.common.util.HttpUtil;
import com.okayjam.web.common.util.JsonUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * configuration
 *
 * @author JamChen
 * @date 2023/10/17 15:59
 **/
@Slf4j
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
@Order(0)
@Component
public class TraceIdFilter implements Filter {

    /**
     * 日志跟踪标识
     */
//    public static final String TRACE_ID = "TRACE_ID";
//    public static final String REQUEST_ID = "TRACE-ID";
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // 设置uuid
//        MDC.put(TRACE_ID, UUID.randomUUID().toString().replaceAll("-", ""));
        String reqUrl = "";
        // 打印请求参数和链接
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String queryString = request.getQueryString();
            // 设置 TRACE_ID
            String traceId = request.getHeader(HttpUtil.REQUEST_ID);
            if (traceId != null && !traceId.isEmpty()) {
                MDC.put(HttpUtil.TRACE_ID, traceId);
            } else {
                MDC.put(HttpUtil.TRACE_ID, UUID.randomUUID().toString().replaceAll("-", ""));
            }
            reqUrl = request.getRequestURL() + (queryString != null ? "?" + queryString : "");

            Map<String, String[]> parameterMap = Collections.emptyMap();
            // 如果是文件上传请求，不打印参数
            if (request.getContentType() != null && !request.getContentType().startsWith("multipart/form-data")) {
                parameterMap = request.getParameterMap();
            }

            Enumeration<String> headerNames = request.getHeaderNames();
            Map<String, String> headerMap = new LinkedHashMap<>();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headerMap.put(headerName, request.getHeader(headerName));
            }

            log.info("req start url:[{}]{}, header:{}, {}", request.getMethod(), reqUrl, JsonUtil.toJsonStr(headerMap),
                    parameterMap.isEmpty() ? "" : ", param:" + JsonUtil.toJsonStr(parameterMap));
        }
        // 计算请求处理耗时
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(servletRequest, servletResponse);
        log.info("req end url:{}, cost:{} s", reqUrl, (System.currentTimeMillis() - startTime) / 1000.0);
    }

    @Override
    public void destroy() {
        MDC.clear();
    }
}
