package com.dastan.spring.reactive.users.persistence;

import com.dastan.spring.reactive.users.domain.User;
import reactor.core.publisher.Mono;

public interface UsersRepository {
    Mono<User> findByUsername(String username);

    Mono<User> create(User user);

    Mono<Long> deleteAll();
}
