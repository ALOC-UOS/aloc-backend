package com.aloc.aloc.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.aloc.aloc.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import com.aloc.aloc.global.login.handler.JwtProviderHandler;
import com.aloc.aloc.global.login.handler.LoginFailureHandler;
import com.aloc.aloc.global.login.service.UserDetailsServiceImpl;
import com.aloc.aloc.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
//			.formLogin(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.addFilterBefore(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
			.addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(
					"/api2/login",
					"/api2/sign-up",
					"/api2/swagger-ui/**",
					"/api-docs/**",
					"/api2/swagger-ui.html"
				).permitAll()
				.requestMatchers("/purchase", "/api2/authorize/*").authenticated()
				.anyRequest().permitAll())
			.exceptionHandling(exceptionConfig -> exceptionConfig
				.authenticationEntryPoint((request, response, authException) -> response.sendError(
						HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Access Denied"))
				.accessDeniedHandler(
					(request, response, accessDeniedException
					) -> response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Missing token"))
			)
			.logout((logout) -> logout
				.logoutSuccessUrl("/api2/logout")
				.invalidateHttpSession(true))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
		return http.build();
	}

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		return new JwtAuthenticationProcessingFilter(jwtService, userRepository);
	}

	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() {
		JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter =
			new JsonUsernamePasswordAuthenticationFilter(objectMapper);
		jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(
			new JwtProviderHandler(jwtService, userRepository)
		);
		jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());
		return jsonUsernamePasswordLoginFilter;
	}

	@Bean
	public AuthenticationFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}

	@Bean
	public JwtProviderHandler loginSuccessJwtProvideHandler() {
		return new JwtProviderHandler(jwtService, userRepository);
	}


	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = daoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		return new ProviderManager(provider);
	}

	private DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder());

		return provider;
	}

	@Bean
	public BCryptPasswordEncoder bcryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// CORS 설정
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		corsConfiguration.setAllowedOriginPatterns(Arrays.asList(
			"http://localhost:3000",
			"https://*.iflab.run",
			"https://iflab.run"
		));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setAllowCredentials(true);

		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

}
