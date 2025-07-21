package com.chatboot.service;

import com.chatboot.Repositories.TokenRepository;
import com.chatboot.Repositories.UserRepository;
import com.chatboot.controller.AuthRequest;
import com.chatboot.controller.AuthResponse;
import com.chatboot.controller.RegisterRequest;
import com.chatboot.controller.TokenResponse;
import com.chatboot.dto.User;
import com.chatboot.entities.Token;
import com.chatboot.entities.UserResponse;
import com.chatboot.entities.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse register(final RegisterRequest request) {
        final Usuario user = Usuario.builder()
                .name(request.name())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();

        final Usuario savedUser = repository.save(user);
        final String jwtToken = jwtService.generateToken(savedUser);
        final String refreshToken = jwtService.generateRefreshToken(savedUser);

        saveUserToken(savedUser, jwtToken);
        UserResponse userResponse = new UserResponse(
                user.getUsername(),
                user.getName(),
                user.isLocked(),
                user.isDisabled()
        );

        return new TokenResponse(userResponse, jwtToken, refreshToken);
    }

    public TokenResponse authenticate1(final AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        final Usuario user = repository.findByUsername(request.username())
                .orElseThrow();
        final String accessToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        UserResponse userResponse = new UserResponse(
                user.getUsername(),
                user.getName(),
                user.isLocked(),
                user.isDisabled()
        );
        System.out.println("credenciales" + userResponse + accessToken + refreshToken);
        return new TokenResponse(userResponse, accessToken, refreshToken);
    }

    public AuthResponse checkTokenStatus(String token) {
        String username = jwtService.extractUsername(token);
        Usuario user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String refreshToken = jwtService.generateRefreshToken(user); // Opcional si quieres refrescar refresh_token, o puedes mantener el mismo que recibiste

        User usuario = new User(
                user.getName(),
                user.getUsername(),
                user.isDisabled(),
                user.isLocked()
        );

        return new AuthResponse(token, refreshToken, usuario);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    public TokenResponse authenticate(final AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        final Usuario user = repository.findByUsername(request.username())
                .orElseThrow();

        final String token = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, token);

        UserResponse userResponse = new UserResponse(
                user.getUsername(),
                user.getName(),
                user.isLocked(),
                user.isDisabled()
        );
        System.out.println("credenciales" + userResponse + token + refreshToken);
        return new TokenResponse(userResponse, token, refreshToken);
    }

    private void saveUserToken(Usuario user, String jwtToken) {
        final Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .type(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(final Usuario user) {
        final List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUsername());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    public TokenResponse refreshToken(@NotNull final String authentication) {

        if (authentication == null || !authentication.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid auth header");
        }
        final String refreshToken = authentication.substring(7);
        final String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            return null;
        }

        final Usuario user = this.repository.findByUsername(username).orElseThrow();
        final boolean isTokenValid = jwtService.isTokenValid(refreshToken, user);
        if (!isTokenValid) {
            return null;
        }

        final String accessToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        UserResponse userResponse = new UserResponse(
                user.getUsername(),
                user.getName(),
                user.isLocked(),
                user.isDisabled()
        );
        return new TokenResponse(userResponse, accessToken, refreshToken);
    }
}
