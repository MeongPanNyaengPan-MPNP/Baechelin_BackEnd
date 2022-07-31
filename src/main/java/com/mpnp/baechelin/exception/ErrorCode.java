package com.mpnp.baechelin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    FAILED_MESSAGE(500, "E-FAI500","서버에서 오류가 발생하였습니다."),
    INVALID_ACCESS_TOKEN(401, "E-IAT401","유효하지 않은 Access Token 입니다."),
    INVALID_REFRESH_TOKEN(401, "E-IRT401", "유효하지 않은 Refresh Token 입니다."),
    EXPIRED_REFRESH_TOKEN(401, "E-ERT401", "만료된 Refresh Token 입니다."),
    WRONG_TYPE_TOKEN(401, "E-WTT401","잘못된 타입의 JWT 토큰입니다."),
    WRONG_TYPE_SIGNATURE(401, "E-WTS401", "잘못된 JWT 서명입니다."),
    ACCESS_DENIED(401, "E-ACD401","접근이 거부되었습니다."),
    EXPIRED_ACCESS_TOKEN(402, "E-EAT402", "만료된 Access Token 입니다."),
    ACCESS_TOKEN_NOT_EXIST(402, "E-RTN402", "Access Token이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_EXIST(403, "E-RTN403", "Refresh Token이 존재하지 않습니다."),
    ALREADY_LOGIN_ACCOUNT(405, "E-ALA405","다른 계정으로 회원가입 되었습니다."),
    WRONG_INPUT(400, "E_WRI400", "입력 값을 확인해주세요."),
    API_LOAD_FAILURE(500, "E-ALF500", "API 로딩에 실패하였습니다."),
    API_NO_RESULT(500, "E-ANR500", "API 결과가 존재하지 않습니다."),
    NULL_POINTER_EXCEPTION(500, "E-NPE500", "빈 값이 들어올 수 없습니다."),
    IMAGE_PROCESS_FAIL(500,"E-IPF500","이미지 오류 발생"),
    KEYWORD_ARE_NEEDED(500, "E-KAE500", "검색어를 입력해주세요."),
    INVALID_BARRIER_TAG(500, "E-IBT500","배리어 프리 태그를 확인해주세요");

    private final int status;
    private final String code;
    private final String message;
}
