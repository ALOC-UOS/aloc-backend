package com.aloc.aloc.item.service;

import com.aloc.aloc.item.dto.response.UserItemResponseDto;
import com.aloc.aloc.item.repository.UserItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserItemService {
	private final UserItemRepository userItemRepository;

	public List<UserItemResponseDto> getUserItemsByUserId(Long userId) {

	}
}
