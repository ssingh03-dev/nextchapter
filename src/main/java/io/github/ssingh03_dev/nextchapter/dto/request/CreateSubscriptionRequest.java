package io.github.ssingh03_dev.nextchapter.dto.request;

import io.github.ssingh03_dev.nextchapter.enums.DeliveryDay;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Set;

public record CreateSubscriptionRequest(
        @NotNull Long bookId,
        @NotNull Set<DeliveryDay> deliveryDays,
        @NotNull LocalTime deliveryTime
) {
}
