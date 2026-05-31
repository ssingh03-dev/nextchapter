package io.github.ssingh03_dev.nextchapter.dto.response;

public record CreateBookResponse (
    Long bookId,
    String title,
    String author,
    String rawToken
) {}
