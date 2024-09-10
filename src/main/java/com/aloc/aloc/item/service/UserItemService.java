package com.aloc.aloc.item.service;

import org.springframework.stereotype.Service;

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

}
