package io.github.ssingh03_dev.nextchapter.controller;

import io.github.ssingh03_dev.nextchapter.dto.request.RequestLinkRequest;
import io.github.ssingh03_dev.nextchapter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/request-link")
    public ResponseEntity<String> requestLink(@Valid @RequestBody RequestLinkRequest requestLinkRequest) {
        authService.requestLink(requestLinkRequest.email());
        return ResponseEntity.ok("If the email can be used to sign in, a link has been sent.");
    }
}
