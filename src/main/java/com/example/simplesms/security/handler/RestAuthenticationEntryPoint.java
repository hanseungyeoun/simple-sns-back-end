package com.example.simplesms.security.handler;

import com.example.simplesms.response.APIErrorResponse;
import com.example.simplesms.response.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
        ErrorCode commonUnauthorized = ErrorCode.COMMON_UNAUTHORIZED;
        APIErrorResponse fail = APIErrorResponse.fail(commonUnauthorized);
        ObjectMapper objectMapper = new ObjectMapper();
        String out = objectMapper.writeValueAsString(fail);

        response.setContentType("application/json");
        response.setStatus(commonUnauthorized.getStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(out);
    }
}
