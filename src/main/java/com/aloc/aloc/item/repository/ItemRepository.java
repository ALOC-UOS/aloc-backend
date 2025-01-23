package com.aloc.aloc.item.repository;

import com.aloc.aloc.item.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findAllByIsHiddenIsTrue();
}
