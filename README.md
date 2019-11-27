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
      
### Event 생성 API 구현: Bad Request 처리하기
- @Valid 사용하면 Entity 값들을 검증을 실행(Entity에는 @NotEmpty, @NotNull, @Min..)
- 에러 발생시 @Valid 애노테이션을 사용한 객체 바로 다음의 Errors타입의 객체에 에러를 넣음
```java
@PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
}
```

- 잘못된 값이 들어간 경우(이벤트 종료일이 시작일보다 빠른경우 등) 처리
- EventValidator