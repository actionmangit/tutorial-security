# Tutorial Security

## 초기 세팅

### Gradle 세팅

1. 환경 변수 세팅
    - 시스템 변수에 __GRADLE_HOME__ 에 gradle 설치 경로 입력.
    - __path__ 에 __%GRADLE_HOME%\bin__  입력
    - 세팅 확인 __gradle -v__

2. 설정
    - 최상위 폴더에 __settings.gradle__ 설정
    - 하부 프로젝트에 __build.gradle__ 설정
    - 하부 프로젝트의 back단 실행 (ex. __.\gradlew :basic:back:bootrun__)

### nodejs 세팅

1. 환경 변수 세팅
    - __path__ 에 nodejs 설치 경로 입력. (installer 로 실행시 자동으로 세팅)

### Angural 세팅

1. Angural cli 설치
    - __npm install -g @angular/cli__ 입력.

2. Angural 설치
    - Angural를 설치할 폴더에 __ng new front__ 입력. (내부에 front 폴더 생성)
