package com.acc.local.service.modules.email;

import com.acc.global.properties.EmailProperties;
import com.acc.local.dto.google.GoogleFormRequest;
import com.acc.local.service.modules.email.templates.EmailContentBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationModule {

    private final EmailContentBuilder contentBuilder;
    private final EmailSenderModule emailSender;
    private final EmailProperties emailProperties;

    public void sendNewProjectNotification(GoogleFormRequest request) {
        String emailBody = contentBuilder.createHtmlContext(request);
        emailSender.sendEmail(request.email(), emailProperties.getSubject(), emailBody);
    }
}
