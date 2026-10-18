# SpringWebFlux

Spring Boot **4.1.1**, Java **17**, Spring WebFlux, Spring Data R2DBC, MariaDB, Thymeleaf 기반의 **리액티브 게시판 실습**입니다.

## 학습 내용

- WebFlux: `Mono`, `Flux`, 비동기 HTTP 요청 처리
- ReactiveCrudRepository: 논블로킹 공지사항 조회 및 등록
- R2DBC: MariaDB 리액티브 데이터 접근
- Thymeleaf: 서버 렌더링 화면과 Ajax 기반 CRUD
- 데이터 검증: 필수 값, UTF-8 바이트 제한, 잘못된 ID(400), 없는 게시글(404)
- 테스트: Mockito + Reactor `StepVerifier`로 DB 없이 서비스와 입력 처리 테스트

> **주의: 본 저장소는 인증·인가 기능이 없는 공개 CRUD 교육용 예제입니다.** 모든 사용자가 동일한 데모 사용자(`USER01`)로 게시글을 등록·수정·삭제할 수 있습니다. 운영 환경에 공개 배포하지 마세요. 운영 환경에서는 Spring Security 기반 로그인, 서버 측 작성자 권한 검사, CSRF 대응 등을 별도로 구현해야 합니다.

## 요구 사항

- JDK 17 이상 (실습 기본: 17)
- Gradle Wrapper 8.14.3 (`./gradlew` 사용)
- MariaDB 서버 (R2DBC 드라이버는 Gradle이 설치)
- 실행 시 데이터베이스 접속 계정과 비밀번호

## 데이터베이스 준비

MariaDB에서 데이터베이스를 만들고 `sql/notice.sql`의 테이블 생성 SQL을 실행합니다.

```sql
CREATE DATABASE IF NOT EXISTS webfluxDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE webfluxDB;
-- 이후 sql/notice.sql 실행
```

연결 계정 `poly`에게 `webfluxDB`에 대한 권한을 부여하거나, 이미 권한이 있는 계정을 이용합니다. 비밀번호를 Git 저장소에 입력하지 마세요.

## 환경 변수

기본 설정(`src/main/resources/application.yaml`):

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `DB_R2DBC_URL` | `r2dbc:mariadb://localhost:3306/webfluxDB` | MariaDB R2DBC URL |
| `DB_USERNAME` | `poly` | DB 사용자 |
| `DB_PASSWORD` | (빈 값) | DB 비밀번호: 실행 전 설정 권장 |
| `SERVER_PORT` | `11000` | 웹 서버 포트 |
| `DEMO_USER_ID` | `USER01` | 모든 방문자가 공유하는 교육용 사용자 ID |

Linux/macOS 예시:

```bash
export DB_USERNAME=poly
export DB_PASSWORD='실제_DB_비밀번호'
./gradlew bootRun
```

Windows PowerShell 예시:

```powershell
$env:DB_USERNAME = 'poly'
$env:DB_PASSWORD = '실제_DB_비밀번호'
.\gradlew.bat bootRun
```

실행 후 `http://localhost:11000/notice/noticeList`에 접속합니다.

## 화면과 API (실제 코드 기준)

| 메서드 | URL | 설명 |
| --- | --- | --- |
| GET | `/notice/noticeList` | 목록 |
| GET | `/notice/noticeReg` | 등록 화면 |
| POST | `/notice/noticeInsert` | 등록 (`title`, `contents`, `noticeYn`) |
| GET | `/notice/noticeInfo?nSeq=1` | 상세 및 조회수 증가 |
| GET | `/notice/noticeEditInfo?nSeq=1` | 수정 화면, 조회수는 유지 |
| POST | `/notice/noticeUpdate` | 수정 (`nSeq`, `title`, `contents`, `noticeYn`) |
| POST | `/notice/noticeDelete` | 삭제 (`nSeq`) |

`noticeYn` 값은 `Y` 또는 `N`입니다. 등록/수정/삭제에 성공하면 `{"result":1,"msg":"..."}` 형태의 JSON을 반환합니다. 유효하지 않은 입력은 HTTP 400, 존재하지 않는 게시글은 HTTP 404를 반환합니다.

등록 예시:

```bash
curl -i -X POST http://localhost:11000/notice/noticeInsert \
  -d 'title=Hello' -d 'contents=WebFlux' -d 'noticeYn=N'
```

삭제 예시:

```bash
curl -i -X POST http://localhost:11000/notice/noticeDelete -d 'nSeq=1'
```

## 테스트와 점검

```bash
./gradlew --no-daemon clean test
./gradlew --no-daemon bootJar
```

`.github/workflows/gradle.yml`에서도 GitHub Actions를 통해 Java 17 테스트를 실행하도록 구성했습니다. 단위 테스트는 데이터베이스 없이 실행됩니다. **실제 MariaDB와 연계한 통합 테스트는 별도로 수행해야 합니다.**

## 주의사항

- `NoticeController`는 교육용 데모 사용자만 사용하며 로그인/세션을 인증으로 취급하지 않습니다.
- Repository의 조회수 갱신과 삭제는 `@Modifying`을 사용하여 영향받은 행 수를 확인합니다.
- 공지사항 수정 시 최초 등록자/등록일과 조회수를 유지합니다.
- 클라이언트와 서버에서 동일하게 UTF-8 바이트 기준(제목 200B, 내용 4000B)으로 제한합니다.
- MariaDB 스키마의 `notice_seq`는 `AUTO_INCREMENT`이므로 새 게시글 저장 시 ID를 설정하지 않습니다.
