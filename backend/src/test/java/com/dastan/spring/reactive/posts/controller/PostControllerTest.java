package com.dastan.spring.reactive.posts.controller;

import com.dastan.spring.reactive.comments.persistence.CommentsRepository;
import com.dastan.spring.reactive.config.SecurityConfig;
import com.dastan.spring.reactive.posts.controller.dto.CreatePostCommand;
import com.dastan.spring.reactive.posts.controller.dto.PostSummary;
import com.dastan.spring.reactive.posts.persistence.PostsRepository;
import com.dastan.spring.reactive.users.persistence.UsersRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebFluxTest(controllers = PostController.class)
@Slf4j
public class PostControllerTest {

    @Autowired
    WebTestClient client;

    @MockBean
    PostsRepository posts;

    @MockBean
    UsersRepository users;

    @MockBean
    CommentsRepository comments;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    public void getAllPostsShouldReturn200AndPosts() {
        final String query = "post";
        final int offset = 0;
        final int limit = 10;

        given(posts.countByKeyword(query)).willReturn(Mono.just(200L));
        given(posts.findByKeyword(query, offset, limit)).willReturn(Flux.just(new PostSummary("1", "my first post", LocalDateTime.now())));

        client.get()
              .uri("/posts?q=" + query)
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody()
              .jsonPath("$.data[0].title")
              .isEqualTo("my first post")
              .jsonPath("$.data[0].id")
              .isEqualTo("1");

        verify(this.posts, times(1)).findByKeyword(query, offset, limit);
        verify(this.posts, times(1)).countByKeyword(query);
    }


    @Test
    public void getPostByIdShouldReturn404ForNotExistingPost() {
        var id = "1";
        given(this.posts.findById(id)).willReturn(Mono.empty());

        client.get().uri("/posts/" + id).exchange().expectStatus().isNotFound();
    }

    @Test
    public void createPostShouldReturn422ForInvalidInput() {
        final CreatePostCommand create = new CreatePostCommand("", "content");
        client.post().uri("/posts").body(BodyInserters.fromValue(create)).exchange().expectStatus().isEqualTo(422);
    }

    @TestConfiguration
    @Import(SecurityConfig.class)
    static class TestConfig {
    }

}