package io.github.ssingh03_dev.nextchapter.dto.response;

import java.time.Instant;

public record ChapterResponse(
        Long id,
        Integer chapterNumber,
        String title,
        String content,
        Instant createdAt
) {
}
