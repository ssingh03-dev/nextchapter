package io.github.ssingh03_dev.nextchapter.dto.response;

// separate token from response, make it so book is connected to email of user
public record CreateBookResponse (
    Long bookId,
    String title,
    String author
) {}
