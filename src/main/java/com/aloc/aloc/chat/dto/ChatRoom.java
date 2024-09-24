package com.aloc.aloc.chat.dto;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ChatRoom {
	private String roomId;
	private final String name;
	private final Set<WebSocketSession> sessions = new HashSet<>();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final ConcurrentHashMap<WebSocketSession, String> userMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, SenderInfo> userInfoMap = new ConcurrentHashMap<>();
	@Builder
	public ChatRoom(String name) {
		this.name = name;
		this.roomId = UUID.randomUUID().toString();
	}

	public void sendMessage(TextMessage message) {
		sessions.removeIf(session -> !session.isOpen());
		this.getSessions()
			.parallelStream()
			.forEach(session -> sendMessageToSession(session, message));
	}

	private void sendMessageToSession(WebSocketSession session, TextMessage message) {
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void join(WebSocketSession session, String sender, SenderInfo senderInfo) {
		try {
			sessions.add(session);
			userMap.put(session, sender);
			userInfoMap.put(sender, senderInfo);
			sendUserListToAll();
		} catch (Exception e) {
			log.error("Failed to join user", e);
		}
	}

	public void leave(WebSocketSession session) {
		sessions.remove(session);
		userMap.remove(session);
		sendUserListToAll();
	}

	private void sendUserListToAll() {
		try {
			List<SenderTotalInfo> userList = userInfoMap.entrySet().stream()
				.map(entry -> new SenderTotalInfo(entry.getKey(), entry.getValue()))
				.toList();
			UserListMessage userListMessage = new UserListMessage("USER_LIST", userList);
			TextMessage message = new TextMessage(objectMapper.writeValueAsString(userListMessage));
			sendMessage(message);
		} catch (IOException e) {
			throw new RuntimeException("Failed to send user list", e);
		}
	}

	public boolean hasSession(WebSocketSession session) {
		return sessions.contains(session);
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
}
