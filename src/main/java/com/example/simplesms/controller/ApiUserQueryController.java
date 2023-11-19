package com.example.simplesms.controller;

import com.example.simplesms.dto.user.UserDetailInfoResponse;
import com.example.simplesms.dto.user.UserInfoResponse;
import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.response.APIDataResponse;
import com.example.simplesms.service.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiUserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/me/details")
    APIDataResponse<UserDetailInfoResponse> getUserInfoDetails(@AuthenticationPrincipal UserPrincipal authentication) {
        return APIDataResponse.success(userQueryService.getUserProfileWithPost(authentication.getId()));
    }

    @GetMapping("/me")
    APIDataResponse<UserInfoResponse> getUserInfo(
            @AuthenticationPrincipal UserPrincipal authentication
    ) {
        return APIDataResponse.success(userQueryService.getUser(authentication.getId()));
    }
}
