package com.aloc.aloc.global.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface JwtService {
  String createAccessToken(String githubId);

  String createRefreshToken();

  void updateRefreshToken(String githubId, String refreshToken);

  void destroyRefreshToken(String githubId);

  void sendAccessAndRefreshToken(
      HttpServletResponse response, String accessToken, String refreshToken);

  void sendAccessToken(HttpServletResponse response, String accessToken);

  Optional<String> extractAccessToken(HttpServletRequest request);

  Optional<String> extractRefreshToken(HttpServletRequest request);

  Optional<String> extractGithubId(String accessToken);

  void setAccessTokenHeader(HttpServletResponse response, String accessToken);

  void setRefreshTokenCookie(HttpServletResponse response, String refreshToken);

  boolean isTokenValid(String token);
}
