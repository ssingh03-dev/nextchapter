package io.github.ssingh03_dev.nextchapter.dto.response;

public record SubscriptionMutationResponse (
        boolean mutated,
        String message,
        SubscriptionDetailResponse subscriptionDetailResponse
) {
}
