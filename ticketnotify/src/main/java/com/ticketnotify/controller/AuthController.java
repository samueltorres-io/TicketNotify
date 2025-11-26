package com.ticketnotify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketnotify.dto.request.UserRequestDto;
import com.ticketnotify.dto.response.LoginResponseDto;
import com.ticketnotify.entity.User;
import com.ticketnotify.service.RedisService;
import com.ticketnotify.service.TokenService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RedisService redisService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid UserRequestDto data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());

        var auth = authenticationManager.authenticate(usernamePassword);
        var user = (User) auth.getPrincipal();

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        redisService.saveRefreshToken(refreshToken, user.getId(), /* Valor do properties */ 1000L * 60 * 60 * 24);

        return ResponseEntity.ok(new LoginResponseDto(user.getId().toString(), accessToken, refreshToken));
    }

}