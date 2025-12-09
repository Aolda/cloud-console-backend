package com.acc.gitlab.bot.service;

import com.acc.gitlab.bot.properties.ClaudeProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClaudeService {

    private final WebClient webClient;
    private final ClaudeProperties claudeProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClaudeService(
            @Qualifier("gitLabBotWebClient") WebClient webClient,
            ClaudeProperties claudeProperties
    ) {
        this.webClient = webClient;
        this.claudeProperties = claudeProperties;
    }

    public String reviewCode(String diff) {
        try {
            String prompt = buildReviewPrompt(diff);

            Map<String, Object> requestBody = Map.of(
                    "model", claudeProperties.getModel(),
                    "max_tokens", claudeProperties.getMaxTokens(),
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    )
            );

            log.info("Sending code review request to Claude API");

            String response = webClient.post()
                    .uri("https://api.anthropic.com/v1/messages")
                    .header("x-api-key", claudeProperties.getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Received response from Claude API");

            return extractReviewFromResponse(response);

        } catch (Exception e) {
            log.error("Error while requesting Claude API", e);
            return "코드 리뷰 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    private String buildReviewPrompt(String diff) {
        return """
                다음 코드 변경사항을 리뷰해주세요.

                리뷰 시 다음 사항들을 중점적으로 확인해주세요:
                1. 버그나 잠재적인 오류 가능성
                2. 코드 품질 및 가독성
                3. 보안 취약점 (SQL Injection, XSS 등)
                4. 성능 이슈
                5. 모범 사례 준수 여부

                코드 변경사항:
                ```
                %s
                ```

                리뷰는 한국어로 작성해주시고, 구체적이고 실행 가능한 피드백을 제공해주세요.
                긍정적인 부분도 언급해주시면 좋습니다.
                """.formatted(diff);
    }

    private String extractReviewFromResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode contentArray = rootNode.get("content");

            if (contentArray != null && contentArray.isArray() && contentArray.size() > 0) {
                JsonNode firstContent = contentArray.get(0);
                String text = firstContent.get("text").asText();
                return text;
            }

            return "리뷰 결과를 파싱할 수 없습니다.";
        } catch (Exception e) {
            log.error("Error parsing Claude response", e);
            return "리뷰 결과를 파싱하는 중 오류가 발생했습니다.";
        }
    }
}
