package com.example.simplesms.service.post;

import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.repository.post.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagFactory {

    private final HashtagRepository hashtagRepository;

    public Set<Hashtag> createHashtag(String content) {
        Set<String> hashtagNames = parseHashtagNames(content);
        Set<Hashtag> hashtags = new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toUnmodifiableSet());

        hashtagNames.forEach(newHashtagName -> {
            if (!existingHashtagNames.contains(newHashtagName)) {
                hashtags.add(new Hashtag(newHashtagName));
            }
        });

        return hashtags;
    }

    private Set<String> parseHashtagNames(String content) {
        if (content == null) {
            return Set.of();
        }

        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());
        Set<String> result = new HashSet<>();

        while (matcher.find()) {
            result.add(matcher.group().replace("#", ""));
        }

        return Set.copyOf(result);
    }
}
