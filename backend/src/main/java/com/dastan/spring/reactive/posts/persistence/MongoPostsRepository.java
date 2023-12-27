package com.dastan.spring.reactive.posts.persistence;

import com.dastan.spring.reactive.comments.domain.Comment;
import com.dastan.spring.reactive.posts.controller.dto.PostSummary;
import com.dastan.spring.reactive.posts.domain.Post;
import com.dastan.spring.reactive.posts.domain.Status;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class MongoPostsRepository implements PostsRepository {

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String STATUS = "status";
    private static final String COMMENTS = "comments";
    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public MongoPostsRepository(final ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Post> findAll() {
        return mongoTemplate.findAll(Post.class);
    }

    @Override
    public Flux<PostSummary> findByKeyword(final String keyword, final int offset, final int limit) {
        return mongoTemplate
                .find(
                        query(where("title").regex(".*" + keyword + ".*", "i"))
                                .skip(offset)
                                .limit(limit),
                        Post.class
                )
                .map(it -> new PostSummary(it.getId(), it.getTitle(), it.getCreatedDate()));
    }

    @Override
    public Mono<Long> countByKeyword(final String keyword) {
        return mongoTemplate.count(query(where(TITLE).regex(".*" + keyword + ".*", "i")), Post.class);
    }

    @Override
    public Mono<Post> findById(final String id) {
        return mongoTemplate.findById(id, Post.class);
    }

    @Override
    public Mono<Post> create(final String title, final String content) {
        return mongoTemplate.insert(Post.builder().title(title).content(content).build());
    }

    @Override
    public Mono<Boolean> update(final String id, final String title, final String content) {
        return mongoTemplate.update(Post.class)
                            .matching(where(ID).is(id))
                            .apply(Update.update(TITLE, title).set(CONTENT, content))
                            .all()
                            .map(result -> result.getModifiedCount() == 1L);
    }

    @Override
    public Mono<Boolean> updateStatus(final String id, final Status status) {
        return mongoTemplate.update(Post.class)
                            .matching(where(ID).is(id))
                            .apply(Update.update(STATUS, status))
                            .all()
                            .map(result -> result.getModifiedCount() == 1L);
    }

    @Override
    public Mono<Boolean> deleteById(final String id) {
        return mongoTemplate.remove(Post.class).matching(where(ID).is(id)).all().map(result -> result.getDeletedCount() == 1L);
    }

    @Override
    public Mono<Long> deleteAll() {
        return mongoTemplate.remove(Post.class).all().map(DeleteResult::getDeletedCount);
    }

    @Override
    public Mono<Boolean> addComment(final String postId, final String commentText) {
        var comment = mongoTemplate.insert(Comment.builder().content(commentText).build());
        return comment.flatMap(c -> mongoTemplate.update(Post.class)
                                                 .matching(where(ID).is(postId))
                                                 .apply(new Update().push(COMMENTS, c))
                                                 .all()
                                                 .map(result -> result.getModifiedCount() == 1L));

    }

    @Override
    public Mono<Boolean> removeComment(final String id, final String commentId) {
        var comment = mongoTemplate.findById(commentId, Comment.class);
        return comment.flatMap(c -> mongoTemplate.update(Post.class)
                                                 .matching(where(ID).is(id))
                                                 .apply(new Update().pull(COMMENTS, comment))
                                                 .all()
                                                 .map(result -> result.getModifiedCount() == 1L));
    }
}
