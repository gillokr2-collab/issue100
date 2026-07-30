# 이슈100

여러 뉴스 기사를 사건 단위로 묶어 대한민국의 실시간 관심 이슈와 사이트 내부 인기 검색어를 보여주는 모바일 우선 뉴스 분석·큐레이션 MVP입니다.

## 구조

```text
issue100/
├── frontend/        Next.js 15, TypeScript, Tailwind CSS
├── backend/         Java 21, Spring Boot 3.5, Flyway, OpenAPI
├── infrastructure/  프론트·백엔드 Dockerfile
├── docs/            아키텍처 문서
├── docker-compose.yml
└── .env.example
```

## 빠른 실행

Java 21, Node.js 22 이상, Docker Desktop이 필요합니다.

```bash
copy .env.example .env
docker compose up --build
```

- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8080/api/v1/issues/rankings
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

## 개별 실행

```bash
docker compose up -d postgres redis
gradle :backend:bootRun
cd frontend
npm ci
npm run dev
```

Gradle이 없다면 Gradle 8.14 이상을 설치하거나 Docker Compose로 실행합니다.

## 검증

```bash
gradle :backend:test :backend:build
cd frontend
npm run typecheck
npm run lint
npm run build
docker compose config
```

## Mock 데이터

Flyway `V2__mock_seed.sql`은 12개 언론사, 15개 이슈, 60개 기사, 220개 검색 이벤트, 520개 조회·클릭 이벤트와 타임라인을 생성합니다. 애플리케이션의 Mock Provider도 데이터베이스 연결 전 API 개발을 위한 동일 규모의 데이터를 제공합니다.

## 개인정보·저작권 원칙

- 익명 visitorId에는 개인정보를 넣지 않습니다.
- IP 원문은 장기 저장하지 않습니다.
- 전화번호와 이메일 형식 검색어를 제외합니다.
- 실제 기사 본문 전체를 복사하지 않습니다.
- 공식 API, RSS, 공개자료 또는 Mock 데이터만 사용합니다.
- 외부 언론사의 비공개 조회수를 수집하지 않습니다.

## 아직 운영화가 필요한 부분

현재 중복 방지는 단일 인스턴스 메모리 구현입니다. 운영 배포 전 Redis TTL 기반 원자적 중복 방지, 실제 News Provider, 관리자 화면, AI Provider, 봇 탐지 및 감사 로그를 추가해야 합니다.
