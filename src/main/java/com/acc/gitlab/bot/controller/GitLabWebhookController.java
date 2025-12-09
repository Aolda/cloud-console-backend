package com.acc.gitlab.bot.controller;

import com.acc.gitlab.bot.dto.GitLabWebhookEvent;
import com.acc.gitlab.bot.service.GitLabBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/gitlab/webhook")
@RequiredArgsConstructor
public class GitLabWebhookController {

    private final GitLabBotService gitLabBotService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody GitLabWebhookEvent event,
            @RequestHeader(value = "X-Gitlab-Event", required = false) String gitlabEvent) {

        log.info("Received GitLab webhook: {}", gitlabEvent);

        try {
            gitLabBotService.handleWebhookEvent(event);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing webhook: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("GitLab Bot is running");
    }
}