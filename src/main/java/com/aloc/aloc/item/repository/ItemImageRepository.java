package com.aloc.aloc.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.entity.ItemImage;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
	List<ItemImage> findAllByItem(Item item);
}
