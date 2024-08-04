package com.aloc.aloc.notion;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;


@Configuration
@ConfigurationProperties(prefix = "notion")
@Getter
@Setter
public class NotionProperty {
	private String token;
	private Map<String, NotionDatabase> databases;
	@Getter
	@Setter
	public static class NotionDatabase {
		private String id;
	}
}
