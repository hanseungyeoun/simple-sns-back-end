package com.example.simplesms.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


public record UserJoinRequest(
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        String nickName
) {

}


