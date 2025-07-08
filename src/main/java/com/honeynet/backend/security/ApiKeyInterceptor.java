package com.honeynet.backend.security;

import com.honeynet.backend.config.ApiKeyConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final ApiKeyConfig apiKeyConfig;

    public ApiKeyInterceptor(ApiKeyConfig apiKeyConfig) {
        this.apiKeyConfig = apiKeyConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String providedKey = request.getHeader("x-api-key");
        String expectedKey = apiKeyConfig.getApiKey();
        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println("All request headers: " );
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println(header + ": " + request.getHeader(header));
        }

        // ✅ Logging for debugging
        System.out.println("➡️ Request URI: " + requestURI);
        System.out.println("🔑 Provided API Key: " + providedKey);
        System.out.println("🔐 Expected API Key: " + expectedKey);

        // ✅ Allow OPTIONS and test/public endpoints to bypass auth
        if (
                "OPTIONS".equalsIgnoreCase(method) ||
                        requestURI.startsWith("/api/test") ||
                        requestURI.equals("/api/blockchain-logs/list") ||
                        requestURI.startsWith("/api/summaries") ||
                        requestURI.startsWith("/actuator/health") ||
                        requestURI.startsWith("/api/public")
        ) {
            System.out.println("✅ Request allowed without API key (whitelisted path or OPTIONS)");
            return true;
        }

        if (expectedKey != null && expectedKey.equals(providedKey)) {
            System.out.println("✅ API key matched");
            return true;
        }

        System.out.println("❌ API key mismatch — rejecting request");
        sendUnauthorizedResponse(response);
        return false;


    }

    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid API Key");
    }
}