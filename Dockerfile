# Base stage - Gradle 설치된 기본 이미지
FROM gradle:8.5-jdk17 AS builder-base
WORKDIR /app

# inotify-tools 설치 (파일 변경 감지)
# RUN apt-get update && apt-get install -y inotify-tools && rm -rf /var/lib/apt/lists/*

# Development stage - 개발 환경용 (run.sh 사용)
FROM builder-base AS development
WORKDIR /app
COPY run.sh /app/run.sh
RUN chmod +x /app/run.sh
EXPOSE 8080
ENTRYPOINT ["/app/run.sh"]
