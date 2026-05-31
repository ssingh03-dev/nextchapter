package io.github.ssingh03_dev.nextchapter.dto.response;

import java.time.Instant;

public record BookResponse(
        Long id,
        String title,
        String author,
        Instant createdAt
) {}
