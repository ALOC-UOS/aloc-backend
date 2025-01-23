package com.aloc.aloc.item.service;

import com.aloc.aloc.item.dto.response.UserItemResponseDto;
import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.entity.UserItem;
import com.aloc.aloc.item.repository.UserItemRepository;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserItemService {
  private final UserItemRepository userItemRepository;

  public void createUserItem(User user, Item item) {
    UserItem userItem = UserItem.builder().user(user).item(item).isActive(false).build();
    userItemRepository.save(userItem);
  }

  public Boolean isExists(User user, Item item) {
    return userItemRepository.existsByUserAndItem(user, item);
  }

  public List<UserItemResponseDto> getUserItems(User user) {
    List<UserItem> userItems = userItemRepository.findAllByUser(user);
    return userItems.stream().map(UserItemResponseDto::of).collect(Collectors.toList());
  }

  public String updateUserItemActive(User user, Long userItemId) {
    UserItem userItem =
        userItemRepository
            .findById(userItemId)
            .orElseThrow(
                () -> new NoSuchElementException("해당 아이템을 구매하지 않았거나, 올바르지 않은 userItemId입니다."));

    validateUserOwnership(user, userItem);

    deactivateActiveUserItemIfExists(user, userItem);
    activateUserItem(userItem);
    return "아이템 '%s' 이(가) 성공적으로 활성화되었습니다.";
  }

  private void activateUserItem(UserItem userItem) {
    userItem.setIsActive(true);
    userItemRepository.save(userItem);
  }

  private void deactivateActiveUserItemIfExists(User user, UserItem userItem) {
    userItemRepository
        .findActiveItemByUserAndItemType(user, userItem.getItem().getItemLocation())
        .ifPresent(
            activeItem -> {
              activeItem.setIsActive(false);
              userItemRepository.save(activeItem);
            });
  }

  private void validateUserOwnership(User user, UserItem userItem) {
    if (!user.equals(userItem.getUser())) {
      throw new IllegalArgumentException("해당 사용자가 구매하지 않은 아이템입니다.");
    }
  }
}
