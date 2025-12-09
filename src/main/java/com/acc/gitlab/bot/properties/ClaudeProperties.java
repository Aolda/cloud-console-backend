package com.acc.gitlab.bot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "claude")
public class ClaudeProperties {
    private String apiKey;
    private String model = "claude-sonnet-4-5-20250929";
    private Integer maxTokens = 4000;
}