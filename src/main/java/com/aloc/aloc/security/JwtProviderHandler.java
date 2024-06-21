package com.aloc.aloc.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.aloc.aloc.jwt.JwtService;
import com.aloc.aloc.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtProviderHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	@Override
	// 로그인 성공 시 JWT 토큰 발급, 로그 출력
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		String githubId = extractGithubId(authentication);
		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		userRepository.findByGithubId(githubId)
			.ifPresentOrElse(
				user -> jwtService.updateRefreshToken(githubId, refreshToken),
				() -> log.error("로그인 성공. JWT 발급. DB에 사용자 정보 없음. githubId: {}", githubId)
			);
		log.info( "로그인에 성공합니다. githubId: {}", githubId);
		log.info( "AccessToken 을 발급합니다. AccessToken: {}", accessToken);
		log.info( "RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);

		response.getWriter().write("success");
	}

	private String extractGithubId(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails.getUsername();
	}
}
