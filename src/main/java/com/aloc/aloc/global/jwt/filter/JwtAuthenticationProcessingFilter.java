package com.aloc.aloc.global.jwt.filter;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException, java.io.IOException {
		String noCheckUrl = "/api2/login";
		String refreshToken = jwtService.extractRefreshToken(request).orElse(null);
		String accessToken = jwtService.extractAccessToken(request).orElse(null);

		// 로그인 시도
		if (request.getRequestURI().equals(noCheckUrl)) {
			filterChain.doFilter(request, response);
			return;
		}

		// accessToken이 있고 유효한 경우
		if (processAccessToken(accessToken)) {
			filterChain.doFilter(request, response);
			return;
		}

		// refreshToken이 있고 유효한 경우
		processRefreshToken(response, refreshToken);
		filterChain.doFilter(request, response);
	}

	private boolean processAccessToken(String accessToken) {
		if (accessToken != null && jwtService.isTokenValid(accessToken)) {
			setAuthentication(accessToken);
			return true;
		}
		return false;
	}

	private void processRefreshToken(HttpServletResponse response, String refreshToken) {
		if (refreshToken != null && jwtService.isTokenValid(refreshToken)) {
			handleValidRefreshToken(response, refreshToken);
		}
	}

	private void handleValidRefreshToken(HttpServletResponse response, String refreshToken) {
		String githubId = getGithubIdByRefreshToken(refreshToken).orElse(null);
		if (githubId != null) {
			String newAccessToken = jwtService.createAccessToken(githubId);
			jwtService.setAccessTokenHeader(response, newAccessToken);
//			response.setHeader("Authorization", "Bearer " + newAccessToken);
			setAuthentication(newAccessToken);
		}
	}

	private void setAuthentication(String accessToken) {
		jwtService.extractGithubId(accessToken)
			.flatMap(userRepository::findByGithubId)
			.ifPresent(this::saveAuthentication);
	}


	private void saveAuthentication(User user) {
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
			user.getAuthority().toString());

		UserDetails userDetails =
			new org.springframework.security.core.userdetails.User(
				user.getGithubId(),
				user.getPassword(),
				Collections.singleton(grantedAuthority)
			);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails,
			null,
			authoritiesMapper.mapAuthorities(userDetails.getAuthorities())
		);


		SecurityContext context = SecurityContextHolder.createEmptyContext(); //5
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private Optional<String> getGithubIdByRefreshToken(String refreshToken) {
		return userRepository.findByRefreshToken(refreshToken)
			.map(User::getGithubId);
	}
}
