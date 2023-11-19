package com.example.simplesms.fixture;

import com.example.simplesms.domain.post.Hashtag;
import org.springframework.test.util.ReflectionTestUtils;

public class HashtagFixture {

    public static Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    public static Hashtag createHashtag(Long id, String hashtagName) {
        Hashtag hashtag = new Hashtag(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);

        return hashtag;
    }
}
