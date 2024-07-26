package com.aloc.aloc.notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService {
	private final RestTemplate restTemplate;

	@Value("${discord.webhook.url}")
	private String discordWebhookUrl;

	public void sendNotification(String message) {
		Map<String, String> request = new HashMap<>();
		request.put("content", message);

		restTemplate.postForObject(discordWebhookUrl, request, String.class);
	}
}
