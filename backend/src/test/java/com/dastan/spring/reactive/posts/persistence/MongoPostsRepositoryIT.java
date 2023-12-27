package com.dastan.spring.reactive.posts.persistence;

import com.dastan.spring.reactive.config.MongoConfig;
import com.dastan.spring.reactive.RepositoryIT;
import com.dastan.spring.reactive.posts.domain.Post;
import com.dastan.spring.reactive.posts.persistence.MongoPostsRepository;
import com.dastan.spring.reactive.posts.persistence.PostsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MongoPostsRepositoryIT extends RepositoryIT<Post> {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    PostsRepository postsRepository;

    @BeforeEach
    public void setup() throws InterruptedException {
        cleanUp(Post.class);
    }

    @Test
    public void shouldCRUD() {
        var content = "my test content";
        var title = "my test title";

        // create
        final Mono<Post> saved = createPost(content, title);
        final String newTitle = "new title";
        final String newContent = "new content";
        final String postId = saved.block().getId();

        // update
        StepVerifier.create(postsRepository.update(postId, newTitle, newContent)).expectNext(true).expectComplete().verify();

        // read
        postsRepository.findById(postId).as(StepVerifier::create).consumeNextWith(p -> {
            log.info("consuming post:: {}", p);
            assertThat(p.getTitle()).isEqualTo(newTitle);
            assertThat(p.getContent()).isEqualTo(newContent);
            assertThat(p.getLastModifiedDate()).isNotNull();
        }).expectComplete().verify();

        // delete
        StepVerifier.create(postsRepository.deleteById(postId)).expectNext(true).expectComplete().verify();
    }

    @Test
    public void shouldAddComment() {
        var content = "my test content";
        var title = "my test title";
        final Mono<Post> post = createPost(content, title);

        var postId = post.block().getId();
        var myComment = "my comment";

        postsRepository.addComment(postId, myComment).as(StepVerifier::create).expectNext(true).expectComplete().verify();

        postsRepository.findById(postId).as(StepVerifier::create).consumeNextWith(p -> {
            assertThat(p.getComments()).hasSize(1);
            assertThat(p.getComments().get(0).getContent()).isEqualTo(myComment);
        }).expectComplete().verify();
    }

    private Mono<Post> createPost(final String content, final String title) {
        return this.postsRepository.create(title, content);
    }

    @TestConfiguration
    @Import({MongoPostsRepository.class, MongoConfig.class})
    static class TestConfig {
    }
}