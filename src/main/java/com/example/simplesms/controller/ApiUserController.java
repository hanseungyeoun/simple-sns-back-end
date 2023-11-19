package com.example.simplesms.controller;

import com.example.simplesms.dto.user.*;
import com.example.simplesms.response.APIDataResponse;
import com.example.simplesms.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserService userService;

    @PostMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIDataResponse<UserInfoResponse> updateUserProfile(
            @Valid UserProfileUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        return APIDataResponse.success(userService.updateUserProfile(authentication.getId(), request));
    }

    @PostMapping("/join")
    public APIDataResponse<Long> join(@Valid @RequestBody UserJoinRequest request) {
        Long id = userService.join(request);
        return APIDataResponse.success(id);
    }

    @PostMapping("/login")
    public APIDataResponse<LoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return APIDataResponse.success(userService.login(request.email(), request.password()));
    }

    @PostMapping("/{userId}")
    public APIDataResponse<UserInfoResponse> update(@PathVariable Long userId, UserProfileUpdateRequest request) {
        return APIDataResponse.success(userService.updateUserProfile(userId, request));
    }
}
