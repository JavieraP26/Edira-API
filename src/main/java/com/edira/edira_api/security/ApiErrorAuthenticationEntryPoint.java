package com.edira.edira_api.security;

import com.edira.edira_api.shared.error.ApiError;
import com.edira.edira_api.shared.error.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiErrorAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log =
            LoggerFactory.getLogger(ApiErrorAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public ApiErrorAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                  HttpServletResponse response,
                  AuthenticationException authException) throws IOException, ServletException {

        String path = request.getRequestURI();
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        ApiError body = ApiError.of(status, ErrorCode.UNAUTHORIZED, "No autenticado. Inicia sesión.", path);
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), body);
        log.warn("401 UNAUTHORIZED path={} errorId={}", path, body.errorId());

    }
}
