package com.blue.blueojbackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 为每个请求生成追踪 ID，并记录请求状态和耗时。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLogFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";

    private static final String TRACE_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        long startTime = System.currentTimeMillis();

        MDC.put(TRACE_ID, traceId);
        response.setHeader(TRACE_HEADER, traceId);

        try {
            log.info(
                    "request start: method={}, path={}, ip={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr()
            );
            filterChain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            log.info(
                    "request end: method={}, path={}, status={}, cost={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    cost
            );
            MDC.remove(TRACE_ID);
        }
    }
}
