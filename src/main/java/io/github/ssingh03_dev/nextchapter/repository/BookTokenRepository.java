package io.github.ssingh03_dev.nextchapter.repository;

import io.github.ssingh03_dev.nextchapter.model.BookToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookTokenRepository extends JpaRepository<BookToken, Long> {
    Optional<BookToken> findByTokenHash(String hashedToken);

    Optional<BookToken> findByBookId(Long bookId);
}
