#!/bin/bash

# Voice AI Demo Docker 배포 스크립트

echo "🚀 Voice AI Demo Docker 배포를 시작합니다..."

# 환경변수 파일 확인
if [ ! -f .env ]; then
    echo "❌ .env 파일이 없습니다."
    echo "📝 env.example을 .env로 복사하고 API 키를 설정해주세요:"
    echo "   cp env.example .env"
    echo "   # .env 파일에서 ELEVENLABS_API_KEY를 설정하세요"
    exit 1
fi

# API 키 확인
if ! grep -q "ELEVENLABS_API_KEY=your_elevenlabs_api_key_here" .env; then
    echo "✅ API 키가 설정되어 있습니다."
else
    echo "❌ .env 파일에서 ELEVENLABS_API_KEY를 설정해주세요."
    exit 1
fi

# 로그 디렉토리 생성
mkdir -p logs

# 기존 컨테이너 중지 및 제거
echo "🛑 기존 컨테이너를 중지하고 제거합니다..."
docker-compose down

# 이미지 빌드
echo "🔨 Docker 이미지를 빌드합니다..."
docker-compose build --no-cache

# 컨테이너 시작
echo "🚀 컨테이너를 시작합니다..."
docker-compose up -d

# 헬스체크
echo "🏥 헬스체크를 수행합니다..."
sleep 10

# 서비스 상태 확인
if curl -f http://localhost:8080/api/voice/health > /dev/null 2>&1; then
    echo "✅ 배포가 성공적으로 완료되었습니다!"
    echo "🌐 서비스 URL: http://localhost:8080"
    echo "📊 컨테이너 상태:"
    docker-compose ps
else
    echo "❌ 서비스가 정상적으로 시작되지 않았습니다."
    echo "📋 로그를 확인해주세요:"
    docker-compose logs
    exit 1
fi 