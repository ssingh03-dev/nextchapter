package io.github.ssingh03_dev.nextchapter.dto.internal;

import java.util.List;

public record ParsedMarkdown(
        String title,
        String author,
        String email,
        String rawToken,
        List<ParsedChapter> chapters
) {
}
