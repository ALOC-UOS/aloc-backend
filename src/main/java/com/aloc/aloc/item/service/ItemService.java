package com.aloc.aloc.item.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.aloc.aloc.coinhistory.enums.CoinType;
import com.aloc.aloc.coinhistory.service.CoinHistoryService;
import com.aloc.aloc.global.apipayload.exception.AlreadyPurchasedException;
import com.aloc.aloc.image.dto.UploadedImageInfo;
import com.aloc.aloc.image.enums.ImageType;
import com.aloc.aloc.image.service.ImageUploadService;
import com.aloc.aloc.item.dto.request.ItemRequestDto;
import com.aloc.aloc.item.dto.request.ItemUpdateRequestDto;
import com.aloc.aloc.item.dto.response.ItemResponseDto;
import com.aloc.aloc.item.dto.response.UserItemResponseDto;
import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.entity.ItemImage;
import com.aloc.aloc.item.repository.ItemImageRepository;
import com.aloc.aloc.item.repository.ItemRepository;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final UserService userService;
	private final ImageUploadService imageUploadService;
	private final ItemImageRepository itemImageRepository;
	private final CoinHistoryService coinHistoryService;
	private final UserItemService userItemService;
	public List<ItemResponseDto> getItems() {
		List<Item> items = itemRepository.findAllByIsHiddenIsTrue();

		return items.stream()
				.map(item -> {
					List<String> imageFileNames = itemImageRepository.findAllByItem(item)
							.stream()
							.map(ItemImage::getFileName)
							.collect(Collectors.toList());

					return ItemResponseDto.of(item, imageFileNames);
				}).collect(Collectors.toList());
	}

	@Transactional
	public String insertItem(String githubId, ItemRequestDto itemRequestDto) {
		userService.checkAdmin(githubId);

		Item item = Item.builder()
			.name(itemRequestDto.getName())
			.description(itemRequestDto.getDescription())
			.coin(itemRequestDto.getCoin())
			.itemLocation(itemRequestDto.getItemLocation())
			.isHidden(itemRequestDto.getIsHidden())
			.build();
		itemRepository.save(item);

		saveItemImages(item, itemRequestDto.getImages());

		return "아이템 '%s' 이(가) 성공적으로 추가되었습니다.".formatted(item.getName());
	}

	private void saveItemImages(Item item, List<MultipartFile> images) {
		List<ItemImage> itemImages = images.stream()
						.map(imageFile ->
							uploadAndCreateItemImage(item, imageFile)).toList();
		itemImageRepository.saveAll(itemImages);
	}

	private ItemImage uploadAndCreateItemImage(Item item, MultipartFile imageFile) {
		try {
			UploadedImageInfo uploadedImageInfo = imageUploadService.uploadImage(imageFile, ImageType.ITEM, null);
			return ItemImage.builder()
					.item(item)
					.fileName(uploadedImageInfo.getImageName())
					.fullPath(uploadedImageInfo.getFullPath().toString())
					.build();
		} catch (FileUploadException e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional
	public String updateItemInfo(String githubId, ItemUpdateRequestDto itemUpdateRequestDto) {
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
		// TODO: 업로드한 이미지 파일 자체를 삭제하는 로직 필요
		itemRepository.delete(item);

		return "아이템 '%s' 이(가) 성공적으로 삭제되었습니다.".formatted(item.getName());
	}

	private void updateItemDetails(Item item, ItemUpdateRequestDto requestDto) {
		item.setName(requestDto.getName());
		item.setDescription(requestDto.getDescription());
		item.setCoin(requestDto.getCoin());
		item.setItemLocation(requestDto.getItemLocation());
		item.setIsHidden(requestDto.getIsHidden());
	}

	@Transactional
	public String purchaseItem(String githubId, Long itemId) {
		User user = userService.findUser(githubId);
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));

		checkPurchaseEligibility(user, item);

		userItemService.createUserItem(user, item);
		updateUserCoin(user, item);
		return "아이템 '%s' 을(를) 성공적으로 구매하였습니다.";
	}

	private void updateUserCoin(User user, Item item) {
		user.getUserProfile().setCoin(user.getUserProfile().getCoin() - item.getCoin());
		userService.saveUser(user);
		coinHistoryService.addCoinHistory(
				user, -item.getCoin(), CoinType.BUY_ITEM, "%s 아이템 구매".formatted(item.getName()));
	}


	private void checkPurchaseEligibility(User user, Item item) {
		if (user.getUserProfile().getCoin() < item.getCoin()) {
			throw new IllegalArgumentException("코인이 부족합니다.");
		}

		if (userItemService.isExists(user, item)) {
			throw new AlreadyPurchasedException("이미 구매한 아이템입니다.");
		}
	}

	public List<UserItemResponseDto> getUserItems(String githubId) {
		User user = userService.findUser(githubId);
		return userItemService.getUserItems(user);
	}

	public String updateUserItemActive(String githubId, Long userItemId) {
		User user = userService.findUser(githubId);
		return userItemService.updateUserItemActive(user, userItemId);
	}
}
