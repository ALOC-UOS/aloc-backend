package com.aloc.aloc.chat.dto;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatMessage {
	private MessageType type;
	private String roomId;
	private String sender;
	private String message;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static ChatMessage of(String payload) {
		try {
			return objectMapper.readValue(payload, ChatMessage.class);
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse ChatMessage", e);
		}
	}

	public enum MessageType {
		ENTER, TALK, NOTICE
	}

}
