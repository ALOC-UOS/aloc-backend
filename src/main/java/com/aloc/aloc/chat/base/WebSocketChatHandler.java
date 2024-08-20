package com.aloc.aloc.chat.base;

import com.aloc.aloc.chat.dto.ChatMessage;
import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.aloc.aloc.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
	private final ChatService chatService;
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)
		throws IOException {
		log.info("Received message from session {}: {}", session.getId(), message.getPayload());
		String payload = message.getPayload();
		ChatMessage chatMessage = ChatMessage.of(payload);
		chatService.handleAction(chatMessage.getRoomId(), session, chatMessage);
	}
}
