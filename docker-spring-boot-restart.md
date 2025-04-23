# Docker 환경에서 Spring Boot 소스 코드 변경 감지 및 자동 재시작
<br/>
<b>로컬 개발 환경에서는 다음 설정을 통해 소스 코드 변경 시 자동으로 재빌드 및 서버 재시작이 가능합니다.</b>
<br/>
<br/>
✅ 1. VS Code 설정 (settings.json 추가)
<br/>

```json
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
* <b>java.autobuild.enabled</b>
<br/> 
자동 빌드 기능 활성화
<br/>
→ 파일을 저장할 때마다 .class 파일이 자동으로 빌드됨
<br/>
→ 이를 통해 Spring Boot DevTools가 변경된 클래스를 감지하여 자동 재시작을 수행할 수 있음
<br/>
<br/>

✅ 2. Gradle 설정 (build.gradle 수정)
<br/>

```gradle
dependencies {
    // DevTools 의존성 추가
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    // ... 다른 의존성들 ...
}
```
<br/>
<b>DevTools가 제공하는 주요 기능</b>
<br/>
<br/>
1. 자동 재시작 (Auto Restart)
<br/>
- 클래스패스의 파일이 변경될 때 자동으로 애플리케이션을 재시작
<br/>
- 주로 Java 소스 코드 변경 시 동작
<br/>
<br/>
2. 라이브 리로드 (Live Reload)
<br/>
- 정적 리소스(HTML, CSS, JS 등) 변경 시 브라우저 자동 새로고침
<br/>
- 별도의 브라우저 플러그인 설치 필요
<br/>
<br/>
<br/>
✅ 3. 자동 빌드 및 실행 명령어
<br/>
<br/>
두 개의 터미널을 열어 각각 실행합니다.
<br/>
<br/>

```bash
# 터미널 1: 소스 코드 변경 감지 및 자동 빌드
gradle build --continuous

# 터미널 2: 애플리케이션 실행
gradle bootRun
```
  
➡  --continuous, --build 옵션을 통해 소스 코드 변경 시 자동으로 감지하고 재빌드 및 재시작이 됩니다.
<br/>
<br/>

<b>하지만 Docker 환경에서는 bootRun --continuous 및 spring-boot-devtools가 동작하지 않습니다</b>
<br/>
<br/>

### ❌ 도커에서 소스 코드 변경 후 재빌드 및 재시작이 실패하는 이유
<br/>
Docker는 "파일 변경 이벤트"를 제대로 감지하지 못하기 때문입니다.
<br/>
<br/>
로컬 개발 환경에서는 파일이 변경될 때 각 운영체제의 커널/시스템에서 제공하는 파일 시스템 모니터링 API를 통해 변경 이벤트가 발생합니다.
<br/>
<br/>

| 운영체제 | API 이름 | 설명 |
|----------|----------|------|
| Linux | inotify | 리눅스 커널 2.6.13 이후 도입된 파일시스템 이벤트 모니터링 시스템으로, 파일/디렉토리의 생성, 수정, 삭제 등의 이벤트를 실시간으로 감지 |
| macOS | FSEvents | Apple의 Core Services 프레임워크의 일부로, 파일시스템 변경사항을 효율적으로 추적하며 시스템 전체의 파일 변경을 모니터링 |
| Windows | ReadDirectoryChangesW | Windows API의 일부로, 지정된 디렉토리의 파일 변경사항을 비동기적으로 감지하는 시스템 콜 |

<br/>
<br/>
Gradle은 이러한 OS 수준의 파일시스템 이벤트를 감지해 재실행합니다.
<br/>
<br/>
반면 Docker는 컨테이너 런타임일 뿐, 운영체제가 아닙니다. 따라서 자체적으로 파일 변경 이벤트를 감지하는 기능을 제공하지 않습니다.
<br/>
<br/>
Docker 컨테이너는 내부적으로 호스트 OS의 커널을 공유하여 실행되는데, 문제는 다음과 같습니다.

#### 1. 볼륨 마운트의 동작 방식
- `-v` 옵션으로 마운트하는 볼륨은 호스트의 디렉터터리를 컨테이너에 연결하는 것입니다.
- 이 연결된 디렉터리에서 발생하는 파일 변경 이벤트는 컨테이너 내부로 전파되지 않습니다.

#### 2. Spring Boot DevTools의 한계
- DevTools는 OS 수준의 파일 시스템 이벤트에 의존하여 변경을 감지합니다.
- 컨테이너 내부에서는 이러한 이벤트를 받을 수 없으므로 자동 재시작 기능이 작동하지 않습니다.
<br/>
<br/>

결과적으로 파일이 변경되어도 컨테이너는 이를 감지할 수 없어, bootRun을 수동으로 재시작하거나 별도의 감지 스크립트를 사용해야 합니다.
<br/>
<br/>

## Docker 환경에서 소스 코드 변경 감지 및 자동 재시작 방법
<br/>
스크립트에서 다음과 같은 방식으로 파일 변경을 감지합니다.
<br/>
<br/>
1. find 명령어로 모든 소스 파일을 찾아서 각 파일의 md5sum을 계산
<br/>
2. 2초 대기 후 동일한 작업 반복
<br/>
3. diff 명령어로 두 시점의 해시값을 비교하여 변경 감지
<br/>
<br/>

💡 md5sum이란?
<br/>
파일 내용을 고정된 길이의 해시값으로 변환하는 도구입니다. 
파일 내용이 조금이라도 변경되면 완전히 다른 해시값이 생성되므로, 
두 시점의 해시값을 비교하여 파일 변경 여부를 정확하게 확인할 수 있습니다.
<br/>
<br/>

```bash
#!/bin/bash

# 현재 실행 중인 Gradle 프로세스 ID 저장 변수
GRADLE_PID=""

# 로그 출력 함수
writeLog() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] - $1"
}

# 애플리케이션 실행 함수 (bootRun)
startApp() {
    writeLog "Building application before running..."
    gradle clean build -x test  # 새롭게 컴파일하여 반영
    writeLog "Starting application with Gradle bootRun..."
    gradle bootRun --continuous --build-cache &
    GRADLE_PID=$!
    writeLog "Application started with PID $GRADLE_PID"
}

# 애플리케이션 종료 함수
stopApp() {
    if [ -n "$GRADLE_PID" ]; then
        writeLog "Stopping application with PID $GRADLE_PID..."
        kill $GRADLE_PID
        wait $GRADLE_PID 2>/dev/null || writeLog "Application process already stopped."
        GRADLE_PID=""
    fi
}

# 변경 감지 및 강제 재시작
monitorChanges() {
    writeLog "Monitoring source code changes using md5sum hashing..."
    while true; do
        #writeLog "Checking for file changes..."
        
        find /app/src -type f -exec md5sum {} \; > /tmp/files_state_before
        sleep 2
        find /app/src -type f -exec md5sum {} \; > /tmp/files_state_after

        if ! diff /tmp/files_state_before /tmp/files_state_after >/dev/null; then
            writeLog "Detected actual content changes in /app/src. Restarting application..."
            stopApp
            startApp
        fi
    done
}

# 초기 실행
writeLog "Initializing script..."
startApp
monitorChanges


