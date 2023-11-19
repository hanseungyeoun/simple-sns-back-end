package com.example.simplesms.dto.post;

import com.example.simplesms.dto.comment.CommentResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostQueryResponse {
    private Long id;
    private String content;
    private String postImage;
    private Long likeCount;
    private Boolean isLike;
    private String nickName;
    private String profileImage;
    private LocalDate createdAt;
    Set<String> hashtags = new LinkedHashSet<>();
    private Set<CommentResponse> comments = new LinkedHashSet<>();

    public PostQueryResponse(
            Long id,
            String content,
            String postImage,
            Long likeCount,
            Long isLike,
            String nickName,
            String profileImage,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.content = content;
        this.postImage = postImage;
        this.likeCount = likeCount;
        this.isLike = isLike > 0L;
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.createdAt = createdAt.toLocalDate();
    }

 }
