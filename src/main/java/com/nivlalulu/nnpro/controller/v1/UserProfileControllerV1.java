package com.nivlalulu.nnpro.controller.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user/{username}")
@Tag(name = "User controller", description = "Operations for managing a specific user")
public class UserProfileControllerV1 {
}
