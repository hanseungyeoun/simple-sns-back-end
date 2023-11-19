package com.example.simplesms.domain.post;

import com.example.simplesms.domain.AbstractEntity;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.response.exception.InvalidParamException;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;


@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "comment"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity

public class PostComment extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Setter
    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(nullable = false)
    private String comment;

    private void changePost(Post post) {
        if (this.post != null) {
            this.post.getComments().remove(this);
        }

        this.post = post;
        post.getComments().add(this);
    }

    @Builder
    public PostComment(Post post, User user, String comment) {
        if (post == null) throw new InvalidParamException("포스트 정보가 옳 바르지 않습니다.");
        if (user == null) throw new InvalidParamException("유저 정보가 옳 바르지 않습니다.");
        if (!StringUtils.hasText(comment)) throw new InvalidParamException("댓글 정보가 옳 바르지 않습니다.");

        this.user = user;
        this.comment = comment;
        changePost(post);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostComment that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
