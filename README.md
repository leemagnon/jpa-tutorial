# JPA Entity Relationships Tutorial

## 프로젝트 소개
이 프로젝트는 Spring Boot와 JPA를 사용하여 다양한 엔티티 관계를 구현한 예제입니다. 실제 블로그 시스템을 모델링하여 JPA의 다양한 관계 매핑을 실습해볼 수 있습니다. 특히 MyBatis와 비교하여 JPA가 가지는 장점을 실제 코드로 경험해볼 수 있도록 구성되었습니다.

## JPA vs MyBatis 비교
### JPA(Java Persistence API)의 특징
- **객체 중심 개발**: 개발자가 SQL이 아닌 객체지향적인 코드에 집중할 수 있음
- **생산성 향상**: 반복적인 CRUD SQL을 직접 작성하지 않아도 됨
- **유지보수성**: 필드 변경 시 관련된 SQL을 일일이 수정할 필요가 없음
- **패러다임 불일치 해결**: 객체지향과 관계형 데이터베이스 간의 패러다임 차이를 해결
- **데이터베이스 독립성**: JPA는 추상화된 데이터 접근 계층을 제공하여 데이터베이스 교체가 용이

### MyBatis와의 주요 차이점
1. **코드 작성 방식**
   - JPA: 객체 중심의 코드 작성
   - MyBatis: SQL 중심의 코드 작성

2. **성능 최적화**
   - JPA: 1차 캐시, 지연 로딩 등 다양한 성능 최적화 기능 제공
   - MyBatis: 직접적인 SQL 튜닝을 통한 성능 최적화

3. **학습 곡선**
   - JPA: 초기 학습 곡선이 있으나, 숙달 시 생산성 대폭 향상
   - MyBatis: SQL에 익숙하다면 빠른 적용 가능

### JPA의 강점
1. **객체 관계 매핑의 편리성**
   - 복잡한 조인 쿼리 없이도 객체 그래프 탐색 가능
   - 연관 관계 설정만으로 자동 조인 처리

2. **상속 관계 매핑**
   - 테이블 상속 전략을 통한 효율적인 데이터 모델링
   - 다형성을 활용한 확장 가능한 설계

3. **성능 최적화 기능**
   - 지연 로딩을 통한 효율적인 데이터 조회
   - 영속성 컨텍스트를 활용한 캐싱

## JPA 실무 적용 시 주의사항

### 1. 페치 전략 선택
#### 즉시 로딩(EAGER)과 지연 로딩(LAZY)
- **즉시 로딩의 문제점**
  - N+1 문제 발생: 연관된 엔티티를 항상 함께 조회하여 불필요한 쿼리 발생
  - 예상하지 못한 SQL 발생: 연관관계가 복잡할수록 예측이 어려움
  ```java
  // 즉시 로딩 - 피해야 할 예시
  @ManyToOne(fetch = FetchType.EAGER)
  private User user;
  ```

- **지연 로딩 권장**
  - 필요한 시점에 데이터를 조회하여 성능 최적화
  - ToOne 관계(@OneToOne, @ManyToOne)는 기본이 즉시 로딩이므로 명시적으로 지연 로딩 설정
  ```java
  // 지연 로딩 - 권장 예시
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
  ```

#### 성능 최적화를 위한 페치 조인
```java
// Repository에서 페치 조인 사용
@Query("select p from Post p join fetch p.user")
List<Post> findAllWithUser();

// 또는 EntityGraph 사용
@EntityGraph(attributePaths = {"user"})
List<Post> findAll();
```

### 2. 영속성 전이(Cascade) 설정
#### 주의사항
- 엔티티의 생명주기가 완전히 동일한 경우에만 사용
- 여러 엔티티에서 참조하는 경우 사용하지 않음
- CascadeType.REMOVE 사용 시 연관된 모든 엔티티가 삭제되므로 주의

```java
// 잘못된 사용 예시 - 댓글이 여러 엔티티에서 참조될 수 있음
@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
private List<Comment> comments;

// 올바른 사용 예시 - UserProfile은 User와 생명주기가 동일
@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
private UserProfile profile;
```

### 3. 성능 최적화 전략
#### 1) N+1 문제 해결
- **페치 조인 사용**
  ```java
  @Query("select distinct p from Post p join fetch p.comments")
  List<Post> findAllWithComments();
  ```

- **@BatchSize 사용**
  ```java
  @BatchSize(size = 100)
  @OneToMany(mappedBy = "post")
  private List<Comment> comments;
  ```

- **하이버네이트 @Fetch 사용**
  ```java
  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(mappedBy = "post")
  private List<Comment> comments;
  ```

#### 2) 페이징 처리
- ToOne 관계는 페치 조인 가능
- ToMany 관계는 페치 조인 시 페이징 불가능
```java
// ToOne 관계 페이징 - 가능
@Query("select p from Post p join fetch p.user")
Page<Post> findAllWithUser(Pageable pageable);

// ToMany 관계는 BatchSize로 해결
@BatchSize(size = 100)
@OneToMany(mappedBy = "post")
private List<Comment> comments;
```

#### 3) 읽기 전용 쿼리 최적화
```java
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
List<Post> findAllByUserId(Long userId);
```

## JPA 핵심 개념 및 구현

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

### 2. 연관 관계 매핑 예시
```java
// 양방향 OneToMany 관계 설정
@Entity
public class User {
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
    
    // 연관관계 편의 메서드
    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }
}

@Entity
public class Post {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
```

