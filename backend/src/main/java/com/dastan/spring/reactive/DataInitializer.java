package com.dastan.spring.reactive;

import com.dastan.spring.reactive.posts.persistence.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final PostsRepository postsRepository;

    @EventListener(value = ApplicationReadyEvent.class)
    public void init() {
        initPosts();
    }

    private void initPosts() {
        log.info("start post data initialization  ...");
        this.postsRepository.deleteAll()
                            .thenMany(Flux.just("Post one", "Post two").flatMap(title -> this.postsRepository.create(title, "content of " + title)))
                            .log()
                            .subscribe(null, null, () -> log.info("done post initialization..."));
    }
}
