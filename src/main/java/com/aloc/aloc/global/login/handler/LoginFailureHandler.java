package com.aloc.aloc.global.login.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception
	) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //401 인증 실패
		response.getWriter().write("fail");
		log.error(Exception.class.getName() + " : " + exception.getMessage());
		log.info("로그인 실패");
	}
}
