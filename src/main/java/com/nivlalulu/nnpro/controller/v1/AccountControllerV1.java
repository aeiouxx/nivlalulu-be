package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.ChangeEmailRequestDto;
import com.nivlalulu.nnpro.dto.v1.ChangePasswordRequestDto;
import com.nivlalulu.nnpro.dto.v1.ChangeUsernameRequestDto;
import com.nivlalulu.nnpro.dto.v1.ChangeUsernameResponseDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.service.IJwtTokenService;
import com.nivlalulu.nnpro.service.IUserCredentialsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account controller", description = "Operations for managing a users account")
public class AccountControllerV1 {
    private final IUserCredentialsService credentialsService;
    private final IJwtTokenService jwtTokenService;

    @Operation(
            summary = "User logout",
            description = "Logs out the user by invalidating the `access token` and the `refresh_token`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User logged out"),

    })
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal User user,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        log.debug("User {} logged out", user.getUsername());
        jwtTokenService.logout(user, request, response);
    }

    @Operation(
            summary = "Change password",
            description = "Changes the password for the specified user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The old and new password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangePasswordRequestDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfuly"),
            @ApiResponse(responseCode = "401", description = "Invalid password"),

    })
    @PostMapping("/password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequestDto request,
                               @AuthenticationPrincipal User user) {
        credentialsService.changePassword(user.getUsername(), request.oldPassword(), request.newPassword());
    }

    @Operation(
            summary = "Change username",
            description = "Changes the username for the specified user," +
                    " regenerates the `access token` and the `refresh_token`.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The new username",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangeUsernameRequestDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Username changed successfuly",
                    content = @Content(schema = @Schema(implementation = ChangeUsernameResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username already in use")
    })
    @PostMapping("/username")
    public ChangeUsernameResponseDto changeUsername(@Valid @RequestBody ChangeUsernameRequestDto dto,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               @AuthenticationPrincipal User user) {
        var newUsername = credentialsService.changeUsername(user.getUsername(), dto.newUsername());
        var newTokenDto = jwtTokenService.refreshAndInvalidate(newUsername, request, response);
        return new ChangeUsernameResponseDto(newUsername, newTokenDto);
    }

    @Operation(
            summary = "Change email",
            description = "Changes the email for the specified user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The new email",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangeEmailRequestDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email changed successfuly"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PostMapping("/email")
    public void changeEmail(@Valid @RequestBody ChangeEmailRequestDto request,
                            @AuthenticationPrincipal User user) {
        var newEmail = credentialsService.changeEmail(user.getUsername(), request.newEmail());
    }
}
