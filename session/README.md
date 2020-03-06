# Session

## 실행 방법

- __.\gradlew :session:back:bootrun__

## 종료 방법

- __ctrl + c__

## Resource 서버 생성

1. front

    - home.component.ts
        - resource URI 변경

2. resource

    - Spring Initialize 를 통한 서버 생성
    - application.yml에 서버 포트 설정

    ~~~yml
    server:
        port: 9000
    ~~~

## CORS 설정

1. resource

    - Spring(4.2이상)의 CORS 지원 기능 사용

    ~~~ java
    @RequestMapping("/")
    @CrossOrigin(origins="*", maxAge=3600)
    public Message home() {
        return new Message("Hello World");
    }
    ~~~

    - __origins="*"__ 는 테스트 용으로 사용한 것으로 실제로 세팅하면 안됨.

## 토큰 인증을 위한 준비

1. back

    - Spring Session, Redis 의존성 목록에 추가

### 마이그레이션

- AS IS

    ~~~ gradle
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    ~~~

- TO BE

    ~~~ gradle
    implementation 'org.springframework.session:spring-session-data-redis'
    implementation 'io.lettuce:lettuce-core:5.2.0.RELEASE'
    ~~~

- 2.X로 넘어오면서 redis 클라이언트를 설정할 수 있게 되었는데 (Jedis, Lettuce) Lettuce 가 Asyc 방식으로 퍼포먼스 면에서 더 좋은 선택이다.

## 토큰 인증

1. front

    - home.component.ts
        - 토큰을 입력 받고 헤더에 토큰값을 입력한뒤 호출하도록 로직 수정

2. back

    - 토큰 생성 로직 적용

3. resource

    - front 단에서 들어오는 호출을 허용 가능하게 CORS 옵션 추가

        ~~~java
        @RequestMapping("/")
        @CrossOrigin(origins = "*", maxAge = 3600,
            allowedHeaders={"x-auth-token", "x-requested-with", "x-xsrf-token"})
        public Message home() {
            return new Message("Hello World");
        }
        ~~~

        - resource 서버의 security 설정에 cors 옵션을 추가함(permitAll()의 경우 프리플라이트때 민감한 데이터를 실수로 보낼수가 있어서 안전한 cors()사용)

    - Resource 서버의 Spring Security 옵션 설정

        

    - JSESSIONID 를 보는 것이 아니라 X-Auth-Token에서 세션값을 찾도록 로직 수정

        ~~~java
        @Bean
        public HttpSessionIdResolver httpSessionIdResolver() {
            return HeaderHttpSessionIdResolver.xAuthToken();
        }
        ~~~


4. 
