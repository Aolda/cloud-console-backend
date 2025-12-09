# GitLab Claude 코드 리뷰 봇 설정 가이드

## 1. 환경 변수 설정

`.env` 파일에 다음 환경 변수들을 추가하세요:

```bash
# GitLab Bot Configuration
GITLAB_URL=https://gitlab.com
GITLAB_ACCESS_TOKEN=your-gitlab-access-token-here
GITLAB_BOT_USERNAME=claude
GITLAB_PROJECT_ID=your-project-id

# Claude API Configuration
CLAUDE_API_KEY=your-anthropic-api-key-here
CLAUDE_MODEL=claude-sonnet-4-5-20250929
CLAUDE_MAX_TOKENS=4000
```

## 2. GitLab Access Token 생성

1. GitLab 프로필 → **Settings** → **Access Tokens** 이동
2. Token name: `claude-bot` (또는 원하는 이름)
3. Scopes 선택:
   - `api` (전체 API 접근)
   - `read_api` (읽기 전용도 가능)
   - `write_repository` (MR 댓글 작성용)
4. **Create personal access token** 클릭
5. 생성된 토큰을 `.env` 파일의 `GITLAB_ACCESS_TOKEN`에 복사

## 3. Anthropic API Key 획득

1. [Anthropic Console](https://console.anthropic.com/) 방문
2. API Keys 메뉴에서 **Create Key** 클릭
3. 생성된 API Key를 `.env` 파일의 `CLAUDE_API_KEY`에 복사

## 4. GitLab Project ID 확인

1. GitLab 프로젝트 페이지로 이동
2. 프로젝트 이름 아래에 있는 **Project ID** 확인 (예: `12345`)
3. 또는 Settings → General에서 확인 가능
4. `.env` 파일의 `GITLAB_PROJECT_ID`에 입력

## 5. GitLab Webhook 설정

1. GitLab 프로젝트에서 **Settings** → **Webhooks** 이동
2. 다음 정보 입력:
   - **URL**: `https://your-server-domain.com/api/gitlab/webhook`
   - **Secret token**: (선택사항, 보안 강화용)
   - **Trigger**:
     - ✅ **Comments** (또는 **Note events**)
3. **SSL verification**: Enable (권장)
4. **Add webhook** 클릭

## 6. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 Docker로 실행:

```bash
docker-compose up -d
```

## 7. 동작 확인

### Health Check
```bash
curl http://localhost:8080/api/gitlab/webhook/health
```

예상 응답:
```
GitLab Bot is running
```

### MR에서 테스트

1. GitLab에서 Merge Request 생성
2. MR 댓글에 다음과 같이 작성:
   ```
   @claude 리뷰해줘
   ```
3. 몇 초 후 Claude의 코드 리뷰 댓글이 자동으로 달립니다

## 8. 사용 가능한 명령어

| 명령어 | 설명 |
|--------|------|
| `@claude 리뷰해줘` | 전체 코드 리뷰 |
| `@claude 보안 체크해줘` | 보안 중점 리뷰 |
| `@claude 성능 최적화 제안해줘` | 성능 관점 리뷰 |
| `@claude 테스트 리뷰해줘` | 테스트 코드 리뷰 |

## 9. 트러블슈팅

### Webhook이 작동하지 않는 경우

1. **Webhook 로그 확인**:
   - GitLab: Settings → Webhooks → Edit → Recent events
   - 응답 코드가 200이 아닌 경우 에러 메시지 확인

2. **서버 로그 확인**:
   ```bash
   # 애플리케이션 로그 확인
   tail -f logs/application.log
   ```

3. **방화벽 확인**:
   - 서버가 외부에서 접근 가능한지 확인
   - GitLab이 서버로 요청을 보낼 수 있는지 확인

### Claude API 오류

1. **API Key 확인**: `.env` 파일의 `CLAUDE_API_KEY`가 올바른지 확인
2. **요금 한도 확인**: Anthropic Console에서 사용량 확인
3. **모델 이름 확인**: 최신 모델 이름이 올바른지 확인

### GitLab API 오류

1. **Access Token 권한 확인**: `api`, `write_repository` 스코프가 있는지 확인
2. **Project ID 확인**: 올바른 프로젝트 ID인지 확인
3. **Token 만료 확인**: Access Token이 만료되지 않았는지 확인

## 10. 보안 고려사항

1. **환경 변수 보호**: `.env` 파일을 절대 git에 커밋하지 마세요
2. **HTTPS 사용**: Webhook은 반드시 HTTPS로 설정하세요
3. **Secret Token**: Webhook에 Secret Token을 설정하여 위조 요청 방지
4. **최소 권한**: GitLab Access Token은 필요한 최소 권한만 부여

## 11. 고급 기능 (선택사항)

### Webhook Secret Token 검증

`GitLabWebhookController`에 다음과 같이 검증 로직을 추가할 수 있습니다:

```java
@PostMapping
public ResponseEntity<String> handleWebhook(
        @RequestBody GitLabWebhookEvent event,
        @RequestHeader(value = "X-Gitlab-Token", required = false) String token) {

    if (!isValidToken(token)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }

    // ... 기존 로직
}
```

### 비동기 처리

대용량 MR의 경우 리뷰 시간이 오래 걸릴 수 있으므로 `@Async`를 사용하여 비동기 처리를 고려하세요.

## 참고 자료

- [GitLab Webhook 문서](https://docs.gitlab.com/ee/user/project/integrations/webhooks.html)
- [Anthropic API 문서](https://docs.anthropic.com/claude/reference/getting-started-with-the-api)
- [Spring Boot WebClient 문서](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)