package com.nivlalulu.nnpro.controller.v1.exposed;

import com.nivlalulu.nnpro.dto.v1.CreatePasswordResetTokenDto;
import com.nivlalulu.nnpro.dto.v1.ResetPasswordRequestDto;
import com.nivlalulu.nnpro.service.IUserCredentialsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1/password-reset")
@RequiredArgsConstructor
public class PasswordResetControllerV1 {
    private final IUserCredentialsService userCredentialsService;

   @Operation(
        summary = "Create Password Reset Token",
        description = "Generates a password reset token for the specified username and sends it via email."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reset token generated and email sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/request")
    public void createPasswordResetToken(@Valid @RequestBody CreatePasswordResetTokenDto request) {
        userCredentialsService.createAndSendPasswordResetToken(request.username());
    }

    @Operation(
            summary = "Reset Password",
            description = "Resets the user’s password using the provided token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The token and the new password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResetPasswordRequestDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @PostMapping("/confirm")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        userCredentialsService.resetPassword(request.token(), request.newPassword());
    }
}
