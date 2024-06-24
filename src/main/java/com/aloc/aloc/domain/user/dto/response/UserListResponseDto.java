package com.aloc.aloc.domain.user.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserListResponseDto {
	List<UserResponseDto> userList;
}
