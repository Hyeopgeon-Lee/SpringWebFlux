# 🌱 Spring Boot Frameworks 3.x + WebFlux + r2dbc 실습

> **Java 17 기반 WebFlux 실습 코드**  
> Spring 3.x부터 Java 8 지원이 불가하므로, 본 실습은 Java 17 기반으로 작성되었습니다.  
> WebFlux를 활용한 비동기 프로그래밍과 MariaDB의 r2dbc를 적용하여 공지사항 게시판 서비스를 구현하였습니다.  
> 공유되는 프로그래밍 코드는 한국폴리텍대학 서울강서캠퍼스 빅데이터과 수업에서 사용된 코드입니다.

---

### 📚 **작성자**
- **한국폴리텍대학 서울강서캠퍼스 빅데이터과**  
- **이협건 교수**  
- ✉️ [hglee67@kopo.ac.kr](mailto:hglee67@kopo.ac.kr)  
- 🔗 [빅데이터학과 입학 상담 오픈채팅방](https://open.kakao.com/o/gEd0JIad)

---

## 🚀 주요 실습 내용

1. **리액티브 프로그래밍 이해**
2. **Mono와 Flux 이해 및 실습**
3. **map, flatMap, flatMapSequential 함수 이해**
4. **WebFlux를 활용한 MariaDB 기반 r2dbc 공지사항 게시판 서비스 구현**

---

## 🛠️ 주요 적용 프레임워크

- **Spring Boot Frameworks**
- **WebFlux**
- **r2dbc**

---

## 📩 문의 및 입학 상담

- 📧 **이메일**: [hglee67@kopo.ac.kr](mailto:hglee67@kopo.ac.kr)  
- 💬 **입학 상담 오픈채팅방**: [바로가기](https://open.kakao.com/o/gEd0JIad)

---

## 💡 **우리 학과 소개**
- 한국폴리텍대학 서울강서캠퍼스 빅데이터과는 **클라우드 컴퓨팅, 인공지능, 빅데이터 기술**을 활용하여 소프트웨어 개발자를 양성하는 학과입니다.  
- 학과에 대한 더 자세한 정보는 [학과 홈페이지](https://www.kopo.ac.kr/kangseo/content.do?menu=1547)를 참고하세요.

---

## 📦 **설치 및 실행 방법**

### 1. 레포지토리 클론
- 아래 명령어를 실행하여 레포지토리를 클론합니다.

```bash
git clone https://github.com/Hyeopgeon-Lee/SpringWebFlux.git
cd SpringWebFlux
```

### 2. MariaDB r2dbc 설정
- application.yml 또는 application.properties 파일에서 MariaDB r2dbc 설정 정보를 업데이트합니다.

```yaml
spring:
  r2dbc:
    url: r2dbc:mariadb://localhost:3306/your_database
    username: your_username
    password: your_password
```

### 3. 의존성 설치 및 빌드
- Gradle을 사용하여 의존성을 설치하고 빌드합니다.

```bash
./gradlew build
```

### 4. 애플리케이션 실행
- 아래 명령어를 실행하여 애플리케이션을 시작합니다.

```bash
./gradlew bootRun
```




  
