package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    /* TODO work on this next, use one-time token and check if active and not expired,
        then use it to add a subscription between that user and book if it didn't exist before,
        or to delete a subscription with subscription id, or update subscription delivery schedule,
        or to get all subscriptions (token not consumed)
        (and can specify one subscription to get in detail,
        or all subscriptions but in summary of maybe subscription id and book title or something) */

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    // probably create dto for subscription
    // one for detailed, one for summary when getting all the subscriptions
    // both can have fields that get the book details like title and author
    // most detailed will be get for a subscription, less detailed will be get all subscriptions
    // medium level of detail for outputs of adding, updating, or deleting

    // also have active field, so can make it that you can pause and unpause, or straight up delete
}
