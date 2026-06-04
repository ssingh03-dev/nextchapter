package io.github.ssingh03_dev.nextchapter.dto.request;

import io.github.ssingh03_dev.nextchapter.enums.DeliveryDay;

import java.time.LocalTime;
import java.util.Set;

public record UpdateSubscriptionRequest(
        Set<DeliveryDay> deliveryDays,
        LocalTime deliveryTime
) {
}
