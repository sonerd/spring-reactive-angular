package com.dastan.spring.reactive.posts.controller;

import com.dastan.spring.reactive.comments.persistence.CommentsRepository;
import com.dastan.spring.reactive.common.PaginatedResult;
import com.dastan.spring.reactive.comments.domain.Comment;
import com.dastan.spring.reactive.common.PostNotFoundException;
import com.dastan.spring.reactive.posts.controller.dto.CommentForm;
import com.dastan.spring.reactive.posts.controller.dto.PostSummary;
import com.dastan.spring.reactive.posts.controller.dto.CreatePostCommand;
import com.dastan.spring.reactive.posts.controller.dto.UpdatePostCommand;
import com.dastan.spring.reactive.posts.controller.dto.UpdatePostStatusCommand;
import com.dastan.spring.reactive.posts.domain.Post;
import com.dastan.spring.reactive.posts.domain.Status;
import com.dastan.spring.reactive.posts.persistence.PostsRepository;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;

@RestController
@RequestMapping("/posts")
@Validated
@RequiredArgsConstructor
public class PostController {

    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;


    @GetMapping("")
    public Mono<PaginatedResult<PostSummary>> all(@RequestParam(value = "q", required = false) String q,
                                                  @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                  @RequestParam(value = "limit", defaultValue = "10") int limit) {

        return this.postsRepository.findByKeyword(q, offset, limit)
                                   .collectList()
                                   .zipWith(this.postsRepository.countByKeyword(q), PaginatedResult::new);
    }

    @PostMapping("")
    public Mono<ResponseEntity> create(@RequestBody @Valid CreatePostCommand post) {
        return this.postsRepository.create(post.title(), post.content()).map(saved -> created(URI.create("/posts/" + saved.getId())).build());
    }

    @GetMapping("/{id}")
    public Mono<Post> get(@PathVariable("id") String id) {
        return this.postsRepository.findById(id).switchIfEmpty(Mono.error(new PostNotFoundException(id)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity> update(@PathVariable("id") String id, @RequestBody @Valid UpdatePostCommand post) {
        return this.postsRepository.update(id, post.title(), post.content()).handle((result, sink) -> {
            if (result) {
                sink.next(noContent().build());
            } else {
                sink.error(new PostNotFoundException(id));
            }
        });
    }

    @PutMapping("/{id}/status")
    public Mono<ResponseEntity> updateStatus(@PathVariable("id") String id, @RequestBody @Valid UpdatePostStatusCommand body) {
        return this.postsRepository.updateStatus(id, Status.valueOf(body.status())).handle((result, sink) -> {
            if (result) {
                sink.next(noContent().build());
            } else {
                sink.error(new PostNotFoundException(id));
            }
        });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity> delete(@PathVariable("id") String id) {
        return this.postsRepository.deleteById(id).handle((result, sink) -> {
            if (result) {
                sink.next(noContent().build());
            } else {
                sink.error(new PostNotFoundException(id));
            }
        });
    }

    @GetMapping("/{id}/comments")
    public Flux<Comment> getCommentsOf(@PathVariable("id") String id) {
        return this.postsRepository.findById(id).flatMapMany(p -> Flux.fromIterable(p.getComments()));
    }

    @PostMapping("/{id}/comments")
    public Mono<ResponseEntity> createCommentsOf(@PathVariable("id") String id, @RequestBody @Valid CommentForm form) {
        return this.postsRepository.addComment(id, form.content()).map(saved -> noContent().build());
    }


}
