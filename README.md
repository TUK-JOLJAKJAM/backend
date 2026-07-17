# ReFit Spring Backend

인증, 사용자 재활 프로필, Unity 게임 기록과 규칙 기반 분석 결과를 MySQL에 저장하는 Spring Boot 서버입니다.

## 분석 데이터 흐름

1. Unity가 `POST /api/v1/game-histories`로 계약 v2 게임·센서 데이터를 저장합니다.
2. React가 인증 토큰과 함께 `POST /api/v1/game-histories/{historyId}/analyses`를 호출합니다.
3. Spring이 기록 소유권을 확인하고 게임 상세와 사용자 프로필을 AI FastAPI에 전달합니다.
4. 전체 분석 JSON, 규칙 버전, 입력 SHA-256, 데이터 품질과 분석 시각을 `analysis_result`에 저장합니다.
5. React는 동일 응답을 표시하고 latest/list API로 저장 여부와 재분석 이력을 확인합니다.

기존 게임 기록 API는 유지하며 `schemaVersion`은 선택 필드입니다. 구버전 Unity의 `yyyyMMddHHmmssff` 시각도 저장 전에 epoch milliseconds로 정규화합니다.

## 분석 API

- `POST /api/v1/game-histories/{historyId}/analyses`: 새 분석 실행 및 DB 저장
- `GET /api/v1/game-histories/{historyId}/analyses/latest`: 최신 전체 분석 결과
- `GET /api/v1/game-histories/{historyId}/analyses?size=20`: 버전별 분석 요약 이력
- `GET /api/v1/analyses?size=20`: 여러 게임 기록을 합친 내 회복 추세

모든 분석 API에는 `Authorization: Bearer <accessToken>`이 필요하며 자기 게임 기록만 조회할 수 있습니다.

## 설정

- `AI_BASE_URL`: 내부 AI 서버 주소. 운영 Compose 기본값은 배포 스모크 테스트와
  동일한 Caddy 경로 `http://proxy/ai`이며, Caddy가 이를 Uvicorn `ai:8000`으로 전달합니다.
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: MySQL 연결
- `REDIS_HOST`, `REDIS_PORT`: 토큰 저장 Redis
- `JWT_SECRET`: JWT 서명 키

## 테스트

Windows:

```powershell
.\gradlew.bat clean test
```

macOS/Linux:

```bash
bash ./gradlew clean test
```

PR과 main 배포 워크플로에서 Spring, AI, React 테스트와 프록시 연결 검증을 순서대로 실행합니다.
