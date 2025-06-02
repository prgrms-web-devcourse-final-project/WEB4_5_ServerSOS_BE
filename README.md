# WEB4_5_Team02_ServerSOS
프로그래머스 4기 5회차 서버야버텨줘 팀 최종프로젝트 PickGo

---
# 팀원 소개
| 김아성 | 김진명 | 전기범 | 이승민 | 조현우 |
|--------|--------|--------|--------|--------|
| <img src="https://avatars.githubusercontent.com/u/130833844?v=4" alt="김아성" width="150"> | <img src="https://avatars.githubusercontent.com/u/91389299?v=4" alt="김진명" width="150"> | <img src="https://avatars.githubusercontent.com/u/37178265?v=4" alt="전기범" width="150"> | <img src="https://avatars.githubusercontent.com/u/101695929?v=4" alt="이승민" width="150"> | <img src="https://avatars.githubusercontent.com/u/151692425?v=4" alt="조현우" width="150"> |
| [asungkim](https://github.com/asungkim) | [jin214930](https://github.com/jin214930) | [JunKiBeom](https://github.com/JunKiBeom) | [min429](https://github.com/min429) | [chohyunwoo](https://github.com/chohyunwoo) |
| - CI/CD 담당<br> - 예약 구현 및 결제 개선<br> - 로그 구현<br> - 모니터링 구현 | - KOPIS API 연동<br> - 공연 데이터 처리<br> - 좌석 구조 구현 | - 결제 구현<br> - 토스페이먼츠 API 연동<br> - 리뷰 기능 개선<br> - 성능 및 부하 테스트<br> - 로그 처리 개선 | - 회원 구현(JWT, OAuth2)<br> - Redis 기반 대기열 구현 | - 좌석 실시간 상태 구현<br> - 관리자 CRUD 구현<br> - 리뷰 구현<br> - 모니터링 구현 |

# PickGO - 공연 티켓 예매 웹사이트
https://web4-5-serversos-be.pages.dev
> 연극, 뮤지컬, 콘서트, 무용 등 다양한 공연을 위한 **기술 중심 예매 시스템**

---

## 프로젝트 개요

### 주제

**PickGO**는 공연 티켓을 실시간으로 예매할 수 있는 웹 기반 플랫폼입니다.  
실 서비스 운영보다는 다음과 같은 **기술적 도전**에 초점을 맞춘 프로젝트입니다:

- 대기열 시스템 설계 및 구현
- 좌석 정합성 보장
- 예매 및 결제 흐름의 원자성 확보

---

### 주제 선정 배경

#### 1. 사용자 경험 기반 문제 인식
- 좌석 선택 화면에서 확대 불가
- 실시간 좌석 상태 반영 지연

#### 2. 기술적 도전 요소
- **대용량 트래픽 대응**
  - 대기열 구조 설계
  - 서버 분리 및 확장 고려
- **도메인 정합성 유지**
  - 좌석 선택 및 상태 일관성 보장
  - 예매 ~ 결제 흐름의 원자성 확보
- **성능 및 부하 테스트**
  - 테스트 도구 활용 및 병목 지점 리팩토링
- **모니터링 체계 구축**
  - 서버 및 JVM 모니터링
  - 비즈니스 지표 시각화

---

## 기술 스택

| 영역          | 기술 구성                                                                   |
|--------------|--------------------------------------------------------------------------|
| **Backend**  | Java, Spring Boot, OAuth2                                                |
| **Database** | MySQL, Redis (Stream, Sorted Set)                                        |
| **Monitoring** | Grafana, Prometheus, Node Exporter, Spring Actuator, InfluxDB          |
| **DevOps**   | Docker, GitHub Actions, AWS, Cloudflare Pages                            |
| **Frontend** | React, TypeScript, Vite, TailwindCSS, Thymeleaf                          |
| **Web Server** | Nginx (Reverse Proxy)                                                  |
| **Testing** | Gradle, JUnit5, K6                                                        |
| **Communication** | Notion, Slack, Discord, figma, Swagger                              |
<br>
<div>
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/spring data JPA-6DB33F?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/OAuth2.0-000000?style=for-the-badge&logo=OAuth2.0&logoColor=white">
<br>
<img src="https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">
 <br>
<img src="https://img.shields.io/badge/grafana-%23F46800.svg?style=for-the-badge&logo=grafana&logoColor=white">
<img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white">
<img src="https://img.shields.io/badge/Node Exporter-E6522C?style=for-the-badge&logo=Node Exporter&logoColor=white">
<img src="https://img.shields.io/badge/Spring Actuator-6DB33F?style=for-the-badge&logo=Spring Actuator&logoColor=white">
<img src="https://img.shields.io/badge/InfluxDB-22ADF6?style=for-the-badge&logo=InfluxDB&logoColor=white">
<br>
<img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white">
<img src="https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white">
<img src="https://img.shields.io/badge/cloudflarepages-F38020?style=for-the-badge&logo=cloudflarepages&logoColor=white">
<br>
<img src="https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB">
<img src="https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white">
<img src="https://img.shields.io/badge/vite-%23646CFF.svg?style=for-the-badge&logo=vite&logoColor=white">
<img src="https://img.shields.io/badge/tailwindcss-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white">
<img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">
<br>
<img src="https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white">
<img src="https://img.shields.io/badge/nginxproxymanager-F15833?style=for-the-badge&logo=nginxproxymanager&logoColor=white">
<br>
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white">
<img src="https://img.shields.io/badge/k6-7D64FF?style=for-the-badge&logo=k6&logoColor=white">
<br>
<img src="https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white">
<img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white">
<img src="https://img.shields.io/badge/Discord-%235865F2.svg?style=for-the-badge&logo=discord&logoColor=white">
<img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">
<img src="https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white">
</div>

---

## ERD
![ERD_server_sos](https://github.com/user-attachments/assets/974bddbc-2775-4e06-b65d-72f768fd1e4c)


## 시스템 아키텍처
![image](https://github.com/user-attachments/assets/810f75a4-2436-4895-95e2-a733b4b60c70)


### 전체 흐름 요약

1. 사용자가 예매 페이지 접속
2. **Nginx Reverse Proxy** → **Spring 서버 API 요청**
3. 서버는 **MySQL, Redis** 기반으로 데이터 처리 및 응답
4. **Redis Stream + SSE** → 실시간 좌석 상태 전파
5. **대기열 입장**은 스케줄러 기반 처리, 사용자별 상태 전송
6. **Prometheus**가 서버 및 JVM 상태 수집
7. **Grafana** 대시보드에서 실시간 확인 (iframe 제공)
8. **GitHub Actions** 자동 배포 → 각 EC2 인스턴스

---

## 서버 구성

| 구분               | 주요 구성 요소                                                             |
|--------------------|---------------------------------------------------------------------------|
| **EC2-1 (비즈니스 서버)** | Spring App, MySQL, Redis, Nginx                                           |
| **EC2-2 (메트릭 서버)**   | Grafana, Prometheus, Node Exporter, InfluxDB, Nginx                       |
|                      |  → EC2-1의 상태 수집 및 메트릭 시각화                                      |

---

## 주요 기능 요약

- 실시간 좌석 상태 반영 (SSE + Redis Stream)
- 사용자 대기열 처리 로직
- 서버 상태/지표 실시간 모니터링
- GitHub Actions 기반 자동 배포 파이프라인
- RESTful API 설계 + OAuth2 인증

---

## 핵심 기능 및 주요 구현

### 1. 대기열 시스템 (Queue System)

[📘 상세 위키 보기](https://github.com/prgrms-web-devcourse-final-project/WEB4_5_ServerSOS_BE/wiki/%EB%8C%80%EA%B8%B0%EC%97%B4-%EA%B5%AC%ED%98%84)

![Queue Overview](https://github.com/user-attachments/assets/0975af57-8f3a-40db-93bd-3c1b376543a7)
![Queue State Flow](https://github.com/user-attachments/assets/8cbc3a20-416d-4a20-91f4-bfc27ac6b4e6)


- **Redis Sorted Set**: 사용자 대기 순서 저장
- **Redis Stream**: 대기 상태 메시지 발행
- **SSE (Server-Sent Events)**: 클라이언트에게 실시간 대기 상태 전송
- **스케줄러 기반 입장 처리**: TPS 기반 속도 제어 및 자동 입장 처리

---

### 2. 실시간 좌석 상태 반영

- **다중 사용자 동시 접속 시 좌석 현황 동기화**
- **SSE 구독**을 통한 실시간 선택/해제 이벤트 전파

---

### 3. 좌석 선택 정합성 보장

- **좌석 테이블에 UNIQUE 제약조건 부여**
  - 동시 선택 충돌 방지
- **낙관적 락 적용**
  - 최종 선택자만 예매 확정 가능
  - 예매 경쟁 상황에서도 데이터 정합성 유지

---

### 4. 예매 및 결제 흐름

- **TossPayments API 연동**을 통한 실 결제 흐름 구현
- **예매/결제 만료 스케줄러**
  - 예매 후 일정 시간 무응답 시 자동 만료 처리
- **예매 → 결제의 트랜잭션 흐름 보장**
  - 원자성 있는 예매 처리 로직 구현

---

### 5. 운영자용 관리자 대시보드

- **운영 현황 시각화**
  - 공연 목록, 예매 상태, 사용자 로그
- **Grafana + MySQL 기반 대시보드**
  - 주요 지표 실시간 시각화
  - iframe 방식으로 관리자 페이지에 통합

---
## API 명세
[Swagger 문서 바로가기](https://api.team2.pick-go.shop/swagger-ui/index.html)

[API 명세 Wiki 바로가기](https://github.com/prgrms-web-devcourse-final-project/WEB4_5_ServerSOS_BE/wiki/API-명세)

## 협업 규칙

### Git 브랜치 규칙

- **GitHub Flow 방식**
    1. **Branch 생성**
        - 기능 개발 또는 버그 수정 시 새 브랜치 생성
    	- 브랜치 네이밍: `feature/이슈번호`, `fix/이슈번호`
    2. **Commit 작성**
        - 브랜치에서 변경 사항을 커밋으로 저장
        
        > Commit 메시지 컨벤션
        > 
        > 1. `feat`: 새로운 기능 추가
        > 2. `fix`: 버그 수정
        > 3. `docs`: 문서 수정 (README, Wiki 등)
        > 4. `style`: 코드 포맷팅, 세미콜론 누락 등 (기능 변경 없음)
        > 5. `refactor`: 코드 리팩토링
        > 6. `test`: 테스트 코드 추가 또는 기존 테스트 수정
        > 7. `chore`: 기타 변경 사항
        > 8. `comment`: 주석 추가 및 변경
        > 9. `!HOTFIX`: 배포 관련 긴급 수정
    3. **Pull Request(PR) 생성**
        - 변경 사항을 `develop` 브랜치로 병합하기 위해 PR을 생성
        > PR 규칙
        > 
        > 1. Merge 전 2명 이상 승인 필요
        > 2. PR 설명에 구현한 기능 간단히 작성
        > 3. PR 전 rebase 확인 필수
    4. **리뷰 및 피드백 반영**
        - 리뷰어 요청 시 적극적으로 반영
        - 코드 리뷰 완료 후 `squash merge`
    5. develop 병합 (Merge)
        - PR이 승인되면 변경 사항을 `develop` 브랜치로 병합.
        - 배포 시점에 `main` 브랜치로 병합

### 자바 컨벤션
[컨벤션 Wiki 바로가기](https://github.com/prgrms-web-devcourse-final-project/WEB4_5_ServerSOS_BE/wiki/Code-Convention)

### 발표 자료
[Canva](https://www.canva.com/design/DAGnMYYVAdM/kjcLF6v-9U2ETx7J82fTYg/view?utm_content=DAGnMYYVAdM&utm_campaign=designshare&utm_medium=link&utm_source=viewer)

### 시연 영상
[Youtube](https://youtu.be/vcHmAY6IW9I)
