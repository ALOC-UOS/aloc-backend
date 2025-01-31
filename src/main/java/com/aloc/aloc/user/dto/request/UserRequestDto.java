package com.aloc.aloc.user.dto.request;

import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class UserRequestDto {

  @NotNull
  @Schema(description = "사용자 이름", example = "홍길동")
  private String username;

  @NotNull
  @Schema(description = "비밀번호", example = "password")
  private String password;

  @NotNull
  @Schema(description = "깃허브 아이디", example = "githubId")
  private String githubId;

  @NotNull
  @Schema(description = "백준 아이디", example = "baekjoonId")
  private String baekjoonId;

  @NotNull
  @Schema(description = "학번", example = "2020920000")
  private String studentId;

  @NotNull
  @Schema(description = "디스코드 아이디", example = "discordId")
  private String discordId;

  @NotNull
  @Schema(description = "노션 초대 이메일", example = "notion@uos.ac.kr")
  private String notionEmail;

  @NotNull
  @Schema(description = "HALF/FULL 코스", example = "HALF")
  private Course course;

  public User toEntity(Integer rank, BCryptPasswordEncoder passwordEncoder) {
    return User.builder()
        .username(username)
        .password(passwordEncoder.encode(password))
        .githubId(githubId)
        .baekjoonId(baekjoonId)
        .studentId(studentId)
        .discordId(discordId)
        .rank(rank)
        .notionEmail(notionEmail)
        .course(course)
        .build();
  }
}
