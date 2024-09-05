package com.aloc.aloc.chat.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.aloc.aloc.chat.dto.ChatMessage;
import com.aloc.aloc.chat.dto.ChatMessage.MessageType;
import com.aloc.aloc.chat.dto.ChatRoom;
import com.aloc.aloc.chat.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

	private final ChatRepository chatRepository;
	private final ObjectMapper objectMapper;
	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();


	public List<ChatRoom> findAll() {
		return chatRepository.findAll();
	}

	public ChatRoom findRoomById(String roomId) {
		return chatRepository.findById(roomId);
	}

	public ChatRoom createRoom(String name) {
		ChatRoom chatRoom = ChatRoom.of(name);
		chatRepository.save(chatRoom.getRoomId(), chatRoom);
		return chatRoom;
	}

	public void handleAction(
		String roomId,
		WebSocketSession session,
		ChatMessage chatMessage
	) throws JsonProcessingException {
		ChatRoom room = findRoomById(roomId);

		if (isEnterRoom(chatMessage)) {
			room.join(session);
			chatMessage.setMessage("새로운 분이 등장했어요 🙋🏻");
			chatMessage.setSender("알림");
			chatMessage.setType(MessageType.NOTICE);
		}

		TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
		room.sendMessage(textMessage);
	}

	private boolean isEnterRoom(ChatMessage chatMessage) {
		return chatMessage.getType().equals(ChatMessage.MessageType.ENTER);
	}

	public Set<String> getUserList(String roomId) {
		ChatRoom room = findRoomById(roomId);
		return room != null ? room.getUserList() : new HashSet<>();
	}
}
