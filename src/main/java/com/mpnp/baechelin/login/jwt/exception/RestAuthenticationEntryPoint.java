package com.mpnp.baechelin.login.jwt.exception;

import com.google.gson.JsonObject;
import com.mpnp.baechelin.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

// 인증되지 않은 유저가 요청을 했을 때 동작하는 클래스
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String exception = (String)request.getAttribute("exception");

        if(exception == null) {
            setResponse(response, ErrorCode.FAILED_MESSAGE);
        }
        //잘못된 타입의 토큰인 경우
        else if(exception.equals(ErrorCode.WRONG_TYPE_TOKEN.getCode())) {
            setResponse(response, ErrorCode.WRONG_TYPE_TOKEN);
        }
        else if(exception.equals(ErrorCode.WRONG_TYPE_SIGNATURE.getCode())) {
            setResponse(response, ErrorCode.WRONG_TYPE_SIGNATURE);
        }
        //토큰 만료된 경우
        else if(exception.equals(ErrorCode.EXPIRED_ACCESS_TOKEN.getCode())) {
            setResponse(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
        }
        // 토큰이 존재하지 않을 경우
        else if (exception.equals(ErrorCode.ACCESS_TOKEN_NOT_EXIST.getCode())) {
            setResponse(response, ErrorCode.ACCESS_TOKEN_NOT_EXIST);
        }
        else {
            setResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    //한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("timestamp", String.valueOf(LocalDateTime.now()));
        responseJson.addProperty("status", errorCode.getStatus());
        responseJson.addProperty("code", errorCode.getCode());
        responseJson.addProperty("error", errorCode.name());
        responseJson.addProperty("message", errorCode.getMessage());

        response.getWriter().print(responseJson);
    }
}
