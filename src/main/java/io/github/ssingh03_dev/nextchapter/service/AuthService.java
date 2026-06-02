package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.model.User;
import io.github.ssingh03_dev.nextchapter.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class AuthService {
    private final UserRepository userRepository;

    // below used it for testing, from testing environment or fake
    // also, the setup session once in contructor so it can time out
    private final String from = "testing123@gmail.com";

    private final JavaMailSender mailSender;

    public AuthService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
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

    private void emailToken(String to, String token, Timestamp expiresAt) {
        LocalDateTime expiry = expiresAt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedExpiry = expiry.format(formatter);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Token - Expires in 20 Minutes");
        message.setText("Here is your token: " + token + "\nThis token expires at: " + formattedExpiry);

        mailSender.send(message);
    }   // not tested yet

    public void requestLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setCreatedAt(Instant.now());
                    return userRepository.save(newUser);
                });

        // get token from authTokenService
        // first find token by email, if there is check if active or expired
        // if inactive or expired, create new token, add hash to old token row and update everything else accordingly, and email the rawtoken
        // if neither then do nothing
        // if no token for email exists, generate new one and add it as a new row
        // emailToken(email, token);
    }
}
