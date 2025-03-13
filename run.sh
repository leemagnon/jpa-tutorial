#!/bin/bash

# 현재 실행 중인 Gradle 프로세스 ID 저장 변수
GRADLE_PID=""

# 로그 출력 함수
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] - $1"
}

# 애플리케이션 실행 함수 (bootRun)
start_app() {
    log "Building application before running..."
    gradle clean build -x test  # 새롭게 컴파일하여 반영
    log "Starting application with Gradle bootRun..."
    gradle bootRun --continuous --build-cache &
    GRADLE_PID=$!
    log "Application started with PID $GRADLE_PID"
}

# 애플리케이션 종료 함수
stop_app() {
    if [ -n "$GRADLE_PID" ]; then
        log "Stopping application with PID $GRADLE_PID..."
        kill $GRADLE_PID
        wait $GRADLE_PID 2>/dev/null || log "Application process already stopped."
        GRADLE_PID=""
    fi
}

# 변경 감지 및 강제 재시작
monitor_changes() {
    log "Monitoring source code changes using md5sum hashing..."
    while true; do
        log "Checking for file changes..."
        
        find /app/src -type f -exec md5sum {} \; > /tmp/files_state_before
        sleep 2
        find /app/src -type f -exec md5sum {} \; > /tmp/files_state_after

        if ! diff /tmp/files_state_before /tmp/files_state_after >/dev/null; then
            log "Detected actual content changes in /app/src. Restarting application..."
            stop_app
            start_app
        fi
    done
}

# 초기 실행
log "Initializing script..."
start_app
monitor_changes
