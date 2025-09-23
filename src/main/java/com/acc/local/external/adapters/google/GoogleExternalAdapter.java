package com.acc.local.external.adapters.google;

import com.acc.local.external.dto.google.GoogleFormRequest;
import com.acc.local.external.modules.email.EmailNotificationModule;
import com.acc.local.external.modules.google.GoogleSheetReadModule;
import com.acc.local.external.ports.GoogleExternalPort;
import com.acc.local.external.modules.discord.DiscordWebhookModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoogleExternalAdapter implements GoogleExternalPort {

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
