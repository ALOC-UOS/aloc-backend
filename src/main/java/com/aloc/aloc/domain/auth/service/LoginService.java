package com.aloc.aloc.domain.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aloc.aloc.domain.user.User;
import com.aloc.aloc.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String githubId) throws UsernameNotFoundException {
		User user = userRepository.findByGithubId(githubId).orElseThrow(() ->
			new UsernameNotFoundException("아이디가 없습니다"));

		return org.springframework.security.core.userdetails.User.builder()
			.username(user.getGithubId())
			.password(user.getPassword())
			.roles(user.getAuthority().name())
			.build();
	}
}
