package com.chatboot.controller;

public record RegisterRequest(String name,
                              String username,
                              String password) {
}
