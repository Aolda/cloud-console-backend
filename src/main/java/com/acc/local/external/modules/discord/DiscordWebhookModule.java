package com.acc.local.external.modules.discord;

import com.acc.global.exception.discord.DiscordErrorCode;
import com.acc.global.exception.discord.DiscordException;
import com.acc.global.properties.DiscordProperties;
import com.acc.local.external.dto.google.GoogleFormRequest;
import com.acc.local.external.modules.discord.templates.DiscordMessageFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordWebhookModule {

    private final WebClient discordWebClient;
    private final DiscordProperties discordProperties;
    private final DiscordMessageFormatter formatter;

    public void sendNewProjectNotification(GoogleFormRequest request) {
        String messageBody = formatter.createDiscordMessage(request);
        sendMessage(discordProperties.getWebhook().getUrl(), messageBody);
    }

    private void sendMessage(String webhookUrl, String jsonBody) {
        try {
            String webhookPath = extractPathForUrl(webhookUrl);

            discordWebClient.post()
                    .uri(webhookPath)
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientException e) {
            throw new DiscordException(DiscordErrorCode.WEBHOOK_SEND_FAILURE, e);
        }
    }

    private String extractPathForUrl(String fullUrl) {
        final String BASE_URL = "https://discord.com/api/webhooks/";
        return fullUrl.substring(BASE_URL.length());
    }
}
