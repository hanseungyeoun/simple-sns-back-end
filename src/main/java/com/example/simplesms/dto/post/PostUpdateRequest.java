package com.example.simplesms.dto.post;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

public record PostUpdateRequest(
        @NotEmpty(message = "사진 설명을 입력해 주세요!")
        String content,
        MultipartFile file
) {
}
