# ACC Server Application

이 프로젝트는 **Spring Boot**와 **MariaDB** 기반의 백엔드 서버입니다.  
로컬 환경에서도 Docker를 활용하여 누구나 동일한 설정으로 실행할 수 있습니다.

---

## 🛠️ 사용 기술 스택

| 항목            | 기술 / 버전                            |
|----------------|----------------------------------------|
| Language       | Java 21                                |
| Framework      | Spring Boot 3.3.2                      |
| ORM            | Spring Data JPA                        |
| Database       | MariaDB 11.1                           |
| Build Tool     | Gradle                                 |
| Deployment     | Docker + Docker Compose                |

---

## 📂 프로젝트 구조 요약
``` 
server 
  ├── src/
  ├── .env                         # 환경 변수 (DB 및 Spring 프로필 설정)
  ├── Dockerfile                   # Spring Boot 앱을 위한 Docker 설정
  ├── docker-compose.yml           # MariaDB + 앱 통합 실행 설정
  ├── build.gradle
  ├── src/main/resources/
  │   ├── application.yml          # Spring 공통 설정
  │   └── application-db.yml       # DB 연결용 Spring 프로필 설정

```

---

## ⚙️ 실행 전 준비 사항

1. **Java 21 설치**
2. **Docker 설치**
3. (선택) 로컬에서 `.env` 구성

---

## ⚡️ 실행 방법

### 1. `.env` 파일 설정

```dotenv
# MariaDB
MARIADB_ROOT_PASSWORD=rootpw
MARIADB_DATABASE=accdb
MARIADB_USER=alodaacc
MARIADB_PASSWORD=alodaacc123@

# Spring
SPRING_PROFILES_ACTIVE=db
SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/accdb
SPRING_DATASOURCE_USERNAME=alodaacc
SPRING_DATASOURCE_PASSWORD=alodaacc123@
```

## 2. 전체 서비스 실행
```
docker compose up --build
```

