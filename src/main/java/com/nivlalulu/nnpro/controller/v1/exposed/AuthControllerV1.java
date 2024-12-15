package com.nivlalulu.nnpro.controller.v1.exposed;

import com.nivlalulu.nnpro.dto.v1.AuthenticationResponseDto;
import com.nivlalulu.nnpro.dto.v1.LoginRequestDto;
import com.nivlalulu.nnpro.dto.v1.RegistrationRequestDto;
import com.nivlalulu.nnpro.service.IAuthService;
import com.nivlalulu.nnpro.service.IJwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoint for authentication operations")
public class AuthControllerV1 {
    private final IAuthService authService;
    private final IJwtTokenService refreshTokenService;

    @Operation(
            summary = "Login",
            description = "Logs in a user and returns an access token. Sets a refresh token in a HttpOnly cookie.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))),
            tags = { "Authentication" }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful login",
            content = @Content(schema = @Schema(implementation = AuthenticationResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content)
    })
    @PostMapping("/login")
    public AuthenticationResponseDto login(@Valid @RequestBody LoginRequestDto loginRequest,
                                           HttpServletResponse response) {
        return authService.login(loginRequest, response);
    }

    @Operation(
            summary = "Register",
            description = "Registers a new user and returns an access token. Sets a refresh token in a HttpOnly cookie.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = RegistrationRequestDto.class))),
            tags = { "Authentication" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    description = "User registered successfully.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponseDto.class))),
            @ApiResponse(
                    description = "User registration failed because of invalid request format.",
                    responseCode = "400"),
            @ApiResponse(
                    description = "User with provided username or email already exists.",
                    responseCode = "409")
    })
    @PostMapping("/register")
    public AuthenticationResponseDto register(@Valid @RequestBody RegistrationRequestDto registrationRequest,
                                              HttpServletResponse response) {
        return authService.register(registrationRequest, response);
    }

}
