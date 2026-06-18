package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.response.AuthTokenResponse;
import io.github.ssingh03_dev.nextchapter.model.AuthToken;
import io.github.ssingh03_dev.nextchapter.model.User;
import io.github.ssingh03_dev.nextchapter.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    private final JavaMailSender mailSender;

    public AuthService(UserRepository userRepository, AuthTokenService authTokenService, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.authTokenService = authTokenService;
        this.mailSender = mailSender;
    }

    // Behavior: find-or-create user, issue token only if no active token exists, send email, return the same message either way
    // one active token per user
    // refuse to issue new one till old one expires
    // single-use, so can use it for one subscription, adding or deletion
    // expires in 20 minutes

    // basically
    /*
    * POST /auth/request-link
    * create user if missing
    * if no active token exists, create one and email it
    * otherwise do not create a new one
    * always respond with the same generic message
    * token is one-time use and expires in 20 minutes
    */

    private void emailToken(String to, String token, Instant expiresAt) {
        LocalDateTime expiry = LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedExpiry = expiry.format(formatter);

        SimpleMailMessage message = new SimpleMailMessage();
        // below used it for testing, from testing environment or fake
        // also, the setup session once in constructor so it can time out
        // can configure hardcoded stuff into applications.properties through javamailer
        String from = "testing123@gmail.com";
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Token - Expires in 20 Minutes");
        message.setText("Here is your token: " + token + "\nThis token expires at: " + formattedExpiry);

        mailSender.send(message);
    }

    public void requestLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setCreatedAt(Instant.now());
                    return userRepository.save(newUser);
                });

        // get token from authTokenService
        // first find if users token already exists, if there is check if active or expired
        // if inactive or expired, create new token, add hash to old token row and update everything else accordingly, and email the rawtoken
        // if neither then do nothing
        // if no token for email exists, generate new one and add it as a new row
        // emailToken(email, token);

        Optional<AuthTokenResponse> authTokenResponse = authTokenService.getToken(user);

        if (authTokenResponse.isPresent()) {
            String token = authTokenResponse.get().rawToken();
            AuthToken authToken = authTokenResponse.get().authToken();
            emailToken(user.getEmail(), token, authToken.getExpiresAt());
        }


    }
}
