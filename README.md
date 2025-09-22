# 🌱 SpringWebFlux

**Spring Boot 3.x · Java 17** 기반의 **리액티브 프로그래밍(WebFlux) 학습 프로젝트**입니다.  
공지사항(Notice) CRUD를 Reactive Repository 기반으로 구현했으며,  
RDBMS와 Reactive Stream을 활용한 비동기/논블로킹 웹 애플리케이션 예제입니다.  

<p align="left">
  <img alt="java" src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white">
  <img alt="spring-boot" src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white">
  <img alt="webflux" src="https://img.shields.io/badge/Spring-WebFlux-00BFAE?logo=spring&logoColor=white">
  <img alt="reactive" src="https://img.shields.io/badge/Reactive-Programming-purple">
  <img alt="build" src="https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle&logoColor=white">
</p>

---

## ✨ 주요 기능
- **공지사항(Notice)**
  - 등록, 수정, 삭제, 조회
  - ReactiveCrudRepository 기반 비동기 DB 처리
- **Reactive API**
  - Controller → Service → Repository 전 구간 WebFlux 적용
- **DTO 기반 응답**
  - `NoticeDTO`, `MsgDTO` 로 API 응답 표준화
- **유틸리티**
  - `CmmUtil`, `DateUtil` 제공

---

## 🧱 기술 스택
- **Spring Boot 3.x**
- **Spring WebFlux**
- **ReactiveCrudRepository**
- **R2DBC (DB Reactive 연동)**
- **Gradle**
- **Lombok**

---

## 📁 프로젝트 구조(요약)
```
SpringWebFlux/
├─ src/main/java/kopo/poly/
│  ├─ controller/     # NoticeController (REST API)
│  ├─ domain/         # Notice 엔티티
│  ├─ dto/            # NoticeDTO, MsgDTO
│  ├─ repository/     # NoticeRepository (ReactiveCrudRepository)
│  ├─ service/        # INoticeService (+ impl)
│  └─ util/           # CmmUtil, DateUtil
├─ src/main/resources/
│  ├─ application.yaml
│  ├─ logback.xml
│  ├─ static/
│  └─ templates/
├─ sql/               # DB 초기화 스크립트 (db.sql, notice.sql, user_info.sql)
├─ build.gradle, settings.gradle
```

---

## ⚙️ 빠른 시작
### 1) 필수 요건
- **JDK 17**
- **Gradle 8.x+**
- **MariaDB 10.x+** (R2DBC 드라이버 필요)

### 2) DB 준비
```sql
CREATE DATABASE webfluxDB DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'poly'@'%' IDENTIFIED BY '강력한비밀번호';
GRANT ALL PRIVILEGES ON webfluxDB.* TO 'poly'@'%';
FLUSH PRIVILEGES;
```

### 3) 환경설정
`src/main/resources/application.yaml` 예시:
```yaml
spring:
  r2dbc:
    url: r2dbc:mariadb://localhost:3306/webfluxDB
    username: poly
    password: ********
  sql:
    init:
      mode: always

logging:
  level:
    org.springframework.r2dbc: DEBUG
```

### 4) 실행
```bash
./gradlew bootRun
```

---

## 🧪 빠른 점검(Quick Test)
### 공지 등록
```bash
curl -X POST http://localhost:8080/notice/reg   -H "Content-Type: application/json"   -d '{"title":"공지사항","contents":"내용입니다"}'
```

### 공지 목록 조회
```bash
curl http://localhost:8080/notice/list
```

### 공지 상세 조회
```bash
curl http://localhost:8080/notice/{noticeId}
```

### 공지 삭제
```bash
curl -X DELETE http://localhost:8080/notice/{noticeId}
```

---

## 📜 라이선스
- 본 저장소는 **Apache-2.0** 라이선스를 따릅니다.  

---

## 🙋‍♀️ 문의
- **한국폴리텍대학 서울강서캠퍼스 빅데이터소프트웨어과**  
- **이협건 교수** · hglee67@kopo.ac.kr  
- 입학 상담 오픈채팅방: <https://open.kakao.com/o/gEd0JIad>
