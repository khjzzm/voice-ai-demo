## 프로젝트 개요
이 프로젝트는 Spring Boot를 사용하는 음성 대화형 AI 프로젝트입니다.

## 주요 기술 스택
- Java 1.8
- Spring Boot 2.2.1.RELEASE
- Gradle

## API 호출 스타일
- 모든 외부 API 호출은 `WebClient`를 사용하여 비동기(asynchronous) 및 논블로킹(non-blocking) 방식으로 작성해야 합니다.
- API 호출 메소드의 반환 타입은 `Mono` 또는 `Flux`여야 합니다.

## 코드 스타일
- Google Java 스타일 가이드를 준수합니다.
- 반복적인 코드를 줄이기 위해 Lombok의 `@RequiredArgsConstructor` 같은 어노테이션을 적극적으로 사용합니다.

## 의존성 관리
- 모든 프로젝트 의존성은 `build.gradle` 파일에서 관리합니다.

## 설정 관리
- API 키, URL 등 외부 설정은 `src/main/resources/application.properties` 파일에 정의합니다.
- `@Value` 어노테이션을 사용하여 설정 값을 클래스에 주입합니다.