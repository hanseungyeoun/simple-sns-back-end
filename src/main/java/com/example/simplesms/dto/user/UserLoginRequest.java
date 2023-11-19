package com.example.simplesms.dto.user;

import javax.validation.constraints.NotBlank;


public record UserLoginRequest(
        @NotBlank
        String email,
        @NotBlank
        String password
) { }

