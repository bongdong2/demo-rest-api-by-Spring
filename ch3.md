ch3. Spring HATEOAS
===================

### 스프링 HATEOAS ?
- 링크를 만드는 기능
  - 문자열 가지고 만들기
  - 컨트롤러와 메소드로 만들기
- 리소스를 만드는 기능
  - 리소스? 테이터(우리가 만들어 보내주는 본문) + 링크
- 링크를 찾아주는 기능 (이 강좌에서는 다루지 않음)
  - Traverson
  - LinkDeiscoverers
- 링크
  - HREF : hypermedia reference(uri, url설)
  - REL : relation 현재 이 리소스와의 관계를 표현
    - self : 자기 자신에 대한 url을 넣어 줄 때
    - profile : 응답 본문에 대한 설명을 가지고 있는 문서로 링크를 걸 때
    - update-event
    - query-events

### 스프링 HATEOAS 적용
- EvnetResource 만들기
  - extends RepresentationModel 문제 
    - @JsonUnwrapped로 해결
    - extends EntityModel<T>로 해결
- 테스트 할 것
  - 응답에 HATEOAS와 profile 관련 링크가 있는지 확인
    - self(view)
    - update(만든 사람은 수정할 수 있으므로)
    - events(목록으로 가는 링크)


### 스프링 REST Docs 소개
- 스프링 MVC 테스트를 사용하여 REST API문서의 일부분을 생성하는 유용한 기능을 제공하는 라이브러리

- Spring REST Docs / Swagger 가 있는데 이 강좌에서 Swagger는 다루지 않음

- REST Docs 코딩
  - andDo(document(“doc-name”, snippets))
  - snippets
    - links()
    - requestParameters() + parameterWithName()
    - pathParameters() + parametersWithName()
    - requestParts() + partWithname()
    - requestPartBody()
    - requestPartFields()
    - requestHeaders() + headerWithName()
    - requestFields() + fieldWithPath()
    - responseHeaders() + headerWithName()
    - responseFields() + fieldWithPath()
    - ...
  - Relaxed*
  - Processor
    - preprocessRequest(prettyPrint())
    - preprocessResponse(prettyPrint())
    - ...
