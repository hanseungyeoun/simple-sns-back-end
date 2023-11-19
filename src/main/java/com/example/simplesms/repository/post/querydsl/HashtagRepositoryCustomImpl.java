package com.example.simplesms.repository.post.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.simplesms.domain.post.QHashtag.hashtag;
import static com.example.simplesms.domain.post.QPost.post;
import static com.example.simplesms.domain.post.QPostLike.postLike;

public class HashtagRepositoryCustomImpl implements HashtagRepositoryCustom {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public HashtagRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<String> findLikeHashTagNameByUserId(Long userId) {
        return queryFactory
                .selectDistinct(
                        hashtag.hashtagName
                ).from(hashtag)
                .join(hashtag.posts, post)
                .join(post.likes, postLike)
                .where(post.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<String> findHashtagNameByUserId(Long id) {
        return queryFactory
                .selectDistinct(
                        hashtag.hashtagName
                ).from(hashtag)
                .join(hashtag.posts, post)
                .where(post.user.id.eq(id))
                .fetch();
    }

    @Override
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
                .toList();
    }


}
