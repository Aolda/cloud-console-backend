package com.acc.local.service.modules.discord.templates;

import com.acc.global.properties.DiscordProperties;
import com.acc.local.dto.google.GoogleFormRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscordMessageFormatter {

    private final DiscordProperties discordProperties;

    public String createDiscordMessage(GoogleFormRequest request) {
        List<String> adminMentions = discordProperties.getAdmin().getMentions();
        String mentionBlock = adminMentions.stream()
                .map(id -> "<@!" + id + ">")
                .collect(Collectors.joining(" "));

        return """
        {
            "content": "%s",
            "embeds": [{
                "title": "🌟 새로운 클라우드 프로젝트 신청",
                "color": 3447003,
                "fields": [
                    {
                        "name": "👤 신청자 정보",
                        "value": "이름: %s\\n학과: %s\\n학번: %d\\n이메일: %s",
                        "inline": false
                    },
                    {
                        "name": "💻 프로젝트 정보",
                        "value": "프로젝트명: %s\\n프로젝트 ID: %s",
                        "inline": false
                    },
                    {
                        "name": "📋 프로젝트 목적",
                        "value": "%s",
                        "inline": false
                    },
                    {
                        "name": "🔧 요청 리소스",
                        "value": "CPU: %d cores\\nMemory: %d GB\\nVolume: %d GB",
                        "inline": false
                    }
                ],
                "timestamp": "%s",
                "footer": {
                    "text": "ACC Cloud Project"
                }
            }]
        }
        """.formatted(
                mentionBlock,
                escapeMarkdown(request.name()),
                escapeMarkdown(request.department()),
                request.studentId(),
                escapeMarkdown(request.email()),
                escapeMarkdown(request.projectName()),
                escapeMarkdown(request.projectId()),
                escapeMarkdown(request.projectPurpose()),
                request.cpuCores(),
                request.memorySize(),
                request.volumeSize(),
                request.timestamp()
        );
    }

    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("|", "\\|");
    }
}
