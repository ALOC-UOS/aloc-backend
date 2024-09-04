package com.aloc.aloc.item.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.item.dto.request.ItemRequestDto;
import com.aloc.aloc.item.dto.request.ItemUpdateRequestDto;
import com.aloc.aloc.item.dto.response.ItemResponseDto;
import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.repository.ItemRepository;
import com.aloc.aloc.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final UserService userService;
	public List<ItemResponseDto> getItems() {
		List<Item> items = itemRepository.findAllByIsHiddenIsTrue();
		return items.stream()
			.map(ItemResponseDto::of).collect(Collectors.toList());
	}

	@Transactional
	public String insertItem(String githubId, ItemRequestDto itemRequestDto) {
		userService.checkAdmin(githubId);

		Item item = Item.builder()
			.name(itemRequestDto.getName())
			.description(itemRequestDto.getDescription())
			.coin(itemRequestDto.getCoin())
			.itemType(itemRequestDto.getItemType())
			.isHidden(itemRequestDto.getIsHidden())
			.build();
		itemRepository.save(item);
		return "아이템 '%s' 이(가) 성공적으로 추가되었습니다.".formatted(item.getName());
	}

	@Transactional
	public String updateItem(String githubId, ItemUpdateRequestDto itemUpdateRequestDto) {
		userService.checkAdmin(githubId);

		Item item = itemRepository.findById(itemUpdateRequestDto.getId())
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));

		updateItemDetails(item, itemUpdateRequestDto);
		itemRepository.save(item);

		return "아이템 '%s' 이(가) 성공적으로 수정되었습니다.".formatted(item.getName());
	}

	@Transactional
	public String deleteItem(String githubId, Long itemId) {
		userService.checkAdmin(githubId);

		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));
		itemRepository.delete(item);

		return "아이템 '%s' 이(가) 성공적으로 삭제되었습니다.".formatted(item.getName());
	}

	private void updateItemDetails(Item item, ItemUpdateRequestDto requestDto) {
		item.setName(requestDto.getName());
		item.setDescription(requestDto.getDescription());
		item.setCoin(requestDto.getCoin());
		item.setItemType(requestDto.getItemType());
		item.setIsHidden(requestDto.getIsHidden());
	}
}
