package com.example.simplesms.response;

import com.example.simplesms.response.exception.BaseException;
import com.example.simplesms.response.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class CommonApiControllerAdvice {

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<APIErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        BindingResult bindingResult = e.getBindingResult();
        ErrorCode errorCode = ErrorCode.COMMON_INVALID_PARAMETER;
        APIErrorResponse response = APIErrorResponse.fail(errorCode, bindingResult);

        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * @ModelAttribut 으로 binding error 발생시 BindException 발생한다.
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<APIErrorResponse> handleBindException(BindException e) {
        log.error("handleBindException", e);
        BindingResult bindingResult = e.getBindingResult();
        ErrorCode errorCode = ErrorCode.COMMON_INVALID_PARAMETER;
        APIErrorResponse response = APIErrorResponse.fail(errorCode, bindingResult);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<APIErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        ErrorCode errorCode = ErrorCode.COMMON_INVALID_PARAMETER;
        final APIErrorResponse response = APIErrorResponse.fail(errorCode, e);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<APIErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        ErrorCode errorCode = ErrorCode.COMMON_METHOD_NOT_ALLOWED;
        APIErrorResponse response = APIErrorResponse.fail(e.getMessage(), errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<APIErrorResponse> handleBaseException(BaseException e) {
        log.error("handleBaseException", e);
        final ErrorCode errorCode = e.getErrorCode();
        APIErrorResponse response = APIErrorResponse.fail(e.getMessage(), errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<APIErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        ErrorCode errorCode = ErrorCode.COMMON_SYSTEM_ERROR;
        APIErrorResponse response = APIErrorResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}
