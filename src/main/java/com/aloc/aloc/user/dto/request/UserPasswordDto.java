package com.aloc.aloc.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserPasswordDto {
	@Schema(description = "유저의 현재 비밀 번호", example = "password")
	private String password;
}
