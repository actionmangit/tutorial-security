# Gateway

## 실행 방법

- __.\gradlew :gateway:back:bootrun__

## 종료 방법

- __ctrl + c__

## Gateway 생성

1. 이점
    - 클라이언트는 한 서버의 URL 만 알면되고 백엔드는 변경없이 자유롭게 리팩터링 할 수 있음.
    - 중앙 집중화 및 제어 측면에서 속도 제한, 인증, 감사 및 로깅
    - CORS 불필요

2. back

    - 어노테이션 추가

    ~~~java
    @SpringBootApplication
    @RestController
    @EnableZuulProxy
    public class BackApplication {
    ~~~

    - yml 설정

    ~~~yml
    zuul:
    routes:
        resource:
        path: /resource/**
        url: http://localhost:9000
    ~~~

    - pom.xml 설정(마이그레이션 필요)

        - 예제의 Dalston은 Spring boot 1.5 환경용임.
        - Greenwich로 변경(2.1용이지만 2.2(Hoxton)용은 RC버전이기 때문에 Greenwich로 사용)

        - AS-IS

        ~~~groovy
        implementation 'org.springframework.cloud:spring-cloud-starter-zuul'
        implementation 'org.springframework.cloud:spring-cloud-dependencies:Dalston.SR4'
        ~~~

        - TO-BE

        ~~~groovy
        implementation 'org.springframework.cloud:spring-cloud-starter-netflix-zuul:2.2.1.RELEASE'
        implementation 'org.springframework.cloud:spring-cloud-dependencies:Greenwich.RELEASE'
        ~~~

3. front

    - home.component.ts
        - resource 호출시 토큰인증 삭제

## Resource 서버 보안

1. application.yml 에 IP 설정

    - 내부망에서만 접속 가능 하게 하도록 세팅(샘플에서만 사용하는 방법으로 솔루션이나 방화벽등을 통해 제어해야 함)

    ~~~yml
    server:
        address: 127.0.0.1
    ~~~

2. Spring security 추가

    ~~~groovy
    implementation 'org.springframework.boot:spring-boot-starter-security'
    ~~~

## 인증 상태 공유

1. back

    - Spring Session 의존성 추가

    - ZUUL 세팅(민감한 것은 없음 세팅)

    ~~~yml
    zuul:
        routes:
            resource:
            sensitive-headers:
    ~~~

2. resource

    - 브라우저가 인증 대화 상자를 팝업하지 못하게 설정
    - CORS 설정 제거
    - 세션 사용안함 설정

    ~~~java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
            .authorizeRequests()
                .anyRequest().authenticated();
    }
    ~~~
