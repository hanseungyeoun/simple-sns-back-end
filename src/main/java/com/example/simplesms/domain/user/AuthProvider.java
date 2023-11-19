package com.example.simplesms.domain.user;

import com.example.simplesms.dto.oauth.OAuth2UserInfo;
import com.example.simplesms.response.exception.BaseException;
import com.example.simplesms.response.exception.ErrorCode;
import com.example.simplesms.response.exception.IllegalStatusException;

import java.util.Arrays;
import java.util.Map;

public enum AuthProvider {
    KAKAO {
        @Override
        public OAuth2UserInfo convert(Map<String, Object> attributes) {
            String id = String.valueOf(attributes.get("id"));
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) account.get("profile");

            if (account == null || profile == null) {
                return null;
            }

            String email = (String) account.get("email");
            String nickName = (String) profile.get("nickname");
            String imageUrl = (String) profile.get("thumbnail_image_url");
            return new OAuth2UserInfo(AuthProvider.KAKAO, id, email, nickName, imageUrl);
        }

    },
    NAVER {
        @Override
        public OAuth2UserInfo convert(Map<String, Object> attributes){
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response == null) {
                return null;
            }

            String id = (String) response.get("id");
            String email = (String) response.get("email");
            String nickName = (String) response.get("nickname");
            String imageUrl = (String) response.get("profile_image");
            return new OAuth2UserInfo(AuthProvider.NAVER, id, email, nickName, imageUrl);
        }
    },
    GOOGLE {
        @Override
        public OAuth2UserInfo convert(Map<String, Object> attributes){
            String id = (String) attributes.get("sub");
            String email = (String) attributes.get("email");
            String nickName =  (String) attributes.get("name");
            String imageUrl =(String) attributes.get("picture");
            return new OAuth2UserInfo(AuthProvider.GOOGLE,id, email, nickName, imageUrl);
        }
    },
    LOCAL {
        @Override
        public OAuth2UserInfo convert(Map<String, Object> attributes) {
            return null;
        }
    };


    public abstract OAuth2UserInfo convert(Map<String, Object> attributes);

    public static AuthProvider of(String providerId) {
        if(providerId == null){
            throw new IllegalStatusException("provider 가 존재 하지 않습니다.");
        }

        return  Arrays.stream(AuthProvider.values())
                .filter(v -> v.name().equals(providerId.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new BaseException("지원하지 않는 소셜 로그인 입니다. (" + providerId + ")", ErrorCode.OAUTH2_AUTHENTICATION_PROCESSING));

    }
}
