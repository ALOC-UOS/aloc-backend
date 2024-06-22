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

import com.aloc.aloc.domain.user.repository.UserRepository;
import com.aloc.aloc.domain.user.service.LoginService;
import com.aloc.aloc.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import com.aloc.aloc.global.login.handler.JwtProviderHandler;
import com.aloc.aloc.global.login.handler.LoginFailureHandler;
import com.aloc.aloc.global.login.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final UserDetailsServiceImpl userDetailsService;
	private final LoginService loginService;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final JwtService	jwtService;
	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
//			.formLogin(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.addFilterBefore(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/login", "/sign-up", "/refresh").permitAll()
				.requestMatchers("/purchase", "/").authenticated()
				.anyRequest().permitAll())
			.logout((logout) -> logout
				.logoutSuccessUrl("/logout")
				.invalidateHttpSession(true))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.addFilterAfter(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);
//			.addFilterBefore(UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		JwtAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter =
			new JwtAuthenticationProcessingFilter(jwtService, userRepository);

		return jsonUsernamePasswordLoginFilter;
	}

	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
		JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter =
			new JsonUsernamePasswordAuthenticationFilter(objectMapper);
		jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJwtProvideHandler());
		jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
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
	public AuthenticationManager authenticationManager() throws Exception {
		DaoAuthenticationProvider provider = daoAuthenticationProvider();
		provider.setPasswordEncoder(bcryptPasswordEncoder());
		return new ProviderManager(provider);
	}

	private DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(bcryptPasswordEncoder());

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
