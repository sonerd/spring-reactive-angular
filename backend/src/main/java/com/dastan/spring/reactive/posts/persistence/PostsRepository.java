package com.dastan.spring.reactive.posts.persistence;

import com.dastan.spring.reactive.posts.controller.dto.PostSummary;
import com.dastan.spring.reactive.posts.domain.Post;
import com.dastan.spring.reactive.posts.domain.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostsRepository {

    Flux<Post> findAll();

    Flux<PostSummary> findByKeyword(String keyword, int offset, int limit);

    Mono<Long> countByKeyword(String keyword);

    Mono<Post> findById(String id);

    Mono<Post> create(String title, String content);

    Mono<Boolean> update(String id, String title, String content);

    Mono<Boolean> updateStatus(String id, Status status);

    Mono<Boolean> deleteById(String id);

    Mono<Long> deleteAll();

    Mono<Boolean> addComment(String id, String comment);

    Mono<Boolean> removeComment(String id, String commentId);
}
