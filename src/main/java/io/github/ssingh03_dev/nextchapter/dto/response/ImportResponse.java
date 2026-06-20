package io.github.ssingh03_dev.nextchapter.dto.response;

import java.util.List;

public record ImportResponse(
        BookResponse bookResponse,
        List<ChapterSummaryResponse> chapterSummaryResponses,
        boolean created     // true - if book is newly created | false - if it already existed
) {
}
