package com.example.simplesms.dto.user;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public record UserProfileUpdateRequest(

        @NotBlank
        String nickName,

        @NotBlank
        String description,

        MultipartFile file
) {}
