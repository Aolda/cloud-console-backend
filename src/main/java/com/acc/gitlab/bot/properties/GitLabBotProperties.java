package com.acc.gitlab.bot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gitlab.bot")
public class GitLabBotProperties {
    private String url;
    private String accessToken;
    private String botUsername = "claude";
    private Long projectId;
}