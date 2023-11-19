package com.example.simplesms.config;

import com.example.simplesms.cache.UserCacheRepository;
import com.example.simplesms.config.AppProperties;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.security.SecurityConfig;
import com.example.simplesms.security.TokenProvider;
import com.example.simplesms.security.handler.OAuth2AuthenticationFailureHandler;
import com.example.simplesms.security.handler.OAuth2AuthenticationSuccessHandler;
import com.example.simplesms.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.simplesms.security.service.CustomOAuth2UserService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static com.example.simplesms.fixture.UserFixture.createUser;
import static com.example.simplesms.fixture.UserFixture.userEmailFixture;
import static org.mockito.Mockito.when;

@Import({SecurityConfig.class,
        OAuth2AuthenticationSuccessHandler.class,
        TokenProvider.class,
        HttpCookieOAuth2AuthorizationRequestRepository.class,
        OAuth2AuthenticationFailureHandler.class,
        SwaggerConfig.class
})
@EnableConfigurationProperties(AppProperties.class)
public class TestSecurityConfig {

    @MockBean
    UserRepository userRepository;
    @MockBean
    UserCacheRepository userCacheRepository;

    @MockBean
    CustomOAuth2UserService customOAuth2UserService;

    @BeforeTestMethod
    public void securitySetUp() {
        when(userRepository.findByEmail(userEmailFixture)).thenReturn(
                Optional.of(createUser())
        );

        when(userCacheRepository.findByUsername(userEmailFixture)).thenReturn(
                Optional.empty()
        );
    }

}
