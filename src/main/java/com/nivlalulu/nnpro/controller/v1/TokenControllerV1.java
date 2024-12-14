package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.RefreshTokenResponseDto;
import com.nivlalulu.nnpro.security.JwtTokenProvider;
import com.nivlalulu.nnpro.service.IJwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1/token")
@RequiredArgsConstructor
@Slf4j
public class TokenControllerV1 {
    private final IJwtTokenService refreshTokenService;

    @Operation(
            summary = "Refresh token",
            description = "Reissue the access token using the refresh token. The refresh token is rotated on use."
    )
    @ApiResponses({
    })
    @PostMapping("/refresh")
    public RefreshTokenResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return refreshTokenService.refreshAndRotate(request, response);
    }
}
