package com.aloc.aloc.auth.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.aloc.aloc.auth.service.AuthService;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원가입 컨트롤러 테스트")
	public void testSignUp() throws Exception {
		// given
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUsername("홍길동");
		userRequestDto.setPassword("1234");
		userRequestDto.setGithubId("github");
		userRequestDto.setBaekjoonId("baejoon");
		userRequestDto.setStudentId("2021920000");
		userRequestDto.setDiscordId("discord");
		userRequestDto.setNotionEmail("notion");

		doNothing().when(authService).signUp(Mockito.any(UserRequestDto.class));

		// when
		mockMvc.perform(post("/api2/sign-up")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(userRequestDto)))
		// then
			.andExpect(status().isOk());
	}
}
