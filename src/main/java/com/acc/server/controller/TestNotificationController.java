package com.acc.server.controller;

import com.acc.server.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestNotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/alert/email")
    public String testEmailAlert(@RequestParam String to) {
        String html = """
            <html>
              <body>
                <h2 style="color:darkblue;">Aolda Cloud 알림</h2>
                <p>안녕하세요,</p>
                <p><b>VM에서 문제가 감지</b>되었습니다.</p>
                <p>즉시 확인 바랍니다. ⚠️</p>
                <p>👉 <a href="https://console.aoldacloud.com">콘솔 바로가기</a></p>
              </body>
            </html>
        """;

        notificationService.sendHtmlEmail(to, "[AoldaCloud] VM 문제 발생", html);
        return "메일 전송 완료 to " + to;
    }
}
