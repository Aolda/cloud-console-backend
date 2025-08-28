package com.acc.local.service.adapters.google;

import com.acc.local.dto.google.GoogleFormRequest;
import com.acc.local.service.modules.email.EmailNotificationModule;
import com.acc.local.service.modules.email.EmailSenderModule;
import com.acc.local.service.modules.google.GoogleSheetReadModule;
import com.acc.local.service.ports.GoogleServicePort;
import com.acc.local.service.modules.discord.DiscordWebhookModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoogleServiceServiceAdapter implements GoogleServicePort {

    private final GoogleSheetReadModule googleSheetReadModule;
    private final DiscordWebhookModule discordWebhookModule;
    private final EmailNotificationModule emailNotificationModule;

    @Override
    public List<GoogleFormRequest> readSheetData() {
        return googleSheetReadModule.read();
    }

    @Override
    public void receiveFormSubmission(GoogleFormRequest request) {
        discordWebhookModule.sendNewProjectNotification(request);
        emailNotificationModule.sendNewProjectNotification(request);
    }
}
