package com.aloc.aloc.item.repository;

import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.entity.UserItem;
import com.aloc.aloc.item.enums.ItemLocation;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
  Boolean existsByUserAndItem(User user, Item item);

  List<UserItem> findAllByUser(User user);

  @Query(
      "SELECT ui FROM UserItem ui "
          + "JOIN ui.item i "
          + "WHERE ui.user = :user "
          + "AND i.itemLocation = :itemLocation "
          + "AND ui.isActive = true")
  Optional<UserItem> findActiveItemByUserAndItemType(
      @Param("user") User user, @Param("itemLocation") ItemLocation itemLocation);
}
