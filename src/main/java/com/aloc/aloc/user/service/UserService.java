package com.aloc.aloc.user.service;

import com.aloc.aloc.alocrequest.AlocRequest;
import com.aloc.aloc.alocrequest.repository.AlocRequestRepository;
import com.aloc.aloc.coinhistory.service.CoinHistoryService;
import com.aloc.aloc.history.service.HistoryService;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.request.UserCoinDto;
import com.aloc.aloc.user.dto.request.UserPasswordDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private static final Set<Authority> ACTIVE_AUTHORITIES =
      Set.of(Authority.ROLE_USER, Authority.ROLE_ADMIN);
  private final UserRepository userRepository;
  private final HistoryService historyService;
  private final BaekjoonRankScrapingService baekjoonRankScrapingService;
  private final AlocRequestRepository alocRequestRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final CoinHistoryService coinHistoryService;

  public void checkAdmin(String githubId) {
    Optional<User> userOptional = userRepository.findByGithubId(githubId);
    if (userOptional.isEmpty()) {
      throw new IllegalStateException("로그인 정보가 없습니다.");
    }
    User user = userOptional.get();
    if (!Authority.ROLE_ADMIN.equals(user.getAuthority())) {
      throw new IllegalStateException("관리자만 가능합니다.");
    }
  }

  @Transactional
  public void checkUserRank(User user) {
    Integer rank = baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId());
    if (!user.getRank().equals(rank)) {
      updateUserRank(user, rank);
    }
  }

  public String changeCourse(String githubId) throws AccessDeniedException {
    User user =
        userRepository
            .findByGithubId(githubId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않은 사용자입니다."));

    if (user.getAuthority().equals(Authority.ROLE_GUEST)) {
      changeCourseForGuest(user);
      return "guest 유저의 코스 변경이 완료되었습니다.";
    }

    if (user.getCourse().equals(Course.FULL)) {
      throw new AccessDeniedException("FULL 코스에서 HALF 코스로의 변경은 불가합니다. 담당자에게 문의하세요.");
    }

    AlocRequest request = AlocRequest.builder().user(user).requestType("changeCourse").build();
    alocRequestRepository.save(request);
    return "다음 주차부터 FULL 코스로 변경됩니다.";
  }

  private void changeCourseForGuest(User user) {
    user.setCourse(user.getCourse().equals(Course.FULL) ? Course.HALF : Course.FULL);
    saveUser(user);
  }

  @Transactional
  public void updateUserRank(User user, Integer rank) {
    user.setRank(rank);
    userRepository.save(user);
    historyService.addHistory(user, "changeRank", rank);
  }

  public List<User> getActiveUsers() {
    return userRepository.findAllByAuthorityIn(ACTIVE_AUTHORITIES);
  }

  public List<User> getActiveUsersByCourse(Course course) {
    return userRepository.findAllByAuthorityInAndCourse(ACTIVE_AUTHORITIES, course);
  }

  public User findUser(String githubId) {
    return userRepository
        .findByGithubId(githubId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
  }

  public void saveUser(User user) {
    userRepository.save(user);
  }

  public void isActiveUser(User user) {
    if (!ACTIVE_AUTHORITIES.contains(user.getAuthority())) {
      throw new org.springframework.security.access.AccessDeniedException("해당 기능을 사용할 수 없는 유저입니다.");
    }
  }

  public User getActiveUser(String githubId) {
    User user = findUser(githubId);
    isActiveUser(user);
    return user;
  }

  public String checkUserPassword(String githubId, UserPasswordDto userPasswordDto) {
    User user = findUser(githubId);
    if (user.matchPassword(passwordEncoder, userPasswordDto.getPassword())) {
      return "유저의 비밀번호가 일치합니다.";
    } else {
      throw new IllegalArgumentException("일치하지 않는 패스워드입니다.");
    }
  }

  @Transactional
  public String updateUserPassword(String githubId, UserPasswordDto userPasswordDto) {
    User user = findUser(githubId);
    user.setPassword(userPasswordDto.getPassword());
    user.encodePassword(passwordEncoder);
    saveUser(user);
    return "유저 비밀번호 변경을 성공하였습니다.";
  }

  @Transactional
  public String updateUserCoin(String githubId, UserCoinDto userCoinDto) {
    checkAdmin(githubId);

    User user = findUser(userCoinDto.getGithubId());
    user.getUserProfile().setCoin(user.getUserProfile().getCoin() + userCoinDto.getCoin());
    saveUser(user);
    coinHistoryService.addCoinHistory(
        user, userCoinDto.getCoin(), userCoinDto.getCoinType(), userCoinDto.getDescription());
    return "유저의 코인 업데이트 성공하였습니다.";
  }
}
