package io.github.ssingh03_dev.nextchapter.dto.response;

public record CreateChapterResponse(
        Long id,
        Long bookId,
        Integer chapterNumber,
        String title,
        String content
) {
}
