package com.example.simplesms.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class APIDataResponse<T> {
    private Result result;
    private String message;
    private T data;

    public APIDataResponse(Result result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public static <T> APIDataResponse<T> success(T data) {
        return success(data, Result.SUCCESS.getMessage());
    }

    public static <T> APIDataResponse<T> success(T data, String message) {
        return new APIDataResponse<>(Result.SUCCESS, message, data);
    }
}