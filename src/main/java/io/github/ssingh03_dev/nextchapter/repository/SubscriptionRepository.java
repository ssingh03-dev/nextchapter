package io.github.ssingh03_dev.nextchapter.repository;

import io.github.ssingh03_dev.nextchapter.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndBookId(Long userId, Long bookId);

    Optional<Subscription> findByIdAndUserId(Long id, Long userId);

    List<Subscription> findByUserId(Long userId);

    // need to test if it works, if not then can query whole database and deliverydays will already
    // be converted to a set so easier to filter out then too
    @Query("""
        SELECT s
        FROM Subscription s
        WHERE CONCAT(',', s.deliveryDays, ',') LIKE CONCAT('%,', :day, ',%')
            AND s.deliveryTime = :time
    """)
    List<Subscription> findDueSubscriptions(@Param("day") String day, @Param("time") LocalTime time);
}
