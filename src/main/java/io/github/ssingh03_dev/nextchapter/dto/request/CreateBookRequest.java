package io.github.ssingh03_dev.nextchapter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookRequest(
        @NotNull String title,
        @NotNull String author,
        @NotBlank @Email String email
) {}
