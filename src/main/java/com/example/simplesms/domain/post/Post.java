package com.example.simplesms.domain.post;


import com.example.simplesms.domain.AbstractEntity;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.response.exception.InvalidParamException;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Column(nullable = true, length = 10000)
    private String content;

    @Setter
    private String postImage;

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<PostComment> comments = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<PostLike> likes = new HashSet<>();

    @ToString.Exclude
    @JoinTable(
            name = "post_hashtag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final Set<Hashtag> hashtags = new LinkedHashSet<>();

    @Builder
    public Post(User user, String content, String postImage, Set<Hashtag> hashtags) {
        if (user == null) throw new InvalidParamException("사용자 정보가 옳바르지 않습니다");
        if (!StringUtils.hasText(postImage)) throw new InvalidParamException("이미지 파일 정보가 옳바르지 않습니다.");

        this.user = user;
        this.content = content;
        this.postImage = postImage;

        if (hashtags.size() > 0) {
            this.getHashtags().addAll(hashtags);
        }
    }

    public void removeLike(PostLike like) {
        this.getLikes().remove(like);
    }

    public void addHashtag(Hashtag hashtag) {
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags) {
        this.getHashtags().addAll(hashtags);
    }

    public void clearHashtags() {
        this.getHashtags().clear();
    }

    public void changePost(String content, String imageName) {
        if(StringUtils.hasText(content)){
            this.content = content;
        }

        if(StringUtils.hasText(imageName)){
            this.postImage = imageName;
        }
    }
}
