package com.aloc.aloc.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aloc.aloc.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findAllByIsHiddenIsTrue();

}
