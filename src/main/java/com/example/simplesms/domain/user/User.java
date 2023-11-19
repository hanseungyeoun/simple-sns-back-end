package com.example.simplesms.domain.user;

import com.example.simplesms.domain.AbstractEntity;
import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.response.exception.InvalidParamException;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Entity
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    @Setter
    private String profileImage;

    @Column(length = 100)
    @Setter
    private String nickName;

    @Column(length = 100)
    @Setter
    private String description;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @ToString.Exclude
    @JoinTable(
            name = "user_hashtag",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "hashtagId")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final Set<Hashtag> hashtags = new LinkedHashSet<>();

    @Builder(builderClassName = "BySingUpBuilder", builderMethodName = "BySingUpBuilder")
    public User(String email, String password, String nickName) {
        if (!StringUtils.hasText(email)) throw new InvalidParamException(" UserAccount email is Empty");
        if (!StringUtils.hasText(password)) throw new InvalidParamException(" UserAccount password is Empty");

        this.email = email;
        this.password = password;
        this.nickName = nickName;
    }

    @Builder(builderClassName = "ByOAuthBuilder", builderMethodName = "ByOAuthBuilder")
    public User(
            String email,
            String password,
            String profileImage,
            String nicName,
            String description,
            String providerId,
            AuthProvider provider
    ) {
        if (!StringUtils.hasText(email)) throw new InvalidParamException("Email은 필수 값입니다. ");
        if (!StringUtils.hasText(password)) throw new InvalidParamException("password 필수 값입니다.");

        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.nickName = nicName;
        this.description = description;
        this.providerId = providerId;
        this.provider = provider;
    }

    public void changeProfile(String nickName, String profileImage, String description) {
        if(StringUtils.hasText(nickName))
            this.nickName = nickName;

        if(StringUtils.hasText(profileImage))
            this.profileImage = profileImage;

        if(StringUtils.hasText(description))
            this.description = description;
    }
}
