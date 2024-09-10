package com.aloc.aloc.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.entity.UserItem;
import com.aloc.aloc.user.entity.User;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
	Boolean existsByUserAndItem(User user, Item item);
}
