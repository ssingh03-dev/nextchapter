package io.github.ssingh03_dev.nextchapter.dto.response;

// for getting all chapters of a book, so List<ChapterSummaryResponse>
public record ChapterSummaryResponse(
        Long id,
        Integer chapterNumber,
        String title
) {
}
