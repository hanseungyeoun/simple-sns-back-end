package com.example.simplesms.domain.post;

import com.example.simplesms.domain.AbstractEntity;
import com.example.simplesms.domain.post.Post;
import com.example.simplesms.response.exception.InvalidParamException;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "hashtagName", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Hashtag extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @ToString.Exclude
    @ManyToMany(mappedBy = "hashtags")
    private final Set<Post> posts = new LinkedHashSet<>();

    @Column(nullable = false)
    private String hashtagName;

    @Builder
    public Hashtag(String hashtagName) {
        if(!StringUtils.hasText(hashtagName)) {
            throw new InvalidParamException("해시태그명 정보가 옳 바르지 않습니다.");
        }

        this.hashtagName = hashtagName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hashtag that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
