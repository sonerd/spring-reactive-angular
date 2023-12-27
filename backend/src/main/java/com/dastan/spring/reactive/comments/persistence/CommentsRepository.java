package com.dastan.spring.reactive.comments.persistence;

import com.dastan.spring.reactive.comments.domain.Comment;
import reactor.core.publisher.Mono;

public interface CommentsRepository {

    Mono<Comment> findById(String id);

    Mono<Boolean> update(String id, String content);
}
