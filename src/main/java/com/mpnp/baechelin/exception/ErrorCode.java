package com.mpnp.baechelin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SUCCESS_MESSAGE(200, "S-SUC200","SUCCESS"),
//    NOT_FOUND_MESSAGE(500, "BE001", "NOT FOUND"),
    FAILED_MESSAGE(500, "E-FAI500","서버에서 오류가 발생하였습니다."),
    INVALID_ACCESS_TOKEN(401, "E-IAT401","유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN(401, "E-IRT401", "유효하지 않은 Refresh Token입니다."),
    NOT_EXPIRED_TOKEN_YET(401, "E-NET401", "만료되지 않은 JWT 토큰입니다."),
    EXPIRED_TOKEN(401, "E-EXT401", "만료된 JWT 토큰입니다."),
    WRONG_TYPE_TOKEN(401, "E-WTT401","잘못된 JWT 토큰입니다."),
    WRONG_TYPE_SIGNATURE(401, "E-WTS401", "잘못된 JWT 서명입니다."),
    ACCESS_DENIED(401, "E-ACD401","접근이 거부되었습니다."),
    TOKEN_NOT_EXIST(401, "E-TNE401", "토큰이 존재하지 않습니다."),
    ALREADY_LOGIN_ACCOUNT(400, "E-ALA400","다른 계정으로 로그인 되었습니다.");

    private final int status;
    private final String code;
    private final String message;
}
