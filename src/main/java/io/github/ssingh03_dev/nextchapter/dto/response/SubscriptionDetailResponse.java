package io.github.ssingh03_dev.nextchapter.dto.response;

import java.time.Instant;
import java.time.LocalTime;

public record SubscriptionDetailResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        String deliveryDays,
        LocalTime deliveryTime,
        Instant createdAt,
        boolean active
) {
}
