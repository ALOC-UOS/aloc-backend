package com.aloc.aloc.user.service;

import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

  private final UserSortingService userSortingService;
  private final UserMapper userMapper;
  private final UserService userService;

  public List<UserDetailResponseDto> getUsers() {
    List<User> users = userService.getActiveUsers();
    if (users.isEmpty()) {
      return List.of();
    }
    List<User> sortedUserList = userSortingService.sortUserList(users);
    return sortedUserList.stream()
        .map(userMapper::mapToUserDetailResponseDto)
        .collect(Collectors.toList());
  }

  public UserDetailResponseDto getUser(String githubId) {
    User user = userService.findUser(githubId);
    return userMapper.mapToUserDetailResponseDto(user);
  }
}
