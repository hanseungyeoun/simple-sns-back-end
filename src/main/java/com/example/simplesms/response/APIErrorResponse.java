package com.example.simplesms.response;

import com.example.simplesms.response.exception.ErrorCode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class APIErrorResponse {
    private Result result;
    private String message;
    private String errorCode;
    private List<FieldError> errors;

    public static APIErrorResponse fail(String message, ErrorCode errorCode) {
        return builder()
                .result(Result.FAIL)
                .message(message)
                .errorCode(errorCode.getCode())
                .errors(List.of())
                .build();
    }

    public static APIErrorResponse fail(ErrorCode errorCode) {
        return builder()
                .result(Result.FAIL)
                .message(errorCode.getErrorMsg())
                .errorCode(errorCode.getCode())
                .errors(List.of())
                .build();
    }

   public static APIErrorResponse fail(ErrorCode errorCode, BindingResult bindingResult) {
        return builder()
                .result(Result.FAIL)
                .message(errorCode.getErrorMsg())
                .errorCode(errorCode.getCode())
                .errors(FieldError.of(bindingResult))
                .build();
    }

    public static APIErrorResponse fail(ErrorCode errorCode, List<FieldError> errors) {
        return builder()
                .result(Result.FAIL)
                .message(errorCode.getErrorMsg())
                .errorCode(errorCode.getCode())
                .errors(errors)
                .build();
    }

    public static APIErrorResponse fail(ErrorCode errorCode, MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        final List<FieldError> errors = FieldError.of(e.getName(), value, e.getErrorCode());
        return builder()
                .result(Result.FAIL)
                .message(errorCode.getErrorMsg())
                .errorCode(errorCode.getCode())
                .errors(errors)
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(String field, String value, String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .toList();
        }
    }

}
