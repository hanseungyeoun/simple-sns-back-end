package com.example.simplesms.security.service;

import com.example.simplesms.domain.user.AuthProvider;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.oauth.OAuth2UserInfo;
import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.BaseException;
import com.example.simplesms.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (!StringUtils.hasText(oAuth2UserInfo.email())) {
            throw new BaseException("Email not found from OAuth2 provider", ErrorCode.OAUTH2_AUTHENTICATION_PROCESSING);
        }

        User newUser = null;
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.email());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.of(registrationId))) {
                throw new BaseException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.", ErrorCode.OAUTH2_AUTHENTICATION_PROCESSING);
            }
            newUser = updateExistingUser(user, oAuth2UserInfo);
        } else {
            newUser = registerNewUser(oAuth2UserInfo, registrationId);
        }

        return UserPrincipal.fromEntity(newUser, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        User user = User.ByOAuthBuilder()
                .email(oAuth2UserInfo.email())
                .nicName(oAuth2UserInfo.nickName())
                .profileImage(oAuth2UserInfo.imageUrl())
                .password(UUID.randomUUID().toString())
                .description("")
                .providerId(oAuth2UserInfo.id())
                .provider(AuthProvider.of(registrationId))
                .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setNickName(oAuth2UserInfo.nickName());
        existingUser.setProfileImage(oAuth2UserInfo.imageUrl());
        return userRepository.save(existingUser);
    }

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        AuthProvider provider = AuthProvider.of(registrationId);
        return provider.convert(attributes);
    }

}
