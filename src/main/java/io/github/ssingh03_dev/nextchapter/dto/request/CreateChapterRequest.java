package io.github.ssingh03_dev.nextchapter.dto.request;

import jakarta.validation.constraints.NotNull;

// chapter number is auto-generated, boo id is from the path
public record CreateChapterRequest(
        @NotNull String title,
        @NotNull String content
) {
}
