package com.dastan.spring.reactive.posts.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePostCommand(@NotBlank String title, @NotBlank String content) {
}
