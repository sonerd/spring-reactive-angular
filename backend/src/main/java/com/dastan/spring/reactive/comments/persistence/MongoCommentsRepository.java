package com.dastan.spring.reactive.comments.persistence;

import com.dastan.spring.reactive.comments.domain.Comment;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class MongoCommentsRepository implements CommentsRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public MongoCommentsRepository(final ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Comment> findById(final String id) {
        return mongoTemplate.findById(id, Comment.class);
    }

    @Override
    public Mono<Boolean> update(final String id, final String content) {
        return mongoTemplate.update(Comment.class)
                            .matching(where("id").is(id))
                            .apply(Update.update("content", content))
                            .first()
                            .map(result -> result.getModifiedCount() > 0);
    }
}
