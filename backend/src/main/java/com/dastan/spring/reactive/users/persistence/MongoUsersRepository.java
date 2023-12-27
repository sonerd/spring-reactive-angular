package com.dastan.spring.reactive.users.persistence;

import com.dastan.spring.reactive.users.domain.User;
import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MongoUsersRepository implements UsersRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<User> findByUsername(final String username) {
        return this.mongoTemplate.findOne(query(where("username").is(username)), User.class);
    }

    @Override
    public Mono<User> create(final User user) {
        return this.mongoTemplate.insert(user);
    }

    @Override
    public Mono<Long> deleteAll() {
        return this.mongoTemplate.remove(User.class).all().map(DeleteResult::getDeletedCount);
    }
}
