package com.chatboot.controller;

import com.chatboot.dto.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refresh_token;
    private User user;

    public AuthResponse(boolean b, String noTokenDetected) {
        this.token = noTokenDetected;
    }
}
