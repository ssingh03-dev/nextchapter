package io.github.ssingh03_dev.nextchapter.repository;

import io.github.ssingh03_dev.nextchapter.model.BookToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTokenRepository extends JpaRepository<BookToken, Long> {
}
