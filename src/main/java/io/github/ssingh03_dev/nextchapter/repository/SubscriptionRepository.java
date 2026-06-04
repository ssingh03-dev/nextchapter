package io.github.ssingh03_dev.nextchapter.repository;

import io.github.ssingh03_dev.nextchapter.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndBookId(Long userId, Long bookId);

    Optional<Subscription> findByIdAndUserId(Long id, Long userId);

    List<Subscription> findByUserId(Long userId);
}
