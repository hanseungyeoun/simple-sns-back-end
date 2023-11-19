package com.example.simplesms.dto.post;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record PostWriteRequest(
        @NotEmpty(message = "사진 설명을 입력해 주세요!")
        String content,
        @NotNull(message = "이미지를 선택해주세요!")
        MultipartFile file
) {
}
