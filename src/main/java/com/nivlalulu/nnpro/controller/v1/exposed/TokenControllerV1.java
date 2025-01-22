package com.nivlalulu.nnpro.controller.v1.exposed;

import com.nivlalulu.nnpro.dto.v1.TokenDto;
import com.nivlalulu.nnpro.service.IJwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1/token")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Token", description = "Endpoint for token operations")
public class TokenControllerV1 {
    private final IJwtTokenService refreshTokenService;

    @Operation(
            summary = "Refresh the access token",
            description = "Reissue the access token using the refresh token."
    )
    @ApiResponses({
    })
    @PostMapping("/refresh")
    public TokenDto refreshAccessToken(HttpServletRequest request) {
        return refreshTokenService.refresh(request);
    }
}
