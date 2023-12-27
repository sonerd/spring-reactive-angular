package com.dastan.spring.reactive.common;

import java.util.List;

public record PaginatedResult<T>(List<T> data, Long count) {
}