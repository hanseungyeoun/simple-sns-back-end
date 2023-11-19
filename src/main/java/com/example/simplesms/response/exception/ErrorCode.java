package com.example.simplesms.response.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    COMMON_SYSTEM_ERROR(INTERNAL_SERVER_ERROR,"C001","일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 장애 상황
    COMMON_INVALID_PARAMETER(HttpStatus.BAD_REQUEST,"C002","요청한 값이 올바르지 않습니다."),
    COMMON_ILLEGAL_STATUS(INTERNAL_SERVER_ERROR,"C003","잘못된 상태값입니다."),
    COMMON_ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST,"C004","존재하지 않는 엔티티입니다."),
    COMMON_METHOD_NOT_ALLOWED(NOT_FOUND,"C005","잘 못된 요청입니다. "),
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "C005","잘 못된 요청입니다."),

    DUPLICATED_USER_NAME(CONFLICT,"M001","이미 등록된 회원 입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "M002","Password 가 일치 하지 않습니다."),
    COMMON_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "M005","로그인 실패하였습니다."),
    OAUTH2_AUTHENTICATION_PROCESSING(HttpStatus.UNAUTHORIZED, "M006","OAuth 인증 중 오류가 발생 하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}