package com.aloc.aloc.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aloc.aloc.auth.dto.UserLoginRequestDto;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public void login(UserLoginRequestDto userLoginRequestDto) {
		System.out.println("Login request received for username: " + userLoginRequestDto.getGithubId());
	}

	public void signUp(UserRequestDto userRequestDto) {
		if (userRepository.existsByGithubId(userRequestDto.getGithubId())
			|| userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())) {
			throw new IllegalArgumentException("이미 존재하는 유저입니다.");
		}
		userRepository.save(userRequestDto.toEntity(passwordEncoder));
	}

}
