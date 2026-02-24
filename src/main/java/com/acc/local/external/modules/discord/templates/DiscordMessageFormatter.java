package com.acc.local.external.modules.discord.templates;

import com.acc.global.properties.DiscordProperties;
import com.acc.local.external.dto.google.GoogleFormRequest;
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

    public String createProjectRequestCreatedMessage(
            String requesterName,
            String requesterEmail,
            String projectName,
            String projectRequestId,
            String projectDescription
    ) {
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
                        "value": "이름: %s\\n이메일: %s",
                        "inline": false
                    },
                    {
                        "name": "💻 프로젝트 정보",
                        "value": "프로젝트명: %s\\n프로젝트 요청 ID: %s",
                        "inline": false
                    },
                    {
                        "name": "📋 프로젝트 목적",
                        "value": "%s",
                        "inline": false
                    }
                ],
                "footer": {
                    "text": "ACC Cloud Project - 신청 접수"
                }
            }]
        }
        """.formatted(
                mentionBlock,
                escapeMarkdown(requesterName),
                escapeMarkdown(requesterEmail),
                escapeMarkdown(projectName),
                escapeMarkdown(projectRequestId),
                escapeMarkdown(projectDescription)
        );
    }

    public String createProjectApprovalMessage(
            String requesterName,
            String requesterEmail,
            String projectName,
            String projectRequestId,
            String projectDescription,
            String createdProjectId
    ) {
        List<String> adminMentions = discordProperties.getAdmin().getMentions();
        String mentionBlock = adminMentions.stream()
                .map(id -> "<@!" + id + ">")
                .collect(Collectors.joining(" "));

        return """
        {
            "content": "%s",
            "embeds": [{
                "title": "✅ 프로젝트 신청 승인",
                "color": 3066993,
                "fields": [
                    {
                        "name": "👤 신청자 정보",
                        "value": "이름: %s\\n이메일: %s",
                        "inline": false
                    },
                    {
                        "name": "💻 프로젝트 정보",
                        "value": "프로젝트명: %s\\n프로젝트 요청 ID: %s\\n생성된 프로젝트 ID: %s",
                        "inline": false
                    },
                    {
                        "name": "📋 프로젝트 목적",
                        "value": "%s",
                        "inline": false
                    }
                ],
                "footer": {
                    "text": "ACC Cloud Project - 승인됨"
                }
            }]
        }
        """.formatted(
                mentionBlock,
                escapeMarkdown(requesterName),
                escapeMarkdown(requesterEmail),
                escapeMarkdown(projectName),
                escapeMarkdown(projectRequestId),
                escapeMarkdown(createdProjectId),
                escapeMarkdown(projectDescription)
        );
    }

    public String createProjectRejectionMessage(
            String requesterName,
            String requesterEmail,
            String projectName,
            String projectRequestId,
            String projectDescription,
            String rejectReason
    ) {
        List<String> adminMentions = discordProperties.getAdmin().getMentions();
        String mentionBlock = adminMentions.stream()
                .map(id -> "<@!" + id + ">")
                .collect(Collectors.joining(" "));

        return """
        {
            "content": "%s\\n\\n━━━━━━━━━━━━━━━━━━━━━",
            "embeds": [{
                "title": "❌ 프로젝트 신청 거부",
                "color": 15158332,
                "fields": [
                    {
                        "name": "👤 신청자 정보",
                        "value": "**📛 이름:** %s\\n**📧 이메일:** %s",
                        "inline": false
                    },
                    {
                        "name": "💻 프로젝트 정보",
                        "value": "**📝 프로젝트명:** %s\\n**🆔 프로젝트 요청 ID:** `%s`",
                        "inline": false
                    },
                    {
                        "name": "📋 프로젝트 설명",
                        "value": "%s",
                        "inline": false
                    },
                    {
                        "name": "🚫 거부 사유",
                        "value": "%s",
                        "inline": false
                    }
                ],
                "footer": {
                    "text": "ACC Cloud Project - 거부됨"
                }
            }]
        }
        """.formatted(
                mentionBlock,
                escapeMarkdown(requesterName),
                escapeMarkdown(requesterEmail),
                escapeMarkdown(projectName),
                escapeMarkdown(projectRequestId),
                escapeMarkdown(projectDescription),
                escapeMarkdown(rejectReason)
        );
    }

    public String createProjectDirectlyCreatedMessage(String projectId, String projectName, String ownerName) {
        List<String> adminMentions = discordProperties.getAdmin().getMentions();
        String mentionBlock = adminMentions.stream()
                .map(id -> "<@!" + id + ">")
                .collect(Collectors.joining(" "));

        return """
        {
            "content": "%s\\n\\n━━━━━━━━━━━━━━━━━━━━━",
            "embeds": [{
                "title": "🎉 프로젝트 생성 완료",
                "color": 10181046,
                "fields": [
                    {
                        "name": "💻 프로젝트 정보",
                        "value": "**📝 프로젝트명:** %s\\n**🆔 프로젝트 ID:** `%s`",
                        "inline": false
                    },
                    {
                        "name": "👤 프로젝트 소유자",
                        "value": "**📛 소유자:** %s",
                        "inline": false
                    }
                ],
                "footer": {
                    "text": "ACC Cloud Project - 관리자가 직접 생성"
                }
            }]
        }
        """.formatted(
                mentionBlock,
                escapeMarkdown(projectName),
                escapeMarkdown(projectId),
                escapeMarkdown(ownerName)
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
