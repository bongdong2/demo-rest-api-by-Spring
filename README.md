스프링 기반 REST API 개발강좌 정리노트
=================================

### Event 생성 API 구현 : 입력값 제한하기

- Event 도메인에서 입력받을 값만 따로 EventDto로 관리
- EventController에서 EventDto를 사용하기 위해 ModelMapper 사용
- @WebMvcTest 빼고 통합테스트로 전환 (@SpringBootTest @AutoConfigureMockMvc)

### Event 생성 API 구현 : 입력값 이외에 에러 발생
- '입력값 제한하기'에서는 Dto에 있는 값이 아니면 입력값을 무시했지만 이번에는 에러를 발생
- json -> object문자열 : deserialization
  - spring.jackson.deserialization.fail-on-unknown-properties=true
      - deserialization 할 때, unknown-properties가 있으면 실패해라