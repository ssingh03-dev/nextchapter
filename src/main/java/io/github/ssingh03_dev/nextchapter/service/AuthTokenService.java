package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.response.AuthTokenResponse;
import io.github.ssingh03_dev.nextchapter.model.AuthToken;
import io.github.ssingh03_dev.nextchapter.model.User;
import io.github.ssingh03_dev.nextchapter.repository.AuthTokenRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    private static final SecureRandom secureRandom = new SecureRandom();

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
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

    private AuthTokenResponse refreshToken(AuthToken authToken) {
        String rawToken = generateRawToken();
        String hashToken = hashRawToken(rawToken);

        authToken.setTokenHash(hashToken);
        authToken.setActive(true);
        authToken.setCreatedAt(Instant.now());

        authToken = authTokenRepository.save(authToken);

        return new AuthTokenResponse(authToken, rawToken);
    }

    // create new token method here
    private AuthTokenResponse createNewToken(User user) {
        String rawToken = generateRawToken();
        String hashToken = hashRawToken(rawToken);

        AuthToken authToken = new AuthToken();
        authToken.setUser(user);
        authToken.setTokenHash(hashToken);
        authToken.setActive(true);
        authToken.setCreatedAt(Instant.now());

        authToken = authTokenRepository.save(authToken);

        return new AuthTokenResponse(authToken, rawToken);
    }

    public Optional<AuthTokenResponse> getToken(User user) {
        Optional<AuthToken> existingToken = authTokenRepository.findByUser(user);

        if (existingToken.isPresent()) {        // user token exists in database
            AuthToken authToken = existingToken.get();

            if (!authToken.getActive() || authToken.getExpiresAt().isBefore(Instant.now())) {
                // this means token has been used, or it expired so a new one will be issued
                return Optional.of(refreshToken(authToken));
            }

            return Optional.empty();
        }

        // user token does not exist in the database, create new row and issue token
        return Optional.of(createNewToken(user));
    }
}
