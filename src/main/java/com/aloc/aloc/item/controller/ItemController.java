package com.aloc.aloc.item.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.item.dto.request.ItemRequestDto;
import com.aloc.aloc.item.dto.request.ItemUpdateRequestDto;
import com.aloc.aloc.item.dto.response.ItemResponseDto;
import com.aloc.aloc.item.dto.response.UserItemResponseDto;
import com.aloc.aloc.item.service.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/item")
public class ItemController {
	private final ItemService itemService;

	@GetMapping("/")
	@Operation(summary = "아이템 목록 조회", description = "공개된 아이템 목록을 조회합니다.")
	public CustomApiResponse<List<ItemResponseDto>> getItems() {
		return CustomApiResponse.onSuccess(itemService.getItems());
	}

	@SecurityRequirement(name = "JWT Auth")
	@PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@Operation(summary = "아이템 추가", description = "새로운 아이템을 추가합니다.")
	public CustomApiResponse<String> insertItem(
		@Parameter(hidden = true) @AuthenticationPrincipal User user,
		@ModelAttribute ItemRequestDto itemRequestDto) {
		return CustomApiResponse.onSuccess(itemService.insertItem(user.getUsername(), itemRequestDto));
	}

	@SecurityRequirement(name = "JWT Auth")
	@PatchMapping("/")
	@Operation(summary = "아이템 수정", description = "기존 아이템 정보를 업데이트 합니다.")
	public CustomApiResponse<String> updateItem(
		@Parameter(hidden = true) @AuthenticationPrincipal User user,
		@RequestBody ItemUpdateRequestDto itemUpdateRequestDto) {
		return CustomApiResponse.onSuccess(itemService.updateItemInfo(user.getUsername(), itemUpdateRequestDto));
	}

	@SecurityRequirement(name = "JWT Auth")
	@DeleteMapping("/{itemId}")
	@Operation(summary = "아이템 삭제", description = "아이템 id로 해당 아이템을 삭제합니다.")
	public CustomApiResponse<String> deleteItem(
		@Parameter(hidden = true) @AuthenticationPrincipal User user,
		@Parameter(description = "아이템 id", required = true) @PathVariable() Long itemId
	) {
		return CustomApiResponse.onSuccess(itemService.deleteItem(user.getUsername(), itemId));
	}

	@SecurityRequirement(name = "JWT Auth")
	@PostMapping("/{itemId}")
	@Operation(summary = "아이템 구매", description = "아이템 id로 해당 아이템을 구매합니다.")
	public CustomApiResponse<String> buyItem(
			@Parameter(hidden = true) @AuthenticationPrincipal User user,
			@Parameter(description = "아이템 id", required = true) @PathVariable() Long itemId
	) {
		return CustomApiResponse.onSuccess(itemService.purchaseItem(user.getUsername(), itemId));
	}

	@SecurityRequirement(name = "JWT Auth")
	@GetMapping("/user-item")
	@Operation(summary = "유저가 구매한 아이템 조회", description = "유저가 구매한 모든 아이템을 조회합니다.")
	public CustomApiResponse<List<UserItemResponseDto>> getUserItems(
			@Parameter(hidden = true) @AuthenticationPrincipal User user) {
		return CustomApiResponse.onSuccess(itemService.getUserItems(user.getUsername()));
	}
}
