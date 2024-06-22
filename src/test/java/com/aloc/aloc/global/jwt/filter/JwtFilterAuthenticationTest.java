package com.aloc.aloc.global.jwt.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.domain.user.User;
import com.aloc.aloc.domain.user.repository.UserRepository;
import com.aloc.aloc.global.jwt.service.JwtService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtFilterAuthenticationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EntityManager em;

	@Autowired
	JwtService jwtService;

	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.access.header}")
	private String accessHeader;
	@Value("${jwt.refresh.header}")
	private String refreshHeader;

	private static String KEY_GITHUBID = "githubId";
	private static String KEY_PASSWORD = "password";
	private static String GITHUBID = "github";
	private static String PASSWORD = "password";

	private static String LOGIN_URL = "/login";

	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String BEARER = "Bearer ";

	private ObjectMapper objectMapper = new ObjectMapper();

	private void clear() {
		em.flush();
		em.clear();
	}

	@BeforeEach
	private void init() {
		userRepository.save(
			User.builder()
				.username("홍길동")
				.password(bCryptPasswordEncoder.encode(PASSWORD))
				.githubId(GITHUBID)
				.baekjoonId("baekjoon")
				.studentId("20")
				.build());
		clear();
	}

	private Map getGithubIdPasswordMap(String githubId, String password) {
		Map<String, String> map = new HashMap<>();
		map.put(KEY_GITHUBID, githubId);
		map.put(KEY_PASSWORD, password);
		return map;
	}

	private Map getAccessAndRefreshToken() throws Exception {

		Map map = getGithubIdPasswordMap(GITHUBID, PASSWORD);
		MvcResult result = mockMvc.perform(
				post(LOGIN_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(map)))
			.andReturn();
		String accessToken = result.getResponse().getHeader(accessHeader);
		String refreshToken = result.getResponse().getHeader(refreshHeader);
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(accessHeader, accessToken);
		tokenMap.put(refreshHeader, refreshToken);

		return tokenMap;
	}

	@Test
	public void access_refresh_doesnt_exist() throws Exception {
		//when, then
		mockMvc.perform(get(LOGIN_URL + "123")) //login이 아닌 다른 임의의 주소
			.andExpect(status().isNotFound());
	}

	@Test
	public void accessToken_only_exist() throws Exception {
		//given
		Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);

		//when, then
		mockMvc.perform(
				get(LOGIN_URL + "123").header(accessHeader, BEARER + accessToken))//login이 아닌 다른 임의의 주소
			.andExpectAll(status().isNotFound()); //없는 주소로 보냈으므로 NotFound

	}

	@Test
	public void invalid_accessToken_auth_failed_with_403() throws Exception {
		//given
		Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);

		//when, then
		mockMvc.perform(
				get(LOGIN_URL + "123").header(accessHeader, accessToken + "1")) //login이 아닌 다른 임의의 주소
			.andExpectAll(status().isNotFound()); //없는 주소로 보냈으므로 NotFound
	}

	@Test
	public void validRefreshToken_reissue_accessToken_with_200() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		System.out.println("accessAndRefreshToken = " + accessAndRefreshToken);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

		//when, then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123").header(refreshHeader,
				BEARER + refreshToken))//login이 아닌 다른 임의의 주소
			.andExpect(status().isOk()).andReturn();

		String accessToken = result.getResponse().getHeader(accessHeader);

		assert accessToken != null;
		String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken)
			.getSubject();
		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
	}

	@Test
	public void invalid_refreshToken_without_accessToken_return_404() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		System.out.println("accessAndRefreshToken = " + accessAndRefreshToken);
		System.out.println("accessHeader = " + accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

		//when, then
		mockMvc.perform(get(LOGIN_URL + "123").header(refreshHeader, refreshToken))
			.andExpect(status().isNotFound()); //Bearer을 붙이지 않음

		mockMvc.perform(
				get(LOGIN_URL + "123").header(refreshHeader, BEARER + refreshToken + "1")) //유효하지 않은 토큰
			.andExpect(status().isNotFound());
	}

	@Test
	public void valid_refreshToken_with_accessToken_then_reissue_accessToken_with_200()
		throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

		//when, then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
				.header(refreshHeader, BEARER + refreshToken)
				.header(accessHeader, BEARER + accessToken))
			.andExpect(status().isOk())
			.andReturn();

		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

		String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken)
			.getSubject();

		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
		assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
	}

	@Test
	public void valid_refreshToken_with_invalidAccessToken_then_accessToken_reissue_with200()
		throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

		//when, then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
				.header(refreshHeader, BEARER + refreshToken)
				.header(accessHeader, BEARER + accessToken + 1))
			.andExpect(status().isOk())
			.andReturn();

		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

		String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken)
			.getSubject();

		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
		assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
	}

	@Test
	public void invalid_refreshToken_with_valid_accessToken_then_200_nothing_reissued()
		throws Exception {
		//given
		Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

		//when, then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
				.header(refreshHeader, BEARER + refreshToken + 1)
				.header(accessHeader, BEARER + accessToken))
			.andExpect(status().isNotFound()) //없는 주소로 보냈으므로 NotFound
			.andReturn();

		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

		assertThat(responseAccessToken).isNull(); //accessToken은 재발급되지 않음
		assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
	}

	@Test
	public void invalid_refreshToken_with_invalid_accessToken_then_return_404() throws Exception {
		//given
		Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

		//when, then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
				.header(refreshHeader, BEARER + refreshToken + 1)
				.header(accessHeader, BEARER + accessToken + 1))
			.andExpect(status().isNotFound()) //없는 주소로 보냈으므로 NotFound
			.andReturn();

		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

		assertThat(responseAccessToken).isNull(); //accessToken은 재발급되지 않음
		assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
	}

	@Test
	public void request_with_loginUrl_then_filter_off() throws Exception {
		//given
		Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

		//when, then
		MvcResult result = mockMvc.perform(
				post(LOGIN_URL)  //get인 경우 config에서 permitAll을 했기에 notFound
					.header(refreshHeader, BEARER + refreshToken)
					.header(accessHeader, BEARER + accessToken))
			.andExpect(status().isBadRequest())
			.andReturn();

	}
}
