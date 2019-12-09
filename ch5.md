ch5. REST API 보안 적용
=====================

### Account 도메인 추가
- OAuth2로 인증을 하려면 일단 Account 부터
  - id
  - email
  - password
  - roels

- AccountRoles
  - ADMIN, USER

- JPA 맵핑
  - @Table(“Users”)

- JPA enumeration collection mapping
```
@ElementCollection(fetch = FetchType.EAGER)
@Enumerated(EnumType.STRING)
private Set<AccountRole> roles;
```

- Event에 owner 추가
```
@ManyToOne
Account manager;
```

### 스프링 시큐리티
- 스프링 시큐리티
  - 웹 시큐리티 (Filter 기반 시큐리티)
  - 메소드 시큐리티 
  - 이 둘 다 Security Interceptor를 사용합니다.
     - 리소스에 접근을 허용할 것이냐 말것이냐를 결정하는 로직이 들어있음.

- 의존성 추가
```
 <dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.1.0.RELEASE</version>
 </dependency>
```

- 우리가 만든 커스텀 ROLE 타입을 스프링 시큐리티 인터페이스 타입으로 변환해주는 것 

- 테스트 다 깨짐 (401 Unauthorized)
  - 깨지는 이유는 스프링 부트가 제공하는 스프링 시큐리티 기본 설정 때문.

### 예외 테스트 (3가지 방법)
- @Test(expected)
  - 예외 타입만 확인 가능
```
    @Test(expected = UsernameNotFoundException.class)
    public void findByUsernameFail() {
        String username = "random@email.com";
        accountService.loadUserByUsername(username);
    }
```


- try-catch
  - 예외 타입과 메시지 확인 가능.
  - 하지만 코드가 다소 복잡.
```
    @Test
    public void findByUsernameFail() {
        String username = "random@email.com";
        try{
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage().contains(username));
        }
    }
```

- @Rule ExpectedException
  - 코드는 간결하면서 예외 타입과 메시지 모두 확인 가능
```
    @Test
    public void findByUsernameFail() {
        // Expected
        String username = "random@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        // When
        accountService.loadUserByUsername(username);
    }
```
