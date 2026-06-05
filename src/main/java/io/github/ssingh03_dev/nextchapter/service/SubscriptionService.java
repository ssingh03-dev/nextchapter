package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.request.UpdateSubscriptionRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.SubscriptionDetailResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.SubscriptionMutationResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.SubscriptionSummaryResponse;
import io.github.ssingh03_dev.nextchapter.enums.DeliveryDay;
import io.github.ssingh03_dev.nextchapter.model.AuthToken;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.Subscription;
import io.github.ssingh03_dev.nextchapter.model.User;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import io.github.ssingh03_dev.nextchapter.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SubscriptionService {
    /* TODO work on this next, use one-time token and check if active and not expired,
        then use it to add a subscription between that user and book if it didn't exist before,
        or to delete a subscription with subscription id, or update subscription delivery schedule,
        or to get all subscriptions (token not consumed)
        (and can specify one subscription to get in detail,
        or all subscriptions but in summary of maybe subscription id and book title or something) */

    private final SubscriptionRepository subscriptionRepository;
    private final AuthTokenService authTokenService;
    private final BookRepository bookRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, AuthTokenService authTokenService, BookRepository bookRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.authTokenService = authTokenService;
        this.bookRepository = bookRepository;
    }

    // probably create dto for subscription
    // one for detailed, one for summary when getting all the subscriptions
    // both can have fields that get the book details like title and author
    // most detailed will be get for a subscription, less detailed will be get all subscriptions
    // medium level of detail for outputs of adding, updating, or deleting

    // also have active field, so can make it that you can pause and unpause, or straight up delete

    private SubscriptionDetailResponse toSubscriptionDetailResponse(Subscription subscription) {
        return new SubscriptionDetailResponse(
                subscription.getId(),
                subscription.getBook().getId(),
                subscription.getBook().getTitle(),
                subscription.getBook().getAuthor(),
                subscription.getDeliveryDays().toString(),
                subscription.getDeliveryTime(),
                subscription.getCreatedAt(),
                subscription.getActive()
        );
    }

    private SubscriptionSummaryResponse toSubscriptionSummaryResponse(Subscription subscription) {
        return new SubscriptionSummaryResponse(
                subscription.getId(),
                subscription.getBook().getId(),
                subscription.getBook().getTitle(),
                subscription.getBook().getAuthor(),
                subscription.getActive()
        );
    }

    public SubscriptionMutationResponse addSubscription(
            String rawToken, Long bookId, Set<DeliveryDay> deliveryDays, LocalTime deliveryTime
    ) {
        Optional<AuthToken> authToken = authTokenService.getUsableToken(rawToken);

        if (authToken.isEmpty()) {
            return new SubscriptionMutationResponse(
                    false,
                    "Token unusable.",
                    null
            );
        }

        Book book = bookRepository.findById(bookId).orElse(null);

        if (book == null) {
            return new SubscriptionMutationResponse(
                    false,
                    "Book not found.",
                    null
            );
        }

        AuthToken usableToken = authToken.get();
        User user = usableToken.getUser();

        if (subscriptionRepository
                .findByUserIdAndBookId(user.getId(), bookId)
                .isPresent()) {
            return new SubscriptionMutationResponse(
                    false,
                    "Subscription exists.",
                    null
            );
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setBook(book);
        subscription.setCurrentChapterNumber(1);
        subscription.setDeliveryDays(deliveryDays);
        subscription.setDeliveryTime(deliveryTime);
        subscription.setActive(true);
        subscription.setCreatedAt(Instant.now());

        subscription = subscriptionRepository.save(subscription);

        // make authtoken token become inactive since it was used; probably authTokenService method
        authTokenService.revokeToken(usableToken);

        return new SubscriptionMutationResponse(
                true,
                "Successfully subscribed to " + book.getTitle() + " by " + book.getAuthor() + ".",
                toSubscriptionDetailResponse(subscription)
        );

    }

    // get methods
    // verify with token, but do not use/revoke it

    public Optional<SubscriptionDetailResponse> getSubscriptionById(String rawToken, Long id) {
        Optional<AuthToken> authToken = authTokenService.getUsableToken(rawToken);

        return authToken.flatMap(token -> subscriptionRepository
                .findByIdAndUserId(id, token.getUser().getId())
                .map(this::toSubscriptionDetailResponse));
    }

    public List<SubscriptionSummaryResponse> getSubscriptions(String rawToken) {
        Optional<AuthToken> authToken = authTokenService.getUsableToken(rawToken);

        return authToken.map(token -> subscriptionRepository.findByUserId(token.getUser().getId())
                .stream()
                .map(this::toSubscriptionSummaryResponse)
                .toList()).orElseGet(List::of);
    }

    // update method, one needed since it's a patch

    public SubscriptionMutationResponse updateSubscription(
            String rawToken, Long id, UpdateSubscriptionRequest updateSubscriptionRequest
    ) {
        Optional<AuthToken> authToken = authTokenService.getUsableToken(rawToken);

        if (authToken.isEmpty()) {
            return new SubscriptionMutationResponse(
                    false,
                    "Token unusable.",
                    null
            );
        }

        Optional<Subscription> subscriptionOptional = subscriptionRepository
                .findByIdAndUserId(id, authToken.get().getUser().getId());

        if (subscriptionOptional.isEmpty()) {
            return new SubscriptionMutationResponse(
                    false,
                    "Subscription does not exist.",
                    null
            );
        }

        Subscription subscription = subscriptionOptional.get();

        if (updateSubscriptionRequest.deliveryDays() != null) {
            subscription.setDeliveryDays(updateSubscriptionRequest.deliveryDays());
        }

        if (updateSubscriptionRequest.deliveryTime() != null) {
            subscription.setDeliveryTime(updateSubscriptionRequest.deliveryTime());
        }

        if (updateSubscriptionRequest.active() != null) {
            subscription.setActive(updateSubscriptionRequest.active());
        }

        subscriptionRepository.save(subscription);

        authTokenService.revokeToken(authToken.get());

        return new SubscriptionMutationResponse(
                true,
                "Subscription has been updated.",
                toSubscriptionDetailResponse(subscription)
        );
    }
}
