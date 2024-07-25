package com.aloc.aloc.global.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import lombok.RequiredArgsConstructor;

@Configuration
@OpenAPIDefinition(
	info = @Info(title = "ALOC API", version = "v2"),
	security = {
		@SecurityRequirement(name = "JWT Auth")
	}
)
@SecuritySchemes({
	@io.swagger.v3.oas.annotations.security.SecurityScheme(
		name = "JWT Auth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
		)
})
@RequiredArgsConstructor
public class SwaggerConfig {
}
