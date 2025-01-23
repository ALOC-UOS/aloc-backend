package com.aloc.aloc.chat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserListMessage {
  private String type;
  private List<SenderTotalInfo> userList;
}
