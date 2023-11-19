package com.example.simplesms.domain.post;


import com.example.simplesms.domain.AbstractEntity;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.response.exception.InvalidParamException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(
        name = "likes"
        ,indexes = @Index(name = "idx__post_id__user_id", columnList = "post_id, user_id")
)
@NoArgsConstructor
public class PostLike extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private void changePost(Post post) {
        if (this.post != null){
            this.post.getLikes().remove(this);
        }

        this.post = post;
        post.getLikes().add(this);
    }

    @Builder
    public PostLike(Post post, User user) {
        if(post == null) throw new InvalidParamException("포스트 정보가 옳 바르지 않습니다.");
        if(user == null) throw new InvalidParamException("유저 정보가 옳 바르지 않습니다.");

        this.user = user;
        changePost(post);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostLike that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
