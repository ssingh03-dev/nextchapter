package io.github.ssingh03_dev.nextchapter.dto.response;

// get based on user so every user_id will be the same
public record SubscriptionSummaryResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        boolean active
) {
}
