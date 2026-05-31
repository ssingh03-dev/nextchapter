package io.github.ssingh03_dev.nextchapter.dto.request;

// different from createbookrequest since the fields are optional/nullable
public record UpdateBookRequest(
        String title,
        String author
) {
}