### 3. 상속 관계 매핑 예시
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Content {
    @Id @GeneratedValue
    private Long id;
    private String title;
}

@Entity
public class Article extends Content {
    private String content;
}

@Entity
public class Video extends Content {
    private String url;
    private Integer duration;
}
```

## 프로젝트 구조 및 API

### 1. 엔티티 구조

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

### 2. API 엔드포인트

#### Users
```http
GET /api/users - 사용자 목록 조회
GET /api/users/{id} - 특정 사용자 조회
POST /api/users - 사용자 생성
PUT /api/users/{id} - 사용자 정보 수정
DELETE /api/users/{id} - 사용자 삭제
```

#### UserProfiles
```http
GET /api/users/{userId}/profile - 사용자 프로필 조회
POST /api/users/{userId}/profile - 사용자 프로필 생성
PUT /api/users/{userId}/profile - 사용자 프로필 수정
DELETE /api/users/{userId}/profile - 사용자 프로필 삭제
```

#### Posts
```http
GET /api/posts - 게시물 목록 조회
GET /api/posts/{id} - 특정 게시물 조회
POST /api/posts - 게시물 생성
PUT /api/posts/{id} - 게시물 수정
DELETE /api/posts/{id} - 게시물 삭제
GET /api/users/{userId}/posts - 특정 사용자의 게시물 목록 조회
```

#### Comments
```http
GET /api/posts/{postId}/comments - 게시물의 댓글 목록 조회
GET /api/comments/{id} - 특정 댓글 조회
POST /api/posts/{postId}/comments - 댓글 생성
POST /api/comments/{parentId}/replies - 대댓글 생성
PUT /api/comments/{id} - 댓글 수정
DELETE /api/comments/{id} - 댓글 삭제
```

#### Contents (Articles & Videos)
```http
# Articles
GET /api/articles - 게시글 목록 조회
GET /api/articles/{id} - 특정 게시글 조회
POST /api/articles - 게시글 생성
PUT /api/articles/{id} - 게시글 수정
DELETE /api/articles/{id} - 게시글 삭제

# Videos
GET /api/videos - 동영상 목록 조회
GET /api/videos/{id} - 특정 동영상 조회
POST /api/videos - 동영상 생성
PUT /api/videos/{id} - 동영상 수정
DELETE /api/videos/{id} - 동영상 삭제

# 통합 컨텐츠 검색
GET /api/contents - 모든 컨텐츠 목록 조회 (게시글 + 동영상)
GET /api/contents/{id} - 특정 컨텐츠 조회
GET /api/users/{userId}/contents - 특정 사용자의 모든 컨텐츠 조회
```

## 프로젝트 설정 및 실행 가이드

### 1. 기술 스택
- **Spring Boot 3.2.3**
  - Spring Web
  - Spring Data JPA
  - Spring Validation
  - Spring DevTools
- **Database**
  - PostgreSQL
  - JPA/Hibernate
- **Build Tool**
  - Gradle 8.x
  - Java 17
- **Development Tools**
  - Spring DevTools (자동 리로드)
- **API Documentation**
  - RESTful API

### 2. 주요 의존성 버전
```groovy
dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // Development Tools
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    
    // Database
    runtimeOnly 'org.postgresql:postgresql'
    
    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 개발 환경 설정
- **IDE**: IntelliJ IDEA 권장
- **Java Version**: JDK 17 이상
- **Database**: PostgreSQL 14 이상
- **Build Tool**: Gradle 8.x

### 3. 데이터베이스 설정

#### PostgreSQL Docker 설정
```bash
# PostgreSQL Docker 이미지 다운로드 및 실행
docker pull postgres:14
docker run -d \
    --name postgres-jpa \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_DB=jpa_tutorial \
    -p 5432:5432 \
    postgres:14

# 컨테이너 실행 확인
docker ps

# PostgreSQL 접속
docker exec -it postgres-jpa psql -U postgres

# 데이터베이스 생성 (psql 프롬프트에서)
postgres=# CREATE DATABASE jpa_tutorial;
postgres=# \c jpa_tutorial
postgres=# \dt  # 테이블 목록 확인
```

#### 애플리케이션 설정
`application.properties` 설정:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/jpa_tutorial
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update        
spring.jpa.defer-datasource-initialization=true   
spring.jpa.show-sql=true                   
spring.jpa.properties.hibernate.format_sql=true   

# Data Initialization
spring.sql.init.mode=never                

# DevTools Configuration
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
spring.devtools.restart.additional-paths=src/main/java,src/main/resources
```

### 4. 프로젝트 설정
```bash
# 프로젝트 클론
git clone [repository-url]
cd jpa-tutorial

# 의존성 설치 및 빌드
./gradlew clean build -x test

# 애플리케이션 실행
./gradlew bootRun
```

## 프로젝트 실행 방법

### Docker Compose로 실행
```bash
# 개발 환경 실행 (자동 리로드 지원)
docker-compose -f docker-compose.dev.yml up

# 백그라운드로 실행하려면
docker-compose -f docker-compose.dev.yml up -d

# 로그 확인
docker-compose -f docker-compose.dev.yml logs -f

# 종료
docker-compose -f docker-compose.dev.yml down
```

- 애플리케이션은 `http://localhost:8080`에서 접속 가능
- 데이터베이스는 자동으로 설정되어 실행됨
- 소스 코드 변경 시 자동으로 재시작됨
