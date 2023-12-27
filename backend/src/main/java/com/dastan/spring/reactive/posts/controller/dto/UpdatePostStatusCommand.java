package com.dastan.spring.reactive.posts.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostStatusCommand(@NotBlank String status) {
}
