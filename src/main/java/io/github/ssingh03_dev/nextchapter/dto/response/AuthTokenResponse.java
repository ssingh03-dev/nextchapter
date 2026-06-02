package io.github.ssingh03_dev.nextchapter.dto.response;

import io.github.ssingh03_dev.nextchapter.model.AuthToken;

public record AuthTokenResponse(
        AuthToken authToken,
        String rawToken
) {
}
