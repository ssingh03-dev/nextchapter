package io.github.ssingh03_dev.nextchapter.repository;

import io.github.ssingh03_dev.nextchapter.model.AuthToken;
import io.github.ssingh03_dev.nextchapter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByUser(User user);
}
