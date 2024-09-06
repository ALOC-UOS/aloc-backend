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

@Getter
public class ChatRoom {
	private final String roomId;
	private final String name;
	private final Set<WebSocketSession> sessions = new HashSet<>();
	private final ObjectMapper objectMapper;
	private final ConcurrentHashMap<WebSocketSession, String> userMap;
	private final ConcurrentHashMap<String, SenderInfo> userInfoMap;
	@Builder
	public ChatRoom(String roomId, String name) {
		this.roomId = roomId;
		this.name = name;
		this.objectMapper = new ObjectMapper();
		this.userMap = new ConcurrentHashMap<>();
		this.userInfoMap = new ConcurrentHashMap<>();
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
		sessions.add(session);
		userMap.put(session, sender);
		userInfoMap.put(sender, senderInfo);
		sendUserListToAll();
	}

	public void leave(WebSocketSession session) {
		sessions.remove(session);
		userMap.remove(session);
		sendUserListToAll();
	}

	public Set<String> getUserList() {
		return new HashSet<>(userMap.values());
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

	public static ChatRoom of(String name) {
		return ChatRoom.builder()
			.name(name)
			.roomId(UUID.randomUUID().toString())
			.build();
	}
}
