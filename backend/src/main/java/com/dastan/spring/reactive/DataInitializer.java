package com.dastan.spring.reactive;

import com.dastan.spring.reactive.posts.persistence.PostsRepository;
import com.dastan.spring.reactive.users.domain.User;
import com.dastan.spring.reactive.users.persistence.UsersRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final PostsRepository postsRepository;
    private final UsersRepository users;
    private final PasswordEncoder passwordEncoder;

    @EventListener(value = ApplicationReadyEvent.class)
    public void init() {
        initUsers();
        initPosts();
    }

    private void initUsers() {
        log.info("start users initialization  ...");
        this.users
                .deleteAll()
                .thenMany(
                        Flux
                                .just("user", "admin")
                                .flatMap(
                                        username -> {
                                            List<String> roles = "user".equals(username)
                                                                 ? Arrays.asList("ROLE_USER")
                                                                 : Arrays.asList("ROLE_USER", "ROLE_ADMIN");

                                            User user = User.builder()
                                                            .roles(roles)
                                                            .username(username)
                                                            .password(passwordEncoder.encode("password"))
                                                            .email(username + "@example.com")
                                                            .build();
                                            return this.users.create(user);
                                        }
                                )
                )
                .log()
                .subscribe(
                        null,
                        null,
                        () -> log.info("done users initialization...")
                );
    }
    private void initPosts() {
        log.info("start post data initialization  ...");
        this.postsRepository.deleteAll()
                            .thenMany(Flux.just("Post one", "Post two").flatMap(title -> this.postsRepository.create(title, "content of " + title)))
                            .log()
                            .subscribe(null, null, () -> log.info("done post initialization..."));
    }
}
