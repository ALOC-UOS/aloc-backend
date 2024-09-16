package com.aloc.aloc.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.aloc.aloc.chat.dto.ChatMessage;
import com.aloc.aloc.chat.dto.ChatMessage.MessageType;
import com.aloc.aloc.chat.dto.ChatRoom;
import com.aloc.aloc.chat.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

	private final ChatRepository chatRepository;
	private final ObjectMapper objectMapper;
	private static final String DEFAULT_ROOM_ID = "defaultRoom";

	@PostConstruct
	public void init() {
		if (findRoomById(DEFAULT_ROOM_ID) == null) {
			createRoom("Default Chat Room");
		}
	}

	public ChatRoom findRoomById(String roomId) {
		if (roomId == null || roomId.isEmpty()) {
			log.error("Room ID must not be null");
		}
		return chatRepository.findById(roomId);
	}

	public ChatRoom getDefaultRoom() {
		ChatRoom room = findRoomById(DEFAULT_ROOM_ID);
		if (room == null) {
			room = createRoom("Default Chat Room");
			room.setRoomId(DEFAULT_ROOM_ID);
			chatRepository.save(DEFAULT_ROOM_ID, room);
		}
		return room;
	}

	public ChatRoom createRoom(String name) {
		ChatRoom chatRoom = ChatRoom.builder()
			.name(name)
			.build();
		chatRepository.save(chatRoom.getRoomId(), chatRoom);
		return chatRoom;
	}

	public void handleAction(
		WebSocketSession session,
		ChatMessage chatMessage
	) throws JsonProcessingException {
		ChatRoom room = getDefaultRoom();
		if (!isSessionValid(session)) {
			log.error("WebSocket session is not valid or closed");
			return;
		}
		boolean isNewUser = isNewUser(room, session);
		if (isNewUser) {
			room.join(session, chatMessage.getSender(), chatMessage.getSenderInfo());
			ChatMessage joinMessage = new ChatMessage();
			joinMessage.setMessage("사용자가 채팅방에 들어왔습니다. 🙋🏻‍");
			joinMessage.setSender("알림");
			joinMessage.setType(MessageType.NOTICE);
			room.sendMessage(new TextMessage(objectMapper.writeValueAsString(joinMessage)));
		}

		TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
		room.sendMessage(textMessage);
	}

	private boolean isSessionValid(WebSocketSession session) {
		return session != null && session.isOpen();
	}

	private boolean isNewUser(ChatRoom room, WebSocketSession session)  {
		return !room.hasSession(session);
	}

	public void leaveRoom(WebSocketSession session) {
		ChatRoom room = getDefaultRoom();
		if (room.hasSession(session)) {
			room.leave(session);
			ChatMessage leaveMessage = new ChatMessage();
			leaveMessage.setType(MessageType.LEAVE);
			leaveMessage.setSender("System");
			leaveMessage.setMessage("사용자가 채팅방을 나갔습니다.");
			try {
				handleAction(session, leaveMessage);
			} catch (Exception e) {
				log.error("Error sending leave message", e);
			}
		}
	}
}
