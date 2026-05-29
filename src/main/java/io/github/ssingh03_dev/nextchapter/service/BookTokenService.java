package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.BookToken;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import io.github.ssingh03_dev.nextchapter.repository.BookTokenRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class BookTokenService {     // generate, hash, store, validate, revoke tokens

    private final BookTokenRepository bookTokenRepository;
    private final BookRepository bookRepository;

    private static final SecureRandom secureRandom = new SecureRandom();

    public BookTokenService(BookTokenRepository bookTokenRepository, BookRepository bookRepository) {
        this.bookTokenRepository = bookTokenRepository;
        this.bookRepository = bookRepository;
    }

    private String hashRawToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }

    }

    private String generateRawToken() {
        byte[] randomBytes = new byte[32]; // 256 bits of entropy
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String getNewToken(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        String prefix = book.getTitle()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-");

        String rawToken = prefix + "_" + generateRawToken();
        String hashToken = hashRawToken(rawToken);

        BookToken newBT = new BookToken();
        newBT.setBook(book);
        newBT.setTokenHash(hashToken);
        newBT.setTokenPrefix(prefix);
        newBT.setActive(true);

        return rawToken;
    }
}
