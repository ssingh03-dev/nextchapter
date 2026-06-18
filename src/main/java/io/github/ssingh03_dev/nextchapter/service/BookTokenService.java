package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.BookToken;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import io.github.ssingh03_dev.nextchapter.repository.BookTokenRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

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
//        String prefix = book.getTitle()
//                .trim()
//                .toLowerCase()
//                .replaceAll("\\s+", "-");
        // book title is not unique, so we need to use book id instead
        String prefix = "bk_" + book.getId().toString();

        String rawToken = prefix + "_" + generateRawToken();
        String hashToken = hashRawToken(rawToken);

        Optional<BookToken> bookToken = bookTokenRepository.findByBookId(bookId);

        BookToken newBT;

        if (bookToken.isPresent()) {
            newBT = bookToken.get();
            newBT.setTokenHash(hashToken);
        } else {
            newBT = new BookToken();
            newBT.setBook(book);
            newBT.setTokenHash(hashToken);
            newBT.setTokenPrefix(prefix);
            newBT.setActive(true);
            newBT.setCreatedAt(Instant.now());
        }

        bookTokenRepository.save(newBT);

        return rawToken;
    }

    public Optional<Book> findBookByToken(String rawToken) {
        String hashedToken = hashRawToken(rawToken);

        return bookTokenRepository.findByTokenHash(hashedToken)
                .filter(BookToken::getActive)
                .map(BookToken::getBook);
    }       // acts as a way to validate the token too

//    public boolean validateToken(String rawToken, Long bookId) {
//        String hashedToken = hashRawToken(rawToken);
//        BookToken bookToken = bookTokenRepository.findByTokenHash(hashedToken)
//                .orElseThrow(() -> new RuntimeException("Invalid token"));
//
//        return bookToken.getBook().getId().equals(bookId) && bookToken.getActive();
//    }

//    public void revokeToken(String rawToken) {
//        String hashedToken = hashRawToken(rawToken);
//        BookToken bookToken = bookTokenRepository.findByTokenHash(hashedToken)
//                .orElseThrow(() -> new RuntimeException("Invalid token"));
//
//        bookToken.setActive(false);
//        bookTokenRepository.save(bookToken);
//    }
}
