package com.aloc.aloc.domain.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aloc.aloc.domain.user.dto.request.UserRequestDto;
import com.aloc.aloc.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public void signUp(UserRequestDto userRequestDto) {
		if (userRepository.existsByGithubId(userRequestDto.getGithubId())) {
			throw new IllegalArgumentException("이미 존재하는 유저입니다.");
		}

		if (userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())) {
			throw new IllegalArgumentException("이미 존재하는 유저입니다.");
		}
		System.out.println(userRequestDto.getPassword());
		System.out.println(userRequestDto.toEntity(passwordEncoder));
//		userRepository.save(userRequestDto.toEntity(passwordEncoder));
	}
}
