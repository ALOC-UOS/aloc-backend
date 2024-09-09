package com.aloc.aloc.global.jwt.service;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;


@SpringBootTest
@Transactional
public class JwtServiceTest {
	@Autowired
	JwtService jwtService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	EntityManager em;

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.access.header}")
	private String accessHeader;
	@Value("${jwt.refresh.header}")
	private String refreshHeader;

	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String USERNAME_CLAIM = "githubId";
	private static final String BEARER = "Bearer ";

//	private String username = "홍길동";
	private String githubId = "github";
	@BeforeEach
	public void init() {
		User user = User.builder()
			.username("홍길동")
			.password("1234")
			.githubId(githubId)
			.baekjoonId("baekjoon")
			.studentId("20")
			.discordId("discord")
			.notionEmail("notion@uos.ac.kr")
			.build();
		userRepository.save(user);
		clear();
	}

	private void clear() {
		em.flush();
		em.clear();
	}

	private DecodedJWT getVerify(String token) {
		return JWT.require(HMAC512(secret)).build().verify(token);
	}

	@Test
	public void create_accessToken_check_githubId_isEqual() throws Exception {
		//given, when
		String accessToken = jwtService.createAccessToken(githubId);
		DecodedJWT verify = getVerify(accessToken);

		String subject = verify.getSubject();
		String findGithubId = verify.getClaim(USERNAME_CLAIM).asString();

		//then
		assertThat(findGithubId).isEqualTo(githubId);
		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
	}

	@Test
	public void create_refreshToken_check_githubId_isNull() throws Exception {
		//given, when
		String refreshToken = jwtService.createRefreshToken();
		DecodedJWT verify = getVerify(refreshToken);
		String subject = verify.getSubject();
		String githubId = verify.getClaim(USERNAME_CLAIM).asString();

		//then
		assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
		assertThat(githubId).isNull();
	}

	@Test
	public void update_refreshToken_check_githubId_isEqual() throws Exception {
		//given
		String refreshToken = jwtService.createRefreshToken();
		jwtService.updateRefreshToken(githubId, refreshToken);
		clear();
		Thread.sleep(3000);

		//when
		String reIssuedRefreshToken = jwtService.createRefreshToken();
		jwtService.updateRefreshToken(githubId, reIssuedRefreshToken);
		clear();

		//then
		assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).orElseThrow());
		assertThat(userRepository.findByRefreshToken(reIssuedRefreshToken).get().getGithubId()).isEqualTo(githubId);
	}

	@Test
	public void destroy_refreshToken_check_refreshToken_isNull() throws Exception {
		//given
		String refreshToken = jwtService.createRefreshToken();
		jwtService.updateRefreshToken(githubId, refreshToken);
		clear();

		//when
		jwtService.destroyRefreshToken(githubId);
		clear();

		//then
		assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).get());

		User user = userRepository.findByGithubId(githubId).get();
		assertThat(user.getRefreshToken()).isNull();
	}

	@Test
	public void set_accessTokenHeader_check_headerAccessToken_isEqual() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);


		//when
		jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

		//then
		String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

		assertThat(headerAccessToken).isEqualTo(accessToken);
	}



	@Test
	public void set_refreshTokenHeader_check_refreshTokenHeader_isEqual() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);


		//when
		jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

		//then
		String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

		assertThat(headerRefreshToken).isEqualTo(refreshToken);
	}

	@Test
	public void sendToken_check_token_isEqual() throws Exception {
		//given
		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();


		//when
		jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

		//then
		String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
		String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);



		assertThat(headerAccessToken).isEqualTo(accessToken);
		assertThat(headerRefreshToken).isEqualTo(refreshToken);

	}

	// 토큰 전송 테스트를 위한 함수
	private HttpServletRequest setRequest(String accessToken, String refreshToken) {

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

		String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
		String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.addHeader(accessHeader, BEARER + headerAccessToken);
		httpServletRequest.addHeader(refreshHeader, BEARER + headerRefreshToken);

		return httpServletRequest;
	}

	@Test
	public void extractAccessToken_AccessToken_check_isExist() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();
		HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

		//when
		String extractAccessToken =
			jwtService.extractAccessToken(httpServletRequest).orElseThrow(()-> new Exception("토큰이 없습니다"));


		//then
		assertThat(extractAccessToken).isEqualTo(accessToken);
		assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(githubId);
	}

	@Test
	public void extractRefreshToken_RefreshToken_check_isExist() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();
		HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);


		//when
		String extractRefreshToken =
			jwtService.extractRefreshToken(httpServletRequest).orElseThrow(()-> new Exception("토큰이 없습니다"));


		//then
		assertThat(extractRefreshToken).isEqualTo(refreshToken);
		assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
	}

	@Test
	public void extract_githubId_check_isEqual() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();
		HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

		String requestAccessToken =
			jwtService.extractAccessToken(httpServletRequest).orElseThrow(()->new Exception("토큰이 없습니다"));

		//when
		String extractGithubId =
			jwtService.extractGithubId(requestAccessToken).orElseThrow(()->new Exception("토큰이 없습니다"));


		//then
		assertThat(extractGithubId).isEqualTo(githubId);
	}

	@Test
	public void check_token_validation() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(githubId);
		String refreshToken = jwtService.createRefreshToken();

		//when, then
		assertThat(jwtService.isTokenValid(accessToken)).isTrue();
		assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
		assertThat(jwtService.isTokenValid(accessToken + "d")).isFalse();
		assertThat(jwtService.isTokenValid(accessToken + "d")).isFalse();

	}
}
