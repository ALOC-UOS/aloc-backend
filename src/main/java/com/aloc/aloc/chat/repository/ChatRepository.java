package com.aloc.aloc.chat.repository;

import com.aloc.aloc.chat.dto.ChatRoom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatRepository {
  private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

  public void save(String roomId, ChatRoom chatRoom) {
    chatRooms.put(roomId, chatRoom);
  }

  public ChatRoom findById(String roomId) {
    return chatRooms.get(roomId);
  }
}
