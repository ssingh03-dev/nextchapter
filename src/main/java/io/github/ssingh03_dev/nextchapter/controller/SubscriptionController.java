package io.github.ssingh03_dev.nextchapter.controller;

import io.github.ssingh03_dev.nextchapter.dto.request.CreateSubscriptionRequest;
import io.github.ssingh03_dev.nextchapter.dto.request.UpdateSubscriptionRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.SubscriptionDetailResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.SubscriptionMutationResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.SubscriptionSummaryResponse;
import io.github.ssingh03_dev.nextchapter.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // for now invalid token is handled as not found like subscription not found
    @GetMapping("/{subId}")
    public ResponseEntity<SubscriptionDetailResponse> getSubscriptionById(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long subId
    ) {
        String rawToken = bearerToken.replace("Bearer ", "");
        return subscriptionService.getSubscriptionById(rawToken, subId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<SubscriptionSummaryResponse> getSubscriptions(
            @RequestHeader("Authorization") String bearerToken
//            Authentication authentication
    ) {
        String rawToken = bearerToken.replace("Bearer ", "");
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//        String rawToken = jwt.ge;
        return subscriptionService.getSubscriptions(rawToken);
    }

    @PatchMapping("/{subId}")
    public SubscriptionMutationResponse updateSubscription(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long subId,
            @RequestBody UpdateSubscriptionRequest updateSubscriptionRequest
            ) {
        String rawToken = bearerToken.replace("Bearer ", "");
        return subscriptionService.updateSubscription(rawToken, subId, updateSubscriptionRequest);
    }

    @DeleteMapping("/{subId}")
    public SubscriptionMutationResponse deleteSubscription(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long subId
    ) {
        String rawToken = bearerToken.replace("Bearer ", "");
        return subscriptionService.deleteSubscription(rawToken, subId);
    }
}