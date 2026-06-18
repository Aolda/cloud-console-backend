# Keycloak OIDC 콜백 처리 흐름

이 문서는 Keycloak Authorization Code Flow 기반 로그인 처리 흐름을 설명합니다. 핵심 진입점은 `KeycloakAuthController`이며, 실제 오케스트레이션은 `KeycloakAuthModule`과 `KeycloakUserModule`이 담당합니다.

---

## 전체 흐름

프론트엔드가 `GET /api/v1/auth/keycloak/login`을 호출하면 백엔드는 CSRF 방지용 state UUID를 생성하여 `keycloak-oauth-state` 쿠키에 저장하고, Keycloak 인가 URL로 302 리다이렉트합니다.

사용자가 Keycloak에서 인증을 마치면 `GET /api/v1/auth/keycloak/callback?code=...&state=...`으로 돌아옵니다. 백엔드는 state 쿠키와 쿼리 파라미터를 비교하여 CSRF를 검증한 뒤 `KeycloakAuthModule.processCallback()`을 호출합니다.

processCallback() 내부 처리 순서입니다.
- code를 Keycloak Token Endpoint에 전달하여 access/refresh/id token을 교환합니다.
- ID Token을 Base64 디코딩하여 `KeycloakIdTokenClaims`를 추출합니다.
- `isLinkedAdmin()`으로 관리자 여부를 확인합니다. 관리자이면 학적 검증을 건너뜁니다.
- 일반 사용자는 `ajou_status`, `ajou_major` 클레임으로 재학생(UNDERGRADUATE) 여부를 검증합니다.
- 검증을 통과하면 `KeycloakUserModule.findOrRegisterKeycloakUser()`로 사용자를 분기 처리합니다.
- Keystone unscoped token을 발급하고, 전체 토큰과 사용자 정보를 SessionData로 묶어 Redis에 저장합니다.
- sessionId를 반환하면 컨트롤러가 `acc-session-id` 쿠키를 설정하고 `KEYCLOAK_FRONTEND_REDIRECT_URL`로 302 리다이렉트합니다.

---

## 사용자 조회/등록 3-way 분기

`KeycloakUserModule.findOrRegisterKeycloakUser()`는 아래 순서로 분기합니다.

**Branch 1. keycloakUserId 연결된 사용자 (일반 로그인)**
`user_detail.keycloak_user_id = claims.subject()`인 레코드가 있으면 저장된 keystoneUsername과 AES-256 복호화된 keystonePassword를 그대로 반환합니다. 외부 API 호출 없이 DB 조회와 복호화만으로 처리됩니다.

**Branch 2. 이메일 일치 기존 사용자 (Account Linking)**
keycloak_user_id는 없지만 `user_auth_detail.user_email = claims.email()`인 레코드가 있을 때 진입합니다. 기존에 ADMIN 또는 GOOGLE 방식으로 가입한 사용자가 Keycloak으로 최초 로그인하는 경우입니다. 기존 Keystone 패스워드를 알 수 없으므로 시스템 어드민 토큰으로 패스워드를 재설정하고, `user_detail`에 keycloak_user_id, keystoneUsername, keystonePassword(암호화)를 업데이트합니다. 이 시점 이후 Keystone 직접 패스워드 로그인은 불가합니다.

**Branch 3. 신규 사용자 등록**
keycloak_user_id도, 이메일 일치도 없는 완전히 새로운 사용자입니다. Keystone에 사용자를 새로 생성(email 앞부분을 name으로 사용, Skyline의 @ 도메인 인식 문제 회피)하고, `user_detail`과 `user_auth_detail`(auth_type=3, KEYCLOAK)을 DB에 저장합니다. departDto가 null인 경우 `USER_NOT_FOUND`로 처리합니다. Keystone 생성 성공 후 DB 저장이 실패하면 Keystone 고아 사용자가 발생할 수 있으므로, 운영 환경에서는 보상 트랜잭션 추가를 검토해야 합니다.

---

