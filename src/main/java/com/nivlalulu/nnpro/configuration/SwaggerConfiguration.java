package com.nivlalulu.nnpro.configuration;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnExpression("${swagger.enabled:true}")
@Profile("dev")
public class SwaggerConfiguration {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nivlalulu API")
                        .description("API for Nivlalulu project")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi apiGroupV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi publicGroupV1() {
        return GroupedOpenApi.builder()
                .group("public-v1")
                .pathsToMatch("/public/v1/**")
                .build();
    }
}
