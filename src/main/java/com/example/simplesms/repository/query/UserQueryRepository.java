package com.example.simplesms.repository.query;

import com.example.simplesms.dto.user.UserDetailInfoResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.simplesms.domain.post.QHashtag.hashtag;
import static com.example.simplesms.domain.post.QPost.post;
import static com.example.simplesms.domain.post.QPostLike.postLike;
import static com.example.simplesms.domain.user.QUser.user;
import static com.querydsl.core.types.ExpressionUtils.count;

@Repository
@Transactional(readOnly = true)
public class UserQueryRepository {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public UserQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public UserDetailInfoResponse findUserDetailByUserId(Long userId) {
        UserDetailInfoResponse result = queryFactory
                .select(Projections.constructor(UserDetailInfoResponse.class,
                        user.id,
                        user.email,
                        user.email,
                        user.profileImage,
                        user.description,
                        JPAExpressions
                                .select(count(post.id))
                                .from(post)
                                .where(post.id.eq(userId)),
                        JPAExpressions
                                .select(count(postLike.id))
                                .from(postLike)
                                .where(postLike.user.id.eq(userId)),
                        JPAExpressions
                                .select(count(hashtag.id)).distinct()
                                .from(hashtag)
                                .join(hashtag.posts, post)
                                .where(post.user.id.eq(userId))
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        List<String> hashtags = findHashtagNameByUserId(userId);
        List<String> topHashtags = findTop2ByUserId(userId);

        result.setHashtagsInfo(hashtags, topHashtags);
        return result;
    }

//    public List<String> findLikeHashTagNameByUserId(Long userId) {
//        return queryFactory
//                .selectDistinct(
//                        hashtag.hashtagName
//                ).from(hashtag)
//                .join(hashtag.posts, post)
//                .join(post.likes, postLike)
//                .where(post.user.id.eq(userId))
//                .fetch();
//    }

    public List<String> findHashtagNameByUserId(Long id) {
        return queryFactory
                .selectDistinct(
                        hashtag.hashtagName
                ).from(hashtag)
                .join(hashtag.posts, post)
                .where(post.user.id.eq(id))
                .fetch();
    }

    public List<String> findTop2ByUserId(Long id) {
        StringPath aliasQuantity = Expressions.stringPath("count1");
        List<Tuple> result = queryFactory
                .select(
                        hashtag.hashtagName, hashtag.hashtagName.count().as("count1")
                ).from(hashtag)
                .join(hashtag.posts, post)
                .where(post.user.id.eq(id))
                .groupBy(hashtag.hashtagName)
                .orderBy(aliasQuantity.desc())
                .limit(2)
                .fetch();

        return result.stream()
                .map(tuple -> tuple.get(hashtag.hashtagName))
                .collect(Collectors.toList());
    }
}
