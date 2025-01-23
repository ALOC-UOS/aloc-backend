package com.aloc.aloc.notion;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
