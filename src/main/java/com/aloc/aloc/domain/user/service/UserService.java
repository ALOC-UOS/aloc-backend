package com.aloc.aloc.domain.user.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aloc.aloc.domain.user.dto.response.UserListResponseDto;
import com.aloc.aloc.domain.user.dto.response.UserResponseDto;
import com.aloc.aloc.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UserListResponseDto getUsers() {
		List<UserResponseDto> studyUsers = userRepository.findAllByAuthority("ROLE_USER");
		return new UserListResponseDto(studyUsers);
	}
}
