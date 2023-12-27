package com.dastan.spring.reactive.posts.controller.dto;

import java.time.LocalDateTime;

public record PostSummary(String id, String title, LocalDateTime createdAt) {
}