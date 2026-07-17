# ReFit 배포 구조

백엔드 `main` 브랜치에 변경이 병합되면 GitHub Actions가 Spring 백엔드와 FastAPI 분석 서버를 함께 빌드하여 기존 Lightsail 서버에 배포합니다.

## 공개 경로

- Spring API: `http://<LIGHTSAIL_HOST>/api/v1/...`
- Swagger: `http://<LIGHTSAIL_HOST>/swagger`
- AI 상태 확인: `http://<LIGHTSAIL_HOST>/ai/health`
- AI 분석: `POST http://<LIGHTSAIL_HOST>/ai/api/v1/analyze_session`

Nginx는 `/ai/` 접두사를 제거한 뒤 `refit-ai` 컨테이너의 8000번 포트로 전달합니다.

## 자동 배포 순서

1. 백엔드와 `refit-ai-server/main`을 체크아웃합니다.
2. AI 서버 테스트를 실행합니다.
3. 백엔드과 AI 서버 Docker 이미지를 GHCR에 커밋 SHA 태그로 올립니다.
4. Lightsail에 Compose와 Nginx 설정을 업로드합니다.
5. 새 이미지를 내려받아 컨테이너를 재기동합니다.
6. AI 컨테이너 자체 healthcheck가 `healthy`가 될 때까지 기다립니다.
7. Nginx 설정 문법을 검사하고 새 라우팅으로 재시작합니다.
8. Lightsail 내부에서 Spring OpenAPI와 AI 분석 API에 실제 요청을 보냅니다.
9. GitHub runner에서 공인 주소로 같은 요청을 다시 보내 외부 연결을 검증합니다.

배포 중 오류가 발생하면 Compose 상태와 AI/Nginx 최근 로그가 Action 로그에 자동으로 출력됩니다. Nginx는 Docker DNS를 주기적으로 다시 조회하므로 컨테이너 IP가 바뀌어도 기존 upstream 주소를 계속 사용하지 않습니다.

## 다음 단계

현재 1단계는 HTTP 배포입니다. GitHub Pages에서 로그인과 AI 분석을 안전하게 사용하려면 다음 배포 단계에서 HTTPS를 적용하고 React 운영 API 주소를 설정해야 합니다.
