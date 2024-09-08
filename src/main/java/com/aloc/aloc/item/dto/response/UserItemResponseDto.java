package com.aloc.aloc.item.dto.response;

import com.aloc.aloc.item.enums.ItemType;
import lombok.Data;

@Data
public class UserItemResponseDto {
	private String itemName;
	private Boolean isActive;
	private ItemType itemType;
	private String  purchaseDate;
}
