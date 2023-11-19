package com.example.simplesms.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class UserDetailInfoResponse {
    private  Long id;
    private  String email;
    private  String nickName;
    private  String profileImage;
    private  String description;
    private  Integer feedCount;
    private  Integer passionIndex;
    private  Integer hashtagCount;
    private  List<String> hashtags = new ArrayList<>();
    private  List<String> topHashtags = new ArrayList<>();

    public UserDetailInfoResponse(
            Long id,
            String email,
            String nickName,
            String profileImage,
            String description,
            Long feedCount,
            Long likeCount,
            Long hashtagCount

    ) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.description = description;
        this.feedCount = feedCount.intValue();
        this.passionIndex = (feedCount.intValue() * 10) + (likeCount.intValue() * 10);
        this.hashtagCount = hashtagCount.intValue();
    }

    public void setHashtagsInfo(List<String> hashtags, List<String> topHashtags) {
        this.hashtags.clear();
        this.topHashtags.clear();
        this.hashtags.addAll(hashtags);
        this.topHashtags.addAll(topHashtags);
    }
}
