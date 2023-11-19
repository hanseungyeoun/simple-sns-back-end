package com.example.simplesms.dto;

public record UserJoinResponse(
        Long id,
        String email,
        String nickName
) {
}
