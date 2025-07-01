package com.example.demo.middlewares;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Order(2)
@Component
public class RequestResponseLoggingFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("Request Received : {} {}", request.getMethod(), request.getRequestURI());
        chain.doFilter(request, response);
        log.info("Request Completed : {} {}", request.getMethod(), request.getRequestURI());
    }
}
