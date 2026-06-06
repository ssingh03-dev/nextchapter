package io.github.ssingh03_dev.nextchapter.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.ssingh03_dev.nextchapter.enums.DeliveryDay;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Set;

public record CreateSubscriptionRequest(
        @NotNull Long bookId,
        @NotNull Set<DeliveryDay> deliveryDays,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @NotNull LocalTime deliveryTime
) {
}
