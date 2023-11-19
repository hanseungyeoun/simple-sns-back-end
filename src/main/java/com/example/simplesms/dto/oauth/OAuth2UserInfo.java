package com.example.simplesms.dto.oauth;

import com.example.simplesms.domain.user.AuthProvider;

public record OAuth2UserInfo(
        AuthProvider provider,
        String id,
        String email,
        String nickName,
        String imageUrl
){}
