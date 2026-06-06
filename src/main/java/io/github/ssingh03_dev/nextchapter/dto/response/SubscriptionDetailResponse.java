package io.github.ssingh03_dev.nextchapter.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalTime;

public record SubscriptionDetailResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        String deliveryDays,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime deliveryTime,
        Instant createdAt,
        boolean active
) {
}
