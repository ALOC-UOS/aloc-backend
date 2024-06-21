package com.aloc.aloc.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.aloc.aloc.security.CustomeUserDetailsService;
import com.aloc.aloc.security.JsonUsernamePasswordAuthenticationFilter;
import com.aloc.aloc.security.JwtProviderHandler;
import com.aloc.aloc.security.LoginFailureHandler;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final ObjectMapper objectMapper;
	private final CustomeUserDetailsService customeUserDetailsService;
	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/purchase", "/", "/login").authenticated()
				.anyRequest().permitAll())
			.logout((logout) -> logout
				.logoutSuccessUrl("/logout")
				.invalidateHttpSession(true))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
//			.addFilterBefore(UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
		JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter =
			new JsonUsernamePasswordAuthenticationFilter(objectMapper);
		jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
		jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
		return jsonUsernamePasswordLoginFilter;
	}

	@Bean
	public AuthenticationFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}

	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new JwtProviderHandler();
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		DaoAuthenticationProvider provider = daoAuthenticationProvider();
		return new ProviderManager(provider);
	}

	private DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customeUserDetailsService);
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
