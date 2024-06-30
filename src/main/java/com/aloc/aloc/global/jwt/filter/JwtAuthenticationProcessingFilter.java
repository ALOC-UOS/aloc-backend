package com.aloc.aloc.global.jwt.filter;

import java.util.Collections;

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

	/**
	 * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
	 *
	 * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException, java.io.IOException {
		String noCheckUrl = "/api2/login";
		if (request.getRequestURI().equals(noCheckUrl)) {
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = jwtService
			.extractRefreshToken(request)
			.filter(jwtService::isTokenValid)
			.orElse(null);


		if (refreshToken != null) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}

		checkAccessTokenAndAuthentication(request, response, filterChain);
	}

	private void checkAccessTokenAndAuthentication(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	)
		throws ServletException, IOException, java.io.IOException {
		jwtService.extractAccessToken(request).filter(jwtService::isTokenValid)
			.flatMap(accessToken -> jwtService.extractGithubId(accessToken)
				.flatMap(userRepository::findByGithubId)).ifPresent(this::saveAuthentication);

		filterChain.doFilter(request, response);
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

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		userRepository.findByRefreshToken(refreshToken).ifPresent(
			user -> jwtService.sendAccessToken(response, jwtService.createAccessToken(user.getGithubId()))
		);
	}
}
