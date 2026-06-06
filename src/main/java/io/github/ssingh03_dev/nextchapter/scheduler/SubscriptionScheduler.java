package io.github.ssingh03_dev.nextchapter.scheduler;

import io.github.ssingh03_dev.nextchapter.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionScheduler {        // for when to run, service is for what to do

    private final SubscriptionService subscriptionService;

    public SubscriptionScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(fixedRate = 60000)       // used cause only a db query + checking column
    public void checkDueSubscriptions() {
        subscriptionService.checkDueSubscriptions();
    }
}
