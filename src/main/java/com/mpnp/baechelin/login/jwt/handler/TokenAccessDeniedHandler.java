package com.mpnp.baechelin.login.jwt.handler;

import com.google.gson.JsonObject;
import com.mpnp.baechelin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

// 정상적인 JWT 가 왔지만 권한이 부족한 경우 예외 처리. SecurityConfig 에서 사용됨.
@Component
@RequiredArgsConstructor
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        setResponse(response);
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("timestamp", String.valueOf(LocalDateTime.now()));
        responseJson.addProperty("status", ErrorCode.ACCESS_DENIED.getStatus());
        responseJson.addProperty("code", ErrorCode.ACCESS_DENIED.getCode());
        responseJson.addProperty("error", ErrorCode.ACCESS_DENIED.name());
        responseJson.addProperty("message", ErrorCode.ACCESS_DENIED.getMessage());

        response.getWriter().print(responseJson);
    }
}
