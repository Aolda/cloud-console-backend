package com.acc.local.external.modules.email;

import com.acc.global.exception.email.EmailErrorCode;
import com.acc.global.exception.email.EmailException;
import com.acc.global.properties.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSenderModule {
    
    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public void sendEmail(String to, String subject, String htmlContent) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(emailProperties.getFrom());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new EmailException(EmailErrorCode.EMAIL_MESSAGE_CREATION_FAILURE, e);
        } catch (MailException e) {
            throw new EmailException(EmailErrorCode.EMAIL_SEND_FAILURE, e);
        }
    }
}
