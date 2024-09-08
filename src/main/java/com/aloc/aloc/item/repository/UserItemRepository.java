package com.aloc.aloc.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aloc.aloc.item.entity.UserItem;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
}
