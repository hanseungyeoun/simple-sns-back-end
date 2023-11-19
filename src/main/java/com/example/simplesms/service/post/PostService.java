package com.example.simplesms.service.post;

import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.domain.post.Post;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.post.PostResponse;
import com.example.simplesms.dto.post.PostResponseWithHashtag;
import com.example.simplesms.dto.post.PostUpdateRequest;
import com.example.simplesms.dto.post.PostWriteRequest;
import com.example.simplesms.repository.post.HashtagRepository;
import com.example.simplesms.repository.post.PostRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.EntityNotFoundException;
import com.example.simplesms.response.exception.IllegalStatusException;
import com.example.simplesms.service.upload.FileUploder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FileUploder fileUploder;
    private final HashtagFactory hashtagFactory;
    private final HashtagRepository hashtagRepository;

    @Transactional
    public PostResponseWithHashtag create(Long userId, PostWriteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("유저 ID(%d)을 찾을 수 없습니다.", userId)));

        String storedFileName = "";

        try {
            storedFileName = fileUploder.storeFile(request.file());
        } catch (IOException e) {
            throw new IllegalStatusException("이미지 파일 저장에 실패 하였습니다.", e.getCause());
        }

        Post post = Post.builder()
                .user(user)
                .content(removeHashtagNames(request.content()))
                .postImage(storedFileName)
                .hashtags(hashtagFactory.createHashtag(request.content()))
                .build();

        return PostResponseWithHashtag.fromEntity(postRepository.save(post));
    }

    @Transactional
    public PostResponseWithHashtag updatePost(Long postId, Long userId, PostUpdateRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post (%d) 을 찾을 수 없습니다.", postId)));

        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new IllegalStatusException(String.format("사용자 (%d )는 post(%d ) 수정 권한이 없습니다.",userId, postId));
        }

        String postImage = "";
        try {
            postImage = fileUploder.storeFile(request.file());
            if (postImage != null) {
                post.setPostImage(postImage);
            }
        } catch (IOException e) {
            throw new IllegalStatusException("이미지 파일 저장에 실패 하였습니다.");
        }

        Set<Long> hashtagIds = post.getHashtags().stream()
                .map(Hashtag::getId)
                .collect(Collectors.toUnmodifiableSet());

        post.changePost(request.content(), postImage);
        post.clearHashtags();
        postRepository.flush();

        hashtagIds.forEach(this::deleteHashtagWithoutPost);

        Set<Hashtag> hashtags = hashtagFactory.createHashtag(request.content());
        post.addHashtags(hashtags);
        return PostResponseWithHashtag.fromEntity(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post (%d) 을 찾을 수 없습니다.", postId)));

        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new IllegalStatusException(String.format("사용자 (%d)는 post(%d) 수정 권한이 없습니다.", userId, postId));
        }

        Set<Long> hashtagIds = post.getHashtags().stream()
                .map(Hashtag::getId)
                .collect(Collectors.toUnmodifiableSet());

        postRepository.deleteByIdAndUser_Id(postId, userId);
        postRepository.flush();

        hashtagIds.forEach(this::deleteHashtagWithoutPost);
    }

    private String removeHashtagNames(String content) {
        if (content == null) {
            return "";
        }

        String newContent = content;
        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());

        while (matcher.find()) {
            newContent = newContent.replace(matcher.group(), "");
        }

        return newContent.trim();
    }

    private void deleteHashtagWithoutPost(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);
        if (hashtag.getPosts().isEmpty()) {
            hashtagRepository.delete(hashtag);
        }
    }

}
