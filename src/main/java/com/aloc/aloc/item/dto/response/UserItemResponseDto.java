package com.aloc.aloc.item.dto.response;

import com.aloc.aloc.item.entity.UserItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserItemResponseDto {
	@Schema(description = "유저 아이템 id", example = "1")
	private Long userItemId;
	@Schema(description = "아이템 id", example = "1")
	private Long itemId;
	@Schema(description = "착용 여부", example = "true")
	private Boolean isActive;

	public static UserItemResponseDto of(UserItem userItem) {
		return UserItemResponseDto.builder()
			.userItemId(userItem.getId())
			.itemId(userItem.getItem().getId())
			.isActive(userItem.getIsActive())
			.build();
	}
}
