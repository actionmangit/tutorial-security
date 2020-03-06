# Login

## 실행 방법

- __.\gradlew :login:back:bootrun__

## 종료 방법

- __ctrl + c__

## 로그인 기능 추가

1. front
    - app.component.html
        - HTML 템플릿으로 신규 추가.

    - app.module.ts
        - __RouterModule__ 을 추가하여 home/login 링크 설정.
        - __FormsModule__ 추가.

    - app.component.ts
        - __AppService__ 의존성 주입
        - 로그아웃 기능 추가. __finalize__ 선언으로 무조건 실행하도록 함.
        - __authenticate__ 함수로 실제 이미 인증되었는지 확인.

    - app.service.ts
        - __authenticate__ 함수 구현

    - home.component.html
        - 홈 화면

    - home.component.ts
        - 홈 화면 컴포넌트

    - login.component.html
        - 로그인 화면

    - login.component.ts
        - 로그인 컴포넌트

2. back

    - /user API 생성
        - 있으면 인증 사용자 정보 리턴, 없으면 Spring Security에서 401 에러 발생

    - Spring Security 설정
        - 익명의 사용자가 정적 리소스(*.html)만 접근가능하도록.
        - Angural에서 제공하는 javascript 구성요소를 익명의 사용자가 사용가능하게.

### 마이그레이션

1. front
    - app.component.ts
        - AS IS

            ~~~javascript
            import 'rxjs/add/operator/finally'

            this.http.post('logout', {}).finally(() => {
                this.app.authenticated = false;
                this.router.navigateByUrl('/login');
            }).subscribe();

        - TO BE

            ~~~javascript
            import { finalize } from 'rxjs/operators';

            this.http.post('logout', {}).pipe(finalize(() => {
                this.app.authenticated = false;
                this.router.navigateByUrl('/login');
            })).subscribe();

2. back
    - security.ignored
        - yml 설정은 Spring boot 1.X 버전에서만 사용가능
        - 2.X 에서는 자바 세팅만 가능

        ~~~java
        @Override
        public void configure(WebSecurity web) {
            web
                .ignoring()
                    .antMatchers("/*es2015.*");
        }
        ~~~

        - permitAll()을 사용해도 좋으나 정적 리소스는 ignoring() 하는것이 좋다. (permitAll()의 경우 필터 동작이 추가됨)

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
