ch4. 이벤 조회 및 수정 REST API 개발
===================

### Event 목록 조회 API 구현
- 페이징, 정렬 어떻게 하지?
  - 스프링 데이터 JPA가 제공하는 Pageable

- Page<Event>에 안에 들어있는 Event 들은 리소스로 어떻게 변경할까?
  - 하나씩 순회하면서 직접 EventResource로 맵핑을 시킬까..
  - PagedResourceAssembler<T> 사용하기

- 테스트 할 때 Pageable 파라미터 제공하는 방법
  - page: 0부터 시작
  - size: 기본값 20
  - sort: property,property(,ASC|DESC)


### Event 조회 API
- 조회하는 이벤트가 있는 경우 이벤트 리소스 확인
  - 링크
  - self
  - profile
  - (update)
  - 이벤트 데이터

- 조회하는 이벤트가 없는 경우 404 응답 확인 
