package com.aloc.aloc.item.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/item")
public class ItemController {

	@GetMapping("/")
	@Operation(summary = "아이템 목록 조회", description = "공개된 아이템 목록을 조회합니다.")
	public CustomApiResponse<> getItems() {
		return CustomApiResponse.onSuccess();
	}

	@

}
