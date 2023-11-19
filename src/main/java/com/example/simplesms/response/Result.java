package com.example.simplesms.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Result {
    SUCCESS("성공"), FAIL("실패");

    String message;
}