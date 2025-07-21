package com.chatboot.entities;


public record UserResponse(
        String name,
        String username,
        Boolean disable,
        Boolean locked
) {
}
