package io.github.ssingh03_dev.nextchapter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestLinkRequest(
        @NotBlank
        @Email
        String email
) {
}
