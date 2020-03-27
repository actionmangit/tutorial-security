# Gateway

## 실행 방법

- **.\gradlew :gateway:back:bootrun**

## 종료 방법

- **ctrl + c**

## 인증 서버 생성

1. auth

   - 종속성 설정
     - Spring5 부터 Spring Security OAuth를 Spring Security로 전환중에 있다. 현재 Authorization쪽은 개발이 되어 있지 않기 때문에 호환성 유지를 위해 만들어진 org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure를 사용해야 한다. 해당 종속성은 stater에 포함되지 않은 종속성으로 따로 버전을 기입해야한다.

   ```groovy
   implementation 'org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:스프링버전'
   ```

   - 어노테이션 추가

   ```java
   @EnableAuthorizationServer
   ```

   - yml 설정(마이그레이션)

   ```yml
   security:
     oauth2:
       client:
         client-id: acme
         client-secret: acmesecret
         authorized-grant-types: authorization_code, refresh_token, password
         scope: openid
         registered-redirect-uri: http://example.com
   server:
     port: 9999
     servlet:
       context-path: /uaa
   ```

   - Spring Security 설정
     - /oauth/authorize를 permitAll 시킨다

   ```java
   @Override
   protected void configure(final HttpSecurity http) throws Exception {
       // @formatter:off
       http.httpBasic().and()
           .logout().and()
           .authorizeRequests()
               .antMatchers("/oauth/authorize", "/login").permitAll()
               .anyRequest().authenticated();
       // @formatter:on
   }
   ```

   - Access 토큰을 받기 위한 도메인 등록

     - <http://localhost:9999/uaa/oauth/authorize?response_type=code&client_id=acme&redirect_uri=http://example.com&state=1234> 호출

       - response_type=code authorization code flow를 사용한다는 것을 알려줌.(OpenID Connect Flow)
         - code : Authorization Code
         - id_token : Implicit
         - id_token token : Implicit
         - code id_token : Hybrid
         - code token : Hybrid
         - code id_token token : Hybrid  

       - client_id=acme 개발자가 클라이언트를 등록할때 받은 클라이언트 아이디
       - redirect_uri=<http://example.com> 인증 서버에게 요청을 승인 한 후 사용자를 다시 보낼 위치
       - scope 권한 요청. 인증서버에서 지원하는 범위까지
       - state 클라이언트에서 임의의 문자열을 생성하여 요청에 포함한다. CSRF 공격 방지

     - 로그인 후 승인여부 결정
     - 응답 <http://example.com/?code=p6DTgX&state=1234>

   - 토큰 생성

     - curl acme:acmesecret@localhost:9999/uaa/oauth/token \
        -d grant_type=authorization_code -d client_id=acme \
        -d redirect_uri=http://example.com -d code=Ro55aW

     ```json
     {
       "access_token": "f6f58f5a-ac7f-4304-ba8c-0d2d50516fe0",
       "token_type": "bearer",
       "refresh_token": "4e89b35f-5dea-4119-939d-83d7892e6948",
       "expires_in": 43199,
       "scope": "openid"
     }
     ```

     - token_type
        - Bearer token 인증 방식(해더에 토큰값을 입력하는 방식)
     - scope:openid
        - OpenID Connect 사용

## resource 서버 설정 변경

1. resource

   - 원래는 Spring Security 5.3을 이용해 구현하려고 하였다. 하지만 기존 Spring Security OAuth와 다른점이 많았다. 첫째는 Resource 서버와 Client의 경우 완성이 되었으나 Authorization 서버는 완성되지 않아 서로간의 합이 맞지 않는 점이 있었다. (예: Resource 서버에서 토큰의 인증이 필요로 하게 되었는데(introspection) Spring Security OAuth에는 없어서 커스텀 구현을 해야함). 둘째는 현재 완성중이다 보니 샘플부분이 확연히 부족했다.

   - 종속성 설정

   ```groovy
   implementation 'org.springframework.security.oauth:spring-security-oauth2'
   ```

   - 어노테이션 추가

   ```java
   @EnableResourceServer
   ```

   - yml 설정

   ```yml
   security:
     oauth2:
       resource:
         userInfoUri: http://localhost:9999/uaa/userinfo
   ```

   - userinfo는 resource 서버를 연결하여 token 디코딩하는 유일한 방법은 아니다. 하지만 OAuth provider 들이 많이 쓰고 있는 방법이다. 다른 방법도 있는데 JWT나 backend Store를 이용할 수도 있다. token_info endpoint를  사용하면 더 자세한 정보를 얻을 수 있는데 그만큼 더 철저한 인증이 필요하다.

2. auth
   - 어노테이션 추가

   ```java
   @EnableResourceServer
   ```

   - 해당 어노테이션 사용시 "/ oauth / *"엔드 포인트를 제외한 Authorization 서버의 모든 것을 보호함.

3. back
   - back 계층에서 Zuul을 사용하기 때문에 spring-cloud-starter-oauth2대신 spring-security-oauth2프록시를 통해 토큰을 릴레이하기위한 자동 구성을 설정.

   - 종속성 설정

   ```groovy
   implementation 'org.springframework.security.oauth:spring-security-oauth2'
   ```

   - 어노테이션 추가

   ```java
   @EnableOAuth2Sso
   ```


2. front

   - home.component.ts
     - resource 호출시 토큰인증 삭제

## Resource 서버 보안

1. application.yml 에 IP 설정

   - 내부망에서만 접속 가능 하게 하도록 세팅(샘플에서만 사용하는 방법으로 솔루션이나 방화벽등을 통해 제어해야 함)

   ```yml
   server:
     address: 127.0.0.1
   ```

2. Spring security 추가

   ```groovy
   implementation 'org.springframework.boot:spring-boot-starter-security'
   ```

## 인증 상태 공유

1. back

   - Spring Session 의존성 추가

   - ZUUL 세팅(민감한 것은 없음 세팅)

   ```yml
   zuul:
     routes:
       resource:
       sensitive-headers:
   ```

2. resource

   - 브라우저가 인증 대화 상자를 팝업하지 못하게 설정
   - CORS 설정 제거
   - 세션 사용안함 설정

   ```java
   @Override
   protected void configure(HttpSecurity http) throws Exception {
       http.httpBasic().disable()
           .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
           .authorizeRequests()
               .anyRequest().authenticated();
   }
   ```