## ID Token 클레임

`KeycloakIdTokenParser.extractClaims()`가 추출하는 클레임입니다. sub, email, preferred_username은 openid 스코프에 기본 포함되며, ajou_* 클레임은 Keycloak Client Mapper를 별도 설정해야 합니다. Mapper 설정 경로는 `Clients → {client} → Client Scopes → {client}-dedicated → Add mapper`이며 User Attribute 타입으로 등록합니다.

- `sub` → keycloakUserId. Branch 1~3 분기 기준 및 세션 저장에 사용됩니다.
- `email` → 계정 연결(Branch 2) 조회 기준입니다. 없으면 빈 문자열로 처리합니다.
- `name` / `given_name` / `family_name` → ACC 표시 이름(userName) 초기값입니다. `name`이 없으면 `family_name + given_name`을 사용합니다. 표준 이름 클레임이 없으면 `ajou_lastName + ajou_firstName`을 fallback으로 사용합니다.
- `preferred_username` → 표시 이름 클레임이 없을 때의 fallback 값입니다.
- `ajou_major` → 학적 검증 및 department 저장에 사용됩니다. `univ_depart_info` 테이블에 매핑이 없으면 ajou_major 값 자체를 department로 저장합니다(신설학과 대응).
- `ajou_status` → 재학 상태 코드입니다. `SS0001(학생(학부))` 형태로 오며 괄호 이하는 파싱 시 제거됩니다. UNDERGRADUATE 여부 검증에 사용됩니다.
- `ajou_grade` → 학년입니다. 정수 파싱에 실패하면 -1로 저장됩니다.
- `ajou_student_id` → 학번입니다. Keycloak SPI 연동 시 채워지며, 없으면 빈 문자열로 저장됩니다.

---

## SuperAdmin 처리

앱 기동 시 `SuperAdminInitializer`가 `SUPER_ADMIN_USER_ID` 기준으로 `user_detail`에 keycloakUserId, keystoneUsername, keystonePassword(AES-256 암호화)를 함께 저장합니다. `user_auth_detail`도 함께 생성하며(department="관리자", studentId="ADMIN"), 멱등성을 보장하여 재기동 시 중복 생성되지 않습니다. keycloakUserId가 미설정된 기존 계정이면 선택적으로 업데이트합니다.

로그인 시 `isLinkedAdmin(keycloakUserId)`가 true이면 ajou_status/ajou_major 클레임 유무와 무관하게 학적 검증을 건너뜁니다. Branch 1에서 사전 저장된 Keystone 자격증명으로 바로 처리됩니다.

---

## Redis 세션 구조

세션 키는 `session:{UUID}` 형태이며 TTL은 30분 슬라이딩 방식입니다. 매 요청마다 `SessionAuthenticationFilter`가 TTL을 연장합니다.

- `keycloakTokens` → accessToken, refreshToken, idToken, expiresAt
- `keystoneTokens` → unscopedToken, expiresAt
- `keycloakUserId` → Keycloak sub claim 값
- `keystoneUserId` → Keystone user UUID (OpenStack API 호출에 사용)
- `userInfo` → name, email

accessToken 만료 시 refreshToken으로 Keycloak 갱신을 1회 시도합니다. refreshToken까지 만료되면 Redis 세션을 삭제하고 401을 반환합니다.

---

## 관련 파일

- `local/controller/KeycloakAuthController.java` — login/callback/logout 엔드포인트
- `local/service/modules/keycloak/KeycloakAuthModule.java` — processCallback() 오케스트레이션
- `local/service/modules/keycloak/KeycloakUserModule.java` — 3-way 분기 처리
- `global/security/keycloak/KeycloakIdTokenParser.java` — ID Token 파싱
- `global/security/session/SessionAuthenticationFilter.java` — 세션 인증 필터
- `global/init/SuperAdminInitializer.java` — 관리자 계정 초기화
- `global/security/crypto/KeystonePasswordEncryptor.java` — AES-256 패스워드 암호화
