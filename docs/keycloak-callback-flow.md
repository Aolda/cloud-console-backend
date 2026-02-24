# Keycloak OIDC 콜백 처리 흐름

> **대상 독자**: ACC 백엔드 개발자
> **관련 브랜치**: `Refactor/#129/jwt-to-session`
> **핵심 클래스**: `KeycloakAuthModule`, `KeycloakUserModule`, `KeycloakAuthController`

---

## 전체 흐름 개요

```
[사용자 브라우저]
      │
      │  GET /api/v1/auth/keycloak/login
      ▼
[KeycloakAuthController.login()]
      │  buildAuthorizationUrl(state)
      │  → KeycloakProperties: issuerUri, clientId, redirectUri 조합
      │
      │  302 Redirect
      ▼
[Keycloak 로그인 페이지]
      │  사용자 인증 완료
      │
      │  302 Redirect (code, state 포함)
      ▼
[KeycloakAuthController.callback()]
      │  processCallback(code)
      │
      ▼
[KeycloakAuthModule.processCallback()]  ← 핵심 오케스트레이션
      │
      ├─ 1. code → Keycloak Token 교환 (access/refresh/id token)
      ├─ 2. ID Token 파싱 → KeycloakIdTokenClaims 추출
      ├─ 3. 사용자 조회/등록 3-way 분기  ← KeycloakUserModule
      ├─ 4. Keystone unscoped token 발급
      ├─ 5. SessionData 구성 → Redis 저장
      └─ 6. sessionId 반환
      │
      │  Set-Cookie: acc-session-id={sessionId}
      │  302 Redirect
      ▼
[프론트엔드 콜백 URL]
```

---

## Step 3 상세: 사용자 조회/등록 3-way 분기

> `KeycloakUserModule.findOrRegisterKeycloakUser(KeycloakIdTokenClaims claims)`

---

### Branch 1: 기존 Keycloak 연결 사용자 (일반 로그인)

**조건**: `user_detail.keycloak_user_id = claims.subject()` 인 레코드가 존재

```
DB: user_detail
  keycloak_user_id = "kc-sub-abc123"   ← 이미 연결됨
  keystone_username = "jungmin"
  keystone_password = "AES-256-암호화된값"
```

**처리**:
1. `UserRepositoryPort.findUserDetailByKeycloakUserId(subject)` 조회
2. `KeystonePasswordEncryptor.decrypt(entity.keystonePassword)` → 평문 패스워드 복호화
3. `KeycloakUserResult(keystoneUserId, keystoneUsername, plainPassword, userName)` 반환

**특징**: 외부 API 호출 없음. DB 조회 + 복호화만으로 처리.

---

### Branch 2: 이메일 일치 기존 사용자 (Account Linking)

**조건**: `keycloak_user_id`는 없지만 `user_auth_detail.user_email = claims.email()` 인 레코드 존재

**발생 시점**: 기존에 ADMIN 또는 GOOGLE 방식으로 가입된 사용자가 Keycloak으로 **최초** 로그인할 때

```
DB: user_auth_detail
  user_email = "jungmin@ajou.ac.kr"   ← 이메일 일치
  auth_type  = 1 (ADMIN) 또는 2 (GOOGLE)

DB: user_detail
  keycloak_user_id = NULL             ← 아직 연결 안 됨
```

**처리**:
1. `UserRepositoryPort.findUserIdentityByEmail(email)` 조회
2. `authModule.issueSystemAdminToken(null)` → admin 토큰 발급
3. `KeystoneAPIExternalPort.getUserDetail(keystoneUserId, adminToken)` → 기존 username 조회
4. **Keystone 패스워드 재설정** (`updateUser`) — 기존 패스워드를 알 수 없으므로 새 패스워드로 교체
5. `user_detail` 업데이트:
   - `keycloak_user_id` = `claims.subject()`
   - `keystone_username` = Keystone에서 가져온 username
   - `keystone_password` = AES-256 암호화된 새 패스워드
6. `KeycloakUserResult` 반환

**주의**: 이후 Keystone 직접 패스워드 로그인은 불가 (Keycloak OIDC 경로만 사용)

---

### Branch 3: 신규 사용자 등록

**조건**: `keycloak_user_id`도 없고 `email`도 없음 → 완전히 새로운 사용자

**처리**:
1. `authModule.issueSystemAdminToken(null)` → admin 토큰 발급
2. **Keystone 사용자 생성** (`createUser`)
   - `name`: 이메일 앞부분 (`@` 기준 split, Skyline의 `@` 도메인 인식 문제 회피)
   - `email`: `claims.email()`
   - `password`: UUID 기반 랜덤 패스워드
3. **`user_detail` INSERT**:
   - `user_id` = Keystone이 발급한 `userId`
   - `keycloak_user_id` = `claims.subject()`
   - `keystone_username` = Keystone 응답의 `name`
   - `keystone_password` = AES-256 암호화된 패스워드
   - `user_name` = `claims.preferredUsername()`
   - `user_phone_number` = `""` (Keycloak은 전화번호 미제공, 추후 사용자가 직접 입력)
4. **`user_auth_detail` INSERT**:
   - `auth_type` = 3 (KEYCLOAK)
   - `department` = `claims.department()`
   - `student_id` = `claims.studentId()`
   - `user_email` = `claims.email()`
5. `KeycloakUserResult` 반환

**주의**: Keystone 사용자 생성 성공 후 DB INSERT 실패 시 Keystone 고아(orphan) 사용자 발생 가능.
운영 환경에서는 보상 트랜잭션(Keystone 사용자 삭제) 추가 검토 필요.

---

## ID Token에서 추출하는 클레임 목록

> `KeycloakIdTokenParser.extractClaims(String idToken)` → `KeycloakIdTokenClaims`

| 클레임 키 | 표준 여부 | 설명 | Keycloak 설정 |
|---|---|---|---|
| `sub` | 표준 | Keycloak 사용자 고유 ID (UUID) | 자동 포함 |
| `email` | 표준 | 사용자 이메일 | 자동 포함 |
| `preferred_username` | 표준 | Keycloak 로그인 ID | 자동 포함 |
| `department` | 커스텀 | 학과명 | Client Mapper 필요 |
| `studentId` | 커스텀 | 학번 | Client Mapper 필요 |

**Keycloak Client Mapper 설정** (커스텀 클레임용):
`Clients → {client} → Client Scopes → {client}-dedicated → Add mapper`

| Mapper Name | Type | User Attribute | Token Claim Name |
|---|---|---|---|
| department | User Attribute | department | department |
| studentId | User Attribute | studentId | studentId |

---

## 세션 저장 구조

```
Redis Key: "session:{UUID}"  (TTL: 30분, 슬라이딩 방식)

Hash Fields:
  keycloakTokens  → { accessToken, refreshToken, idToken, expiresAt }
  keystoneTokens  → { unscopedToken, expiresAt }
  keycloakUserId  → "kc-sub-abc123"
  keystoneUserId  → "keystone-user-uuid"
  userInfo        → { name, email }
```

---

## 분기 결정 흐름도

```
findOrRegisterKeycloakUser(claims)
        │
        ▼
[user_detail WHERE keycloak_user_id = claims.subject()]
        │
  ┌─ 존재 ──────────────────────────────────────────────────────┐
  │ Branch 1: 기존 연결 사용자                                   │
  │   - DB에서 keystoneUsername/Password 읽기                    │
  │   - 복호화 후 KeycloakUserResult 반환                        │
  └──────────────────────────────────────────────────────────────┘
        │
  없음  ▼
[user_auth_detail WHERE user_email = claims.email()]
        │
  ┌─ 존재 ──────────────────────────────────────────────────────┐
  │ Branch 2: 이메일 일치 → Account Linking                     │
  │   - Keystone 패스워드 재설정                                 │
  │   - user_detail에 keycloak_user_id + 새 password 업데이트   │
  └──────────────────────────────────────────────────────────────┘
        │
  없음  ▼
Branch 3: 완전 신규 사용자
  - Keystone 사용자 생성
  - user_detail + user_auth_detail INSERT
```

---

## 관련 파일

| 역할 | 파일 |
|---|---|
| 엔드포인트 (login/callback/logout) | `local/controller/KeycloakAuthController.java` |
| 오케스트레이션 | `local/service/modules/keycloak/KeycloakAuthModule.java` |
| 3-way 분기 처리 | `local/service/modules/keycloak/KeycloakUserModule.java` |
| ID Token 파싱 | `global/security/keycloak/KeycloakIdTokenParser.java` |
| 세션 Redis 저장/조회 | `local/repository/adapters/RedisSessionRepositoryAdapter.java` |
| 세션 인증 필터 | `global/security/session/SessionAuthenticationFilter.java` |
| Keycloak API 호출 | `local/external/modules/keycloak/KeycloakOidcAPIModule.java` |
| 패스워드 AES 암호화 | `global/security/crypto/KeystonePasswordEncryptor.java` |
