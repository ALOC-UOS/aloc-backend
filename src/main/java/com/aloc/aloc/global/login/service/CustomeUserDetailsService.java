//package com.aloc.aloc.security;
//
//import java.util.Collections;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.aloc.aloc.domain.User.User;
//import com.aloc.aloc.domain.User.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class CustomeUserDetailsService implements UserDetailsService {
//
//	private final UserRepository userRepository;
//
//	@Override
//	public UserDetails loadUserByUsername(String githubId) {
//		return userRepository.findByGithubId(githubId)
//			.map(this::createUserDetails)
//			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));
//	}
//
//	// DB에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
//	private UserDetails createUserDetails(User user) {
//		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
//			user.getAuthority().toString());
//
//		return new org.springframework.security.core.userdetails.User(
//			user.getId(),
//			user.getPassword(),
//			Collections.singleton(grantedAuthority)
//		);
//	}
//}
