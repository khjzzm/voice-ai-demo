# Docker 배포 가이드

Voice AI Demo 프로젝트를 Docker로 배포하는 방법을 안내합니다.

## 📋 사전 요구사항

- Docker 설치
- Docker Compose 설치
- ElevenLabs API 키

## 🚀 빠른 시작

### 1. 환경변수 설정

```bash
# 환경변수 예시 파일을 복사
cp env.example .env

# .env 파일을 편집하여 API 키 설정
nano .env
```

`.env` 파일 내용:
```env
ELEVENLABS_API_KEY=your_actual_api_key_here
SERVER_PORT=8080
```

### 2. 자동 배포 스크립트 실행

```bash
./deploy.sh
```

## 🔧 수동 배포

### 1. Docker 이미지 빌드

```bash
docker-compose build
```

### 2. 컨테이너 시작

```bash
docker-compose up -d
```

### 3. 로그 확인

```bash
docker-compose logs -f
```

### 4. 컨테이너 상태 확인

```bash
docker-compose ps
```

## 🛠️ 관리 명령어

### 컨테이너 중지
```bash
docker-compose down
```

### 컨테이너 재시작
```bash
docker-compose restart
```

### 로그 확인
```bash
docker-compose logs -f voice-ai-demo
```

### 컨테이너 내부 접속
```bash
docker-compose exec voice-ai-demo sh
```

## 🌐 서비스 접속

배포 완료 후 다음 URL로 접속할 수 있습니다:

- **메인 페이지**: http://localhost:8080
- **헬스체크**: http://localhost:8080/api/voice/health

## 📊 모니터링

### 헬스체크
```bash
curl http://localhost:8080/api/voice/health
```

### 컨테이너 리소스 사용량
```bash
docker stats voice-ai-demo
```

## 🔍 문제 해결

### 1. 포트 충돌
포트 8080이 이미 사용 중인 경우:
```bash
# .env 파일에서 포트 변경
SERVER_PORT=8081
```

### 2. API 키 오류
```bash
# .env 파일 확인
cat .env

# 컨테이너 재시작
docker-compose restart
```

### 3. 로그 확인
```bash
# 애플리케이션 로그
docker-compose logs voice-ai-demo

# 시스템 로그
docker-compose logs
```

### 4. 컨테이너 재빌드
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

## 📁 파일 구조

```
voice-ai-demo/
├── Dockerfile              # Docker 이미지 정의
├── docker-compose.yml      # Docker Compose 설정
├── .dockerignore          # Docker 빌드 제외 파일
├── deploy.sh              # 배포 스크립트
├── env.example            # 환경변수 예시
├── .env                   # 환경변수 (사용자 생성)
└── logs/                  # 로그 디렉토리
```

## 🔒 보안 고려사항

1. **API 키 보안**: `.env` 파일을 Git에 커밋하지 마세요
2. **포트 노출**: 프로덕션에서는 방화벽 설정을 확인하세요
3. **로그 관리**: 민감한 정보가 로그에 포함되지 않도록 주의하세요

## 🚀 프로덕션 배포

프로덕션 환경에서는 다음 사항을 고려하세요:

1. **HTTPS 설정**: 리버스 프록시(nginx) 사용
2. **로드 밸런싱**: 여러 인스턴스 배포
3. **모니터링**: Prometheus, Grafana 등 설정
4. **백업**: 데이터 백업 전략 수립 