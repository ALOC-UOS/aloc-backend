package com.aloc.aloc.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtProviderHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Override
	// 로그인 성공 시 JWT 토큰 발급, 로그 출력
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		log.info( "로그인 성공. JWT 발급. username: {}", userDetails.getUsername());

		response.getWriter().write("success");
	}
}
