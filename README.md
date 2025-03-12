# JPA Entity Relationships Tutorial

## 프로젝트 소개
이 프로젝트는 Spring Boot와 JPA를 사용하여 다양한 엔티티 관계를 구현한 예제입니다. 실제 블로그 시스템을 모델링하여 JPA의 다양한 관계 매핑을 실습해볼 수 있습니다.

## 주요 기능 및 특징

### 1. 다양한 JPA 관계 매핑 구현
- **OneToMany & ManyToOne**: 사용자와 게시물 관계
- **OneToOne**: 사용자와 프로필 정보
- **Inheritance**: 컨텐츠 타입 (게시글, 비디오)
- **Self-Join**: 댓글과 대댓글 구조

### 2. 엔티티 구조
![Entity Relationship Diagram](./docs/images/erd.png)

#### User & UserProfile (OneToOne)
- 사용자는 하나의 프로필만 가질 수 있음
- 프로필은 사용자의 상세 정보 포함

#### User & Post (OneToMany)
- 한 사용자는 여러 게시물 작성 가능
- 게시물은 반드시 작성자가 있어야 함

#### Content Hierarchy (Inheritance)
- Content: 기본 컨텐츠 정보
  - Article: 텍스트 중심 컨텐츠
  - Video: 동영상 컨텐츠

#### Comment System (Self-Join)
- 댓글은 대댓글을 가질 수 있음
- 계층형 댓글 구조 구현

### 3. 기술 스택
- **Spring Boot 3.2.3**
  - Spring Web
  - Spring Data JPA
  - Spring DevTools
- **Database**
  - PostgreSQL
  - Hibernate
- **Documentation**
  - Swagger/OpenAPI
- **Development**
  - Lombok
  - P6Spy (SQL 로깅)

### 4. API 엔드포인트

#### Users
```http
GET /api/users - 사용자 목록 조회
GET /api/users/{id} - 특정 사용자 조회
POST /api/users - 사용자 생성
PUT /api/users/{id} - 사용자 정보 수정
DELETE /api/users/{id} - 사용자 삭제
```

#### Posts
```http
GET /api/posts - 게시물 목록 조회
GET /api/posts/{id} - 특정 게시물 조회
POST /api/posts - 게시물 생성
PUT /api/posts/{id} - 게시물 수정
DELETE /api/posts/{id} - 게시물 삭제
```

[다른 API 엔드포인트들...]

## 프로젝트 설정 및 실행 방법

### 1. 개발 환경 설정
```bash
# PostgreSQL 설치 및 데이터베이스 생성
createdb jpa_tutorial

# 프로젝트 클론
git clone [repository-url]
cd jpa-tutorial

# 의존성 설치 및 빌드
./gradlew build
```

### 2. 애플리케이션 설정
`application.properties` 주요 설정:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/jpa_tutorial
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# DevTools
spring.devtools.restart.enabled=true
```

### 3. 실행 방법
```bash
./gradlew bootRun
```
서버는 `http://localhost:8080`에서 실행됩니다.

## 주요 구현 포인트

### 1. JPA 관계 매핑
```java
// OneToOne 예시
@OneToOne(mappedBy = "user")
private UserProfile profile;

// OneToMany 예시
@OneToMany(mappedBy = "user")
private Set<Post> posts;

// Inheritance 예시
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Content { }

// Self-Join 예시
@ManyToOne
@JoinColumn(name = "parent_id")
private Comment parentComment;
```

### 2. 데이터 초기화
- `data.sql`을 통한 초기 데이터 설정
- 각 엔티티 간의 관계를 고려한 데이터 삽입 순서

### 3. API 설계
- RESTful API 원칙 준수
- 계층형 구조의 엔드포인트 설계
- 적절한 HTTP 메서드 사용
