package com.aloc.aloc.item.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.item.dto.response.UserItemResponseDto;
import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.entity.UserItem;
import com.aloc.aloc.item.repository.UserItemRepository;
import com.aloc.aloc.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserItemService {
	private final UserItemRepository userItemRepository;

	public void createUserItem(User user, Item item) {
		UserItem userItem = UserItem.builder()
				.user(user)
				.item(item)
				.isActive(false)
				.build();
		userItemRepository.save(userItem);
	}

	public Boolean isExists(User user, Item item) {
		return userItemRepository.existsByUserAndItem(user, item);
	}

	public List<UserItemResponseDto> getUserItems(User user) {
		List<UserItem> userItems = userItemRepository.findAllByUser(user);
		return userItems.stream()
				.map(UserItemResponseDto::of).collect(Collectors.toList());
	}

}
