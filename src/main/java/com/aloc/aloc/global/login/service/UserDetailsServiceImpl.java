package com.aloc.aloc.global.login.service;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String githubId) throws UsernameNotFoundException {
		User user = userRepository.findByGithubId(githubId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
			user.getAuthority().toString());
		return new org.springframework.security.core.userdetails.User(
			user.getGithubId(),
			user.getPassword(),
			Collections.singleton(grantedAuthority)
		);
	}
}
