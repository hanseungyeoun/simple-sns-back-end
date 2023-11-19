package com.example.simplesms.repository.query;

import com.example.simplesms.dto.comment.CommentResponse;
import com.example.simplesms.dto.post.HashtagResponse;
import com.example.simplesms.dto.post.PostQueryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.simplesms.domain.post.QHashtag.hashtag;
import static com.example.simplesms.domain.post.QPost.post;
import static com.example.simplesms.domain.post.QPostComment.postComment;
import static com.example.simplesms.domain.post.QPostLike.postLike;
import static com.example.simplesms.domain.user.QUser.user;
import static com.querydsl.core.types.ExpressionUtils.count;
import static java.util.stream.Collectors.*;

@Repository
@Transactional(readOnly = true)
public class PostQueryRepository {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public PostQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<PostQueryResponse> findPostAll(Long userId, Pageable pageable) {

        List<PostQueryResponse> result = queryFactory
                .select(Projections.constructor(PostQueryResponse.class,
                        post.id,
                        post.content,
                        post.postImage,
                        likeCount(),
                        getIsLike(userId),
                        user.nickName,
                        user.profileImage,
                        post.createdAt
                ))
                .from(post)
                .join(post.user, user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        setCommentAndHashtag(result);
        return PageableExecutionUtils.getPage(result, pageable, this::postTotalCount);
    }

    private static JPQLQuery<Long> getIsLike(Long userId) {
        return JPAExpressions
                .select(count(postLike.id))
                .from(postLike)
                .where(postLike.post.eq(post)
                        .and(postLike.user.id.eq(userId)));
    }

    public Page<PostQueryResponse> findPostAllByUserId(Long userId, Pageable pageable) {
        List<PostQueryResponse> result = queryFactory
                .select(Projections.constructor(PostQueryResponse.class,
                        post.id,
                        post.content,
                        post.postImage,
                        likeCount(),
                        getIsLike(userId),
                        user.nickName,
                        user.profileImage,
                        post.createdAt
                ))
                .from(post)
                .join(post.user, user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .where(userIdEq(userId))
                .fetch();

        setCommentAndHashtag(result);
        return PageableExecutionUtils.getPage(result, pageable, () -> postTotalCountByUserId(userId));
    }

    private static JPQLQuery<Long> likeCount() {
        return JPAExpressions
                .select(count(postLike.id))
                .from(postLike)
                .where(postLike.post.eq(post));
    }

    public Page<PostQueryResponse> findPostAllByUserIdAndHashtagName(Long userId, String hashtagName, Pageable pageable) {
        List<PostQueryResponse> result = queryFactory
                .select(Projections.constructor(PostQueryResponse.class,
                        post.id,
                        post.content,
                        post.postImage,
                        likeCount(),
                        getIsLike(userId),
                        user.nickName,
                        user.profileImage,
                        post.createdAt
                ))
                .from(post)
                .join(post.user, user)
                .join(post.hashtags, hashtag)
                .where(hashtagNameContainsIgnoreCase(hashtagName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        setCommentAndHashtag(result);
        return PageableExecutionUtils.getPage(result, pageable, () -> postTotalUserIdAndHashtagName(hashtagName));
    }

    private List<CommentResponse> findCommentIn(List<Long> postIds) {
        return queryFactory
                .select(Projections.constructor(
                                CommentResponse.class,
                                postComment.id,
                                post.id,
                                postComment.user.id,
                                postComment.comment
                        )
                )
                .from(postComment)
                .join(postComment.post, post)
                .join(postComment.user, user)
                .where(postComment.post.id.in(postIds))
                .orderBy(postComment.createdAt.desc())
                .fetch();
    }

    public List<HashtagResponse> findHashtagNamesIn(List<Long> postIds) {
        return queryFactory
                .select(Projections.constructor(
                        HashtagResponse.class,
                        post.id,
                        hashtag.hashtagName)
                )
                .from(post)
                .join(post.hashtags, hashtag)

                .where(post.id.in(postIds))
                .orderBy(hashtag.createdAt.desc())
                .fetch();
    }

    private Long postTotalCount() {
        return queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();
    }

    private Long postTotalCountByUserId(Long userId) {
        return queryFactory
                .select(post.count())
                .from(post)
                .join(post.user, user)
                .where(userIdEq(userId))
                .fetchOne();
    }

    private Long postTotalUserIdAndHashtagName(String hashtagName) {
        return queryFactory
                .select(post.count())
                .from(post)
                .join(post.hashtags, hashtag)
                .where(hashtagNameContainsIgnoreCase(hashtagName))
                .fetchOne();
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? post.user.id.eq(userId) : null;
    }

    private BooleanExpression hashtagNameContainsIgnoreCase(String hashtagName) {
        return hashtagName != null ? hashtag.hashtagName.containsIgnoreCase(hashtagName) : null;
    }

    private void setCommentAndHashtag(List<PostQueryResponse> result) {
        List<Long> postIds = toPostIds(result);

        if (postIds.size() == 0)
            return;

        List<CommentResponse> comments = findCommentIn(postIds);
        setComments(result, comments);

        List<HashtagResponse> hashtags = findHashtagNamesIn(postIds);
        setHashtag(result, hashtags);
    }

    private List<Long> toPostIds(List<PostQueryResponse> posts) {
        return posts.stream()
                .map(PostQueryResponse::getId)
                .toList();
    }

    private void setComments(List<PostQueryResponse> result, List<CommentResponse> comments) {
        Map<Long, Set<CommentResponse>> commentMap = toCommentMap(comments);
        result.forEach(post -> post.setComments(commentMap.get(post.getId())));
    }

    private void setHashtag(List<PostQueryResponse> result, List<HashtagResponse> hashtags) {
        Map<Long, LinkedHashSet<String>> map = toHashtagMap(hashtags);
        result.forEach(post -> post.setHashtags(map.get(post.getId())));
    }

    private Map<Long, Set<CommentResponse>> toCommentMap(List<CommentResponse> comments) {
        return comments.stream()
                .collect(groupingBy(CommentResponse::postId, toCollection(LinkedHashSet::new)));
    }

    private Map<Long, LinkedHashSet<String>> toHashtagMap(List<HashtagResponse> hashtags) {
        return hashtags.stream()
                .collect(groupingBy(HashtagResponse::id, mapping(HashtagResponse::hashtagName,
                                        toCollection(LinkedHashSet::new))));
    }
}
