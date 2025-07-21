package com.chatboot.controller;

import com.chatboot.entities.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        UserResponse user,
        @JsonProperty("token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
