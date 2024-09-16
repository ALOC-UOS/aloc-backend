package com.aloc.aloc.chat.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.aloc.aloc.chat.dto.ChatMessage;
import com.aloc.aloc.chat.dto.ChatMessage.MessageType;
import com.aloc.aloc.chat.dto.ChatRoom;
import com.aloc.aloc.chat.dto.SenderInfo;
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

	public List<ChatRoom> findAll() {
		return chatRepository.findAll();
	}

	public ChatRoom findRoomById(String roomId) {
		if (roomId == null || roomId.isEmpty()) {
			throw new IllegalArgumentException("Room ID must not be null");
		}
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
		if (!isSessionValid(session)) {
			log.error("WebSocket session is not valid or closed");
			return;
		}

		if (room == null) {
			throw new RuntimeException("Room not found: " + roomId);
		}

		if (isEnterRoom(room, chatMessage.getSender(), chatMessage.getSenderInfo())) {
			room.join(session, chatMessage.getSender(), chatMessage.getSenderInfo());
			chatMessage.setMessage("새로운 분이 등장했어요 🙋🏻");
			chatMessage.setSender("알림");
			chatMessage.setType(MessageType.NOTICE);
		}

		TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
		room.sendMessage(textMessage);
	}

	private boolean isSessionValid(WebSocketSession session) {
		return session != null && session.isOpen();
	}

	private boolean isEnterRoom(ChatRoom room, String sender, SenderInfo senderInfo)  {
		Set<String> userList = room.getUserList();
		boolean isNewUser = userList.stream()
			.noneMatch(existingUser -> existingUser.equals(sender));

		if (isNewUser) {
			userList.add(sender);
		}
		return isNewUser;
	}

	public void leaveAllRooms(WebSocketSession session) {
		List<ChatRoom> rooms = findAll();
		for (ChatRoom room : rooms) {
			if (room.hasSession(session)) {
				room.leave(session);
				ChatMessage leaveMessage = new ChatMessage();
				leaveMessage.setType(MessageType.LEAVE);
				leaveMessage.setSender("System");
				leaveMessage.setMessage("사용자가 채팅방을 나갔습니다.");
				try {
					handleAction(room.getRoomId(), session, leaveMessage);
				} catch (Exception e) {
					log.error("Error sending leave message", e);
				}
			}
		}
	}
}
