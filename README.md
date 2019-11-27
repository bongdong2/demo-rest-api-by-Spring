입력값 제한하기
---
- Event 도메인에서 입력받을 값만 따로 EventDto로 관리
- EventController에서 EventDto를 사용하기 위해 ModelMapper 사용
- @WebMvcTest 빼고 통합테스트로 전환 (@SpringBootTest @AutoConfigureMockMvc)
