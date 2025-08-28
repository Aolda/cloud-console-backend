package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "discord")
@Getter @Setter
public class DiscordProperties {

    private Webhook webhook = new Webhook();
    private Admin admin = new Admin();

    @Getter @Setter
    public static class Webhook {
        private String url;
    }

    @Getter @Setter
    public static class Admin {
        private List<String> mentions;
    }
}
