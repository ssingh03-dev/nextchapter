package io.github.ssingh03_dev.nextchapter.controller;

import io.github.ssingh03_dev.nextchapter.dto.request.CreateSubscriptionRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.SubscriptionMutationResponse;
import io.github.ssingh03_dev.nextchapter.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public SubscriptionMutationResponse addSubscription(
            @RequestHeader("Authorization") String bearerToken,
            @Valid @RequestBody CreateSubscriptionRequest createSubscriptionRequest
    ) {
        String rawToken = bearerToken.replace("Bearer ", "");
        return subscriptionService.addSubscription(
                rawToken,
                createSubscriptionRequest.bookId(),
                createSubscriptionRequest.deliveryDays(),
                createSubscriptionRequest.deliveryTime()
        );
    }
}
