# Docker 환경에서 Spring Boot 소스 코드 변경 감지 및 자동 재시작
<br/>
<b>로컬 개발 환경에서는 다음 설정을 통해 소스 코드 변경 시 자동으로 재빌드 및 서버 재시작이 가능합니다.</b>
<br/>
<br/>
✅ 1. VS Code (Cursor IDE) 설정 (settings.json 추가)
<br/>

```
{
    "window.commandCenter": 1,
    "java.autobuild.enabled": true,
    "java.jdt.ls.java.home": "",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.updateBuildConfiguration": "automatic",
    "files.autoSave": "afterDelay",
    "files.autoSaveDelay": 1000,
    "editor.formatOnSave": true,
    "java.configuration.checkProjectSettingsExclusions": false,
    "java.debug.settings.forceBuildBeforeLaunch": true
}
```
<br/>
✅ 2. Gradle 설정 (build.gradle 수정)
<br/>

```
// ... existing code ...

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Development Tools
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    
    // Database
    runtimeOnly 'org.postgresql:postgresql'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

// ... existing code ...

bootRun { // 어플리케이션 실행 태스크
    jvmArgs = [
        "-Dspring.devtools.restart.enabled=true", // 자동 재시작
        "-Dspring.devtools.livereload.enabled=true" // 라이브 리로드
    ]
}
```
<br/>
✅ 3. 자동 빌드 및 실행 명령어
<br/>
<br/>
두 개의 터미널을 열어 각각 실행합니다.
<br/>
<br/>

```
gradle build --continuous // 개발 중 자동 빌드
gradle bootRun --continuous --build // 빌드 후 어플리케이션 자동 실행
```
  
➡  --continuous, --build 옵션을 통해 소스 코드 변경 시 자동으로 감지하고 재빌드 및 재시작이 됩니다.
<br/>
<br/>

<b>하지만 Docker 환경에서는 bootRun --continuous 및 devtools가 동작하지 않습니다</b>
<br/>
<br/>

❌ 1. bootRun --continuous가 실패하는 이유

Docker 내부에서는 파일 변경 이벤트가 로컬과 다르게 처리됩니다.

Gradle이 Docker 볼륨으로 마운트된 파일의 변경 사항을 제대로 감지하지 못합니다.
<br/>
<br/>

❌ 2. spring-boot-devtools가 동작하지 않는 이유
  
devtools는 클래스패스(classpath) 변경을 감지해야 하지만, Docker에서는 src/ 변경 시 바로 반영되지 않습니다.
  
결국 bootRun을 강제로 재시작해야만 변경 사항이 반영됩니다.
  
따라서 도커 환경에서는 스크립트를 활용하여 수동으로 변경 사항을 감지하고 어플리케이션을 재시작해야 합니다.
<br/>
<br/>

## Docker 환경에서 소스 코드 변경 감지 및 자동 재시작 방법
  
<b>방법 1 :</b> 스크립트를 활용하여 변경 감지 후 자동 재시작
  
스크립트에서 파일 변경 감지(md5sum 활용) 후 JAR 재빌드 및 애플리케이션을 재시작 합니다. 

```
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
        #log "Checking for file changes..."
        
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
```

<br/>

<b>방법 2 :</b> bootJar를 사용하여 JAR 실행 방식으로 변경
<br/>
<br/>
Docker 컨테이너가 항상 최신 JAR을 실행하도록 설정합니다.
<br/>
<br/>
🔹 build.gradle 수정 (JAR 빌드 적용)
```
tasks.named('bootJar') {
    archiveFileName = "app.jar"
}
```  
<br/>

🔹 Dockerfile 수정
```
# Base Image
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

# Production Image
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
``` 
<br/>

🔹 docker-compose.dev.yml 수정 (JAR 파일 자동 반영)
```
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    volumes:
      - ./build/libs:/app/build/libs
```
  
<br/>
gradle bootJar 실행 후 자동으로 변경된 JAR 실행 가능합니다.