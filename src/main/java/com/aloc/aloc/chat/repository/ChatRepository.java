package com.aloc.aloc.chat.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.aloc.aloc.chat.dto.ChatRoom;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Repository
public class ChatRepository {
	private final Map<String, ChatRoom> chatRooms;

	public void save(String roomId, ChatRoom chatRoom) {
		chatRooms.put(roomId, chatRoom);
	}

	public ChatRoom findById(String roomId) {
		return chatRooms.get(roomId);
	}

	public List<ChatRoom> findAll() {
		return new ArrayList<>(chatRooms.values());
	}
}
