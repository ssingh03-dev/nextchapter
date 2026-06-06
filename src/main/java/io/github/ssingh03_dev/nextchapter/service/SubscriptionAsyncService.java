package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.model.Subscription;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionAsyncService {

    private final SubscriptionService subscriptionService;

    public SubscriptionAsyncService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Async("subscriptionTaskExecutor")
    public void processSubscriptionAsync(Subscription subscription) {
        subscriptionService.processDueSubscription(subscription);
    }
}
