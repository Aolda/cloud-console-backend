package com.acc.server.service;/*
package com.acc.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // Discord 알림
    public void sendDiscordAlert(String content) {
        String webhookUrl = "https://discord.com/api/webhooks/your_webhook_id";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        body.put("content", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(webhookUrl, entity, String.class);
    }

    // 이메일 알림
    public void sendEmailAlert(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("your-ajou-email@ajou.ac.kr"); // 반드시 발신자 명시

        mailSender.send(message);
    }
}
*/
//테스트 코드
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);  // 두 번째 인자 true → HTML로 인식
            helper.setFrom("your-ajou-email@ajou.ac.kr");

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // 예외 로깅 혹은 Discord 알림으로 보낼 수도 있음
        }
    }
}
