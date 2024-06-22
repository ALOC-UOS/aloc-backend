package com.aloc.aloc.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.domain.user.dto.request.UserRequestDto;
import com.aloc.aloc.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

public class UserController {
	private final UserService userService;
	@GetMapping("/")
	public String index() {
		return "hello world!";
	}

	@GetMapping("/show/user")
	public String showUser() {
		return "show user";
	}

	@PostMapping("/sign-up")
	public void signUp(@RequestBody UserRequestDto userRequestDto) {
		System.out.println(userRequestDto);
		userService.signUp(userRequestDto);
	}
}
