# ReFit 배포 구조

백엔드 `main` 브랜치에 변경이 병합되면 GitHub Actions가 React 프론트엔드, Spring 백엔드와 FastAPI 분석 서버를 함께 빌드하여 기존 Lightsail 서버에 배포합니다.

## 공개 경로

- React 화면: `http://<LIGHTSAIL_HOST>/`
- Spring API: `http://<LIGHTSAIL_HOST>/api/v1/...`
- Swagger: `http://<LIGHTSAIL_HOST>/swagger`
- AI 상태 확인: `http://<LIGHTSAIL_HOST>/ai/health`
- AI 분석: `POST http://<LIGHTSAIL_HOST>/ai/api/v1/analyze_session`

Caddy는 `/`에서 React 정적 빌드를 제공하고, `/api`·Swagger 경로는 Spring으로, `/ai/`는 접두사를 제거한 뒤 FastAPI로 전달합니다. 모든 구성요소가 같은 HTTP origin을 사용하므로 GitHub Pages의 HTTPS→HTTP Mixed Content 제한을 피합니다.

현재 구성은 빠른 시연을 위한 HTTP 데모 모드이며 Lightsail IPv4 방화벽의 기존 TCP 80만 사용합니다. 통신이 암호화되지 않으므로 테스트 계정과 비식별 데이터만 사용해야 합니다.

## 자동 배포 순서

1. 백엔드, `front-end/main`, `refit-ai-server/main`을 체크아웃합니다.
2. React와 AI 전체 테스트를 실행합니다.
3. React를 HTTP 운영 주소로 빌드합니다.
4. 백엔드와 AI 서버 Docker 이미지를 GHCR에 커밋 SHA 태그로 올립니다.
5. React 빌드, Compose와 Caddy 설정을 Lightsail에 업로드합니다.
6. 새 이미지를 내려받아 컨테이너를 재기동합니다.
7. AI healthcheck와 Caddy 설정을 검증합니다.
8. Lightsail 내부와 GitHub runner에서 React HTML, Spring OpenAPI, AI health와 실제 분석 POST를 검증합니다.

PR 단계의 `verify-deployment` 워크플로도 React·AI 전체 테스트, Compose 설정, 실제 React 정적 파일, Spring mock 라우팅, AI Docker 이미지와 분석 POST를 검증합니다. 배포 중 오류가 발생하면 Compose 상태와 AI/Caddy 최근 로그가 Action 로그에 자동으로 출력됩니다.

## 다음 단계

실제 사용자와 개인정보를 다루기 전에는 TCP 443을 개방하고 HTTPS 구성으로 복귀해야 합니다.
