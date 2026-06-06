package io.github.ssingh03_dev.nextchapter.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.ssingh03_dev.nextchapter.enums.DeliveryDay;

import java.time.LocalTime;
import java.util.Set;

public record UpdateSubscriptionRequest(
        Set<DeliveryDay> deliveryDays,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime deliveryTime,
        Boolean active
) {
}
