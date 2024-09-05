package com.aloc.aloc.chat.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserListMessage {
	private String type;
	private Set<String> users;
}
