package com.aloc.aloc.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {
	@NotBlank
	@Schema(description = "Github ID", example = "githubId")
	private String githubId;
	@NotBlank
	@Schema(description = "Password", example = "password")
	private String password;
}
