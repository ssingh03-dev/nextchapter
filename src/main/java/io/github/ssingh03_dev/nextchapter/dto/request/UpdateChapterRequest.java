package io.github.ssingh03_dev.nextchapter.dto.request;

// similar to other update requests, it's not required to send all fields since it's a patch request
public record UpdateChapterRequest(
        String title,
        String content
) {
}
