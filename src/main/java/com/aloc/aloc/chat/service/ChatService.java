package com.aloc.aloc.chat.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.aloc.aloc.chat.dto.ChatMessage;
import com.aloc.aloc.chat.dto.ChatMessage.MessageType;
import com.aloc.aloc.chat.dto.ChatRoom;
import com.aloc.aloc.chat.dto.SenderInfo;
import com.aloc.aloc.chat.dto.SenderTotalInfo;
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

		if (room == null) {
			throw new RuntimeException("Room not found: " + roomId);
		}

//		validateMessageType(chatMessage);

		if (isEnterRoom(room, chatMessage.getSender(), chatMessage.getSenderInfo())) {
			room.join(session, chatMessage.getSender(), chatMessage.getSenderInfo());
			chatMessage.setMessage("새로운 분이 등장했어요 🙋🏻");
			chatMessage.setSender("알림");
			chatMessage.setType(MessageType.NOTICE);
		}

		TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
		room.sendMessage(textMessage);
	}

	private boolean isEnterRoom(ChatRoom room, String sender, SenderInfo senderInfo)  {
		Set<String> userList = room.getUserList();
		boolean isNewUser = userList.stream()
			.noneMatch(existingUser -> existingUser.equals(sender));

		if (isNewUser) {
			userList.add(sender);
			room.getUserInfoMap().put(sender, senderInfo);
		}
		return isNewUser;
	}

	public Collection<SenderTotalInfo> getUserList(String roomId) {
		ChatRoom room = findRoomById(roomId);
		if (room == null || room.getUserInfoMap() == null) {
			return new HashSet<>();
		}

		return room.getUserInfoMap().entrySet().stream()
			.map(entry -> new SenderTotalInfo(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
	}
}
