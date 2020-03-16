# Gateway

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

## 토큰 인증

1. back

    - Spring Session(, Redis) 추가

### 마이그레이션

- AS IS

~~~ gradle
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
~~~

- TO BE

~~~ gradle
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
    <version>5.2.0.RELEASE</version>
</dependency>
~~~

- 2.X로 넘어오면서 redis 클라이언트를 설정할 수 있게 되었는데 (Jedis, Lettuce) Lettuce 가 Asyc 방식으로 퍼포먼스 면에서 더 좋은 선택이다.


## 로그인 팝업 해제

- XHR(Ajax) 호출시 401에러와 함께 헤더에 __WWW-Authenticate__ 값이 포함된 경우 브라우저에서 강제 인증 팝업을 띄움.

- Spring Security에서 오는 __WWW-Authenticate__ 값을 해제하려면 __X-Requested-With = XMLHttpRequest__ 값을 헤더에 입력해야함.
  - __WWW-Authenticate__ 값은 인증 필요시 인증 방법을 정의한 값을 클라이언트에 알려주는 값. 인증방법(Basic, Bearer...)
  - __X-Requested-With = XMLHttpRequest__ 사용시 ajax 호출로 정의 되어 클라이언트(브라우저)에서 팝업을 노출시키지 않게 한다.
  - __X__ 가 맨앞에 붙는 헤더 키값의 경우 표준이 아님을 뜻함.
  - CORS 통신일 경우 origin 파라메터 덕분에 ajax 임을 확인할 수 있음.
  - HTML5 일경우에도 origin 파라메터가 추가되기 때문에 ajax 임을 확인할 수 있음.

1. front
    - app.module.ts
        - interceptor를 추가하여 XHR호출시 __X-Requested-With = XMLHttpRequest__ 값을 입력하도록함.

## 로그 아웃

- 로그 아웃시 403(forbidden)에러가 발생하는데 CSRF(Cross-Site Request Forgery)가 적용되지 않아서 발생하는 문제.

1. back
    - CSRF를 적용시키기 위해서는 dynamic HTML page를 사용하여 토큰값을 가져오거나 다른 커스텀 Endpoint를 사용하여 가져오거 쿠키를 통해 가져올수 있다.
    - Angural의 경우 쿠키를 기반으로 CSRF(XSRF)를 지원하기 때문에 쿠키를 사용하도록함.
    - 아래 코드를 입력하면 Spring Security(4.1이상)에서 자동 지원함.

    ~~~java
    .and().csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    ~~~

    - __http only__ 옵션은 보안상 클라이언트에서 쿠키를 접근할 수 없게 하는 옵션.
    - __secure cookies__  옵션은 보안상 https가 아닌 통신에서 쿠키를 전달하지 않음

## 상태(세션)가 없어져야 한다

- 인증 및 CSRF 보호를 위한 가장 확실한 방법.
- 이 상태를 다른 곳에 저장하기 위해서는 더 많은 코드 더 많은 유지 보수를 발생시킴.
- 상태가 없어져야 하는 주 이유는 수평 확장을 위해서인데 다음 두가지 방법으로 상태를 존재시키면서 확장시킬수 있다.
    1. 밸런서를 사용
    2. 응용 프로그램간 세션 데이터를 공유(Spring Session)
