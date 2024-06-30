package com.aloc.aloc.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserListResponseDto;
import com.aloc.aloc.user.dto.response.UserResponseDto;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private void checkAdmin(String githubId) {
		Optional<User> userOptional = userRepository.findByGithubId(githubId);
		if (userOptional.isEmpty()) {
			throw new IllegalStateException("로그인 정보가 없습니다.");
		}
		User user = userOptional.get();
		if (!Authority.ROLE_ADMIN.equals(user.getAuthority())) {
			throw new IllegalStateException("관리자만 가능합니다.");
		}
	}

	public UserListResponseDto getUsers() {
		List<UserResponseDto> studyUsers = userRepository.findAllByAuthority("ROLE_USER");
		return new UserListResponseDto(studyUsers);
	}

	public String addUser(String username, String githubId) {
		checkAdmin(username);
		Optional<User> userOptional = userRepository.findByGithubId(githubId);
		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("해당 깃허브 아이디로 가입된 사용자가 없습니다.");
		}
		User user = userOptional.get();
		if (Authority.ROLE_USER.equals(user.getAuthority())) {
			throw new IllegalArgumentException("이미 등록된 멤버입니다.");
		}
		user.setAuthority(Authority.ROLE_USER);
		return "스터디 멤버로 등록되었습니다.";
	}
}
