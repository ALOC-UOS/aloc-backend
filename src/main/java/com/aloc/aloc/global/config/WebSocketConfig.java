package com.aloc.aloc.global.config;

import com.aloc.aloc.chat.base.WebSocketChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {
  private final WebSocketChatHandler webSocketChatHandler;

  public WebSocketConfig(WebSocketChatHandler webSocketChatHandler) {
    this.webSocketChatHandler = webSocketChatHandler;
    log.info("WebSocketConfig constructor called");
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    log.info("WebSocketConfig.registerWebSocketHandlers called");
    registry.addHandler(webSocketChatHandler, "/ws/chat").setAllowedOrigins("*");
  }
}
