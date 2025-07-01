package com.example.demo.middlewares;

import com.fasterxml.uuid.Generators;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Order(1)
@Component
public class RequestIdAttacher extends HttpFilter {
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "RequestId";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isBlank())
                requestId = Generators.timeBasedEpochGenerator().generate().toString();

            response.addHeader(REQUEST_ID_HEADER, requestId);
            MDC.put(MDC_KEY, requestId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
