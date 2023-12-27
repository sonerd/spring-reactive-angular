package com.dastan.spring.reactive.comments.persistence;

import com.dastan.spring.reactive.comments.persistence.MongoCommentsRepository;
import com.dastan.spring.reactive.config.MongoConfig;
import com.dastan.spring.reactive.RepositoryIT;
import com.dastan.spring.reactive.comments.domain.Comment;
import com.dastan.spring.reactive.posts.domain.Post;
import com.dastan.spring.reactive.posts.persistence.MongoPostsRepository;
import com.dastan.spring.reactive.posts.persistence.PostsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MongoCommentsRepositoryIT extends RepositoryIT {

    @Autowired
    CommentsRepository commentsRepository;
    @Autowired
    PostsRepository postsRepository;

    @BeforeEach
    public void setup() throws InterruptedException {
        cleanUp(Comment.class);
        cleanUp(Post.class);
    }

    @Test
    public void shouldCRUD() {
        final Mono<Post> post = postsRepository.create("title", "content");

        final Post createdPost = post.block();
        final String postId = createdPost.getId();
        assertThat(postId).isNotNull();

        final Mono<Boolean> myComment = postsRepository.addComment(postId, "myComment");
        assertThat(myComment.block()).isTrue();

        final Mono<Post> updatedPost = postsRepository.findById(createdPost.getId());
        final Mono<Comment> comment = commentsRepository.findById(updatedPost.block().getComments().get(0).getId());
        assertThat(comment.block().getContent()).isEqualTo("myComment");
    }

    @TestConfiguration
    @Import({MongoPostsRepository.class, MongoCommentsRepository.class, MongoConfig.class})
    static class TestConfig {
    }
}