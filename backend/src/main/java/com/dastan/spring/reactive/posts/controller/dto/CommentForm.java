package com.dastan.spring.reactive.posts.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentForm(@NotBlank String content) {
}