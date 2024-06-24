package com.aloc.aloc.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@SecuritySchemes({
	@io.swagger.v3.oas.annotations.security.SecurityScheme(
		name = "Refresh",
		type = SecuritySchemeType.APIKEY,
		in = SecuritySchemeIn.HEADER
		),
	@io.swagger.v3.oas.annotations.security.SecurityScheme(
		name = "JWT Auth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
		)
})
@RequiredArgsConstructor
public class SwaggerConfig {
	@Bean
	public OpenAPI openApi() {
//		String jwtSchemeName = "JWT TOKEN";
		// API 요청 헤더에 인증정보 포함
//		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
		// SecuritySchemes 등록
//		Components components = new Components()
//			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
//				.name(jwtSchemeName)
//				.type(SecurityScheme.Type.HTTP)
//				.scheme("bearer")
//				.bearerFormat("JWT"));


		return new OpenAPI()
			.addServersItem(new Server().url("/"))
//			.addSecurityItem(securityRequirement)
//			.components(components)
			.info(new Info()
				.title("Aloc API")
				.description("Aloc API 문서")
				.version("1.0.0")
			);
	}

}
