package com.aloc.aloc.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	@GetMapping("/")
	public String index() {
		return "hello world!";
	}

	@GetMapping("/show/user")
	public String showUser() {
		return "show user";
	}
}
