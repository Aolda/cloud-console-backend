package com.acc.gitlab.bot.client;

import com.acc.gitlab.bot.dto.GitLabMergeRequestChanges;
import com.acc.gitlab.bot.properties.GitLabBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GitLabApiClient {

    private final WebClient webClient;
    private final GitLabBotProperties gitLabBotProperties;

    public GitLabApiClient(
            @Qualifier("gitLabBotWebClient") WebClient webClient,
            GitLabBotProperties gitLabBotProperties
    ) {
        this.webClient = webClient;
        this.gitLabBotProperties = gitLabBotProperties;
    }

    public GitLabMergeRequestChanges getMergeRequestChanges(Long projectId, Long mergeRequestIid) {
        String url = String.format("%s/api/v4/projects/%d/merge_requests/%d/changes",
                gitLabBotProperties.getUrl(), projectId, mergeRequestIid);

        log.info("Fetching MR changes from GitLab: {}", url);

        return webClient.get()
                .uri(url)
                .header("PRIVATE-TOKEN", gitLabBotProperties.getAccessToken())
                .retrieve()
                .bodyToMono(GitLabMergeRequestChanges.class)
                .block();
    }

    public void postComment(Long projectId, Long mergeRequestIid, String comment) {
        String url = String.format("%s/api/v4/projects/%d/merge_requests/%d/notes",
                gitLabBotProperties.getUrl(), projectId, mergeRequestIid);

        log.info("Posting comment to GitLab MR: {}", url);

        webClient.post()
                .uri(url)
                .header("PRIVATE-TOKEN", gitLabBotProperties.getAccessToken())
                .bodyValue(new CommentRequest(comment))
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        log.info("Comment posted successfully");
    }

    private record CommentRequest(String body) {
    }
}
