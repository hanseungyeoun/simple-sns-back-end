package com.example.simplesms.dto.user;

import com.example.simplesms.domain.user.User;

public record UserInfoResponse(
        Long id,
        String email,
        String nickName,
        String profileImage,
        String description
) {
    public static UserInfoResponse fromEntity(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getNickName(),
                user.getProfileImage(),
                user.getDescription());
    }
}
