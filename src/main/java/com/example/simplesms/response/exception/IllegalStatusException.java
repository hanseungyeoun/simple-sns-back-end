package com.example.simplesms.response.exception;


public class IllegalStatusException extends BaseException {

    public IllegalStatusException() {
        super(ErrorCode.COMMON_ILLEGAL_STATUS);
    }

    public IllegalStatusException(String message) {
        super(message, ErrorCode.COMMON_ILLEGAL_STATUS);
    }

    public IllegalStatusException( String message,Throwable cause ) {
        super(message, ErrorCode.COMMON_ILLEGAL_STATUS, cause);
    }
}
