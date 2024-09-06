package com.aloc.aloc.chat.controller;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.chat.dto.ChatRoom;
import com.aloc.aloc.chat.dto.SenderInfo;
import com.aloc.aloc.chat.service.ChatService;


import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api2/chat")
public class ChatController {
	private final ChatService chatService;

	@PostMapping
	public ChatRoom createRoom(@RequestParam String name) {
		return chatService.createRoom(name);
	}

	@GetMapping
	public List<ChatRoom> getAll() {
		return chatService.findAll();
	}

	@GetMapping("/user")
	public Collection<SenderInfo> getUserList(@RequestParam String roomId) {
		return chatService.getUserList(roomId);
	}
}
