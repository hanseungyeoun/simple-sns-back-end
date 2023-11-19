package com.example.simplesms.security;

import com.example.simplesms.cache.UserCacheRepository;
import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.security.filter.TokenAuthenticationFilter;
import com.example.simplesms.security.handler.OAuth2AuthenticationFailureHandler;
import com.example.simplesms.security.handler.OAuth2AuthenticationSuccessHandler;
import com.example.simplesms.security.handler.RestAuthenticationEntryPoint;
import com.example.simplesms.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.simplesms.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final UserCacheRepository userCacheRepository;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;
    private final TokenProvider tokenProvider;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .sessionManagement(configurer -> configurer.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .antMatchers("/",
                                "/error",
                                "/favicon.ico",
                                "/**/*.png",
                                "/**/*.gif",
                                "/**/*.svg",
                                "/**/*.jpg",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**"
                        )
                        .permitAll()
                        .antMatchers(
                                HttpMethod.GET,
                                "/api/v1/posts",
                                "/images/**",
                                "/s3/**"
                        ).permitAll()
                        .mvcMatchers(
                                HttpMethod.POST,
                                "/api/v1/users/login",
                                "/api/v1/users/join"
                        ).permitAll()
                        .anyRequest().authenticated()

                )
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*").and()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .exceptionHandling(error -> error.authenticationEntryPoint(new RestAuthenticationEntryPoint()))
                .addFilterBefore(
                        new TokenAuthenticationFilter(
                                userDetailsService(userRepository, userCacheRepository),
                                tokenProvider
                        ),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, UserCacheRepository userCacheRepository) {
        return username -> userCacheRepository.findByUsername(username).orElseGet(
                () -> {
                    UserPrincipal userPrincipal = userRepository.findByEmail(username).map(UserPrincipal::fromEntity).orElseThrow(
                            () -> new UsernameNotFoundException(username));
                    userCacheRepository.save(userPrincipal);
                    return userPrincipal;
                }
        );
    }
}
