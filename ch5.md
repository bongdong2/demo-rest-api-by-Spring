ch5. REST API 보안 적용
=====================

### Account 도메인 추가
- OAuth2로 인증을 하려면 일단 Account 부터
  - id
  - email
  - password
  - roles

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


### 스프링 시큐리티 기본 설정
- 시큐리티 필터를 적용하지 않음...
  - /docs/index.html

- 로그인 없이 접근 가능
  - GET /api/events
  - GET /api/events/{id}

- 로그인 해야 접근 가능
  - 나머지 다...
  - POST /api/events
  - PUT /api/events/{id{
  - ...

- 스프링 시큐리티 OAuth 2.0
  - AuthorizationServer: OAuth2 토큰 발행(/oauth/token) 및 토큰 인증(/oauth/authorize)
     - Oder 0 (리소스 서버 보다 우선 순위가 높다.)
  - ResourceServer: 리소스 요청 인증 처리 (OAuth 2 토큰 검사)
     - Oder 3 (이 값은 현재 고칠 수 없음)

- 스프링 시큐리티 설정
  ```java
        @Configuration
        @EnableWebSecurity
        public class SecurityConfig extends WebSecurityConfigurerAdapter {
        
            @Autowired
            AccountService accountService;
        
            @Autowired
            PasswordEncoder passwordEncoder;
        
            @Bean
            public TokenStore tokenStore() {
                return new InMemoryTokenStore();
            }
        
            @Bean
            @Override
            public AuthenticationManager authenticationManagerBean() throws Exception {
                return super.authenticationManagerBean();
            }
        
            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(accountService)
                        .passwordEncoder(passwordEncoder);
            }
        
            @Override
            public void configure(WebSecurity web) throws Exception {
                web.ignoring().mvcMatchers("/docs/index.html");
                web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
            }
        
            // 아래 방법 보다 위의 방법이 더 낫다.
            /*@Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                        .mvcMatchers("/docs/index.html").anonymous()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).anonymous();
            }*/
        }
    ```
  - @EnableWebSecurity : 스프링부트의 기본설정을 무효화하고 개발자가 만든 설정을 사용한다.
  - @EnableGlobalMethodSecurity
  - extends WebSecurityConfigurerAdapter
  - PasswordEncoder: PasswordEncoderFactories.createDelegatingPassworkEncoder()
  - TokenStore: InMemoryTokenStore
  - AuthenticationManagerBean
  - configure(AuthenticationManagerBuidler auth)
    - userDetailsService
    - passwordEncoder
  - configure(HttpSecurity http)
    - /docs/**: permitAll
  - configure(WebSecurty web)
    - ignore
        - /docs/**
        - /favicon.ico
  - PathRequest.toStaticResources() 사용하기


### 스프링 시큐리티 폼 인증 설정
```
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .formLogin()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
                .anyRequest().authenticated();
    }
```

- 익명 사용자 사용 활성화
- 폼 인증 방식 활성화
    - 스프링 시큐리티가 기본 로그인 페이지 제공
    
```
@Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account seungui = Account.builder()
                        .email("seungui@mail.com")
                        .password("seungui")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(seungui);
            }
        };
    }
```  
    
- 요청에 인증 적용
    - /api 이하 모든 GET 요청에 인증이 필요함. (permitAll()을 사용하여 인증이 필요없이 익명으로 접근이 가능케 할 수 있음)
    - 그밖에 모은 요청도 인증이 필요함.


### 스프링 시큐리티 OAuth 2 설정: 인증 서버 설정
```
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <version>${spring-security.version}</version>
    <scope>test</scope>
</dependency>
```

```java
// 설정 테스트
public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception {
        // Given
        String username = "seungui@email.com";
        String password = "1q2w3e4r";
        Account seungui = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(seungui);

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }
}

// 설정 클래스
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountService accountService;

    @Autowired
    TokenStore tokenStore;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("myApp")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write")
                .secret(this.passwordEncoder.encode("pass"))
                .accessTokenValiditySeconds(10 * 60)
                .refreshTokenValiditySeconds(6 * 10 * 60);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }
}
```

- 토큰 발행 테스트
  - User
  - Client
  - POST /oauth/token
    - HTTP Basic 인증 헤더 (클라이언트 아이디 + 클라이언트 시크릿)
    - 요청 매개변수 (MultiValuMap<String, String>)
        - grant_type: password
        - username
        - password
    - 응답에 access_token 나오는지 확인

- Grant Type: Password
    - Granty Type: 토큰 받아오는 방법
    - 서비스 오너가 만든 클라이언트에서 사용하는 Grant Type
    - https://developer.okta.com/blog/2018/06/29/what-is-the-oauth2-password-grant

- AuthorizationServer 설정
    - @EnableAuthorizationServer
    - extends AuthorizationServerConfigurerAdapter
    - configure(AuthorizationServerSecurityConfigurer security)
        - PassswordEncode 설정
    - configure(ClientDetailsServiceConfigurer clients)
        - 클라이언트 설정
        - grantTypes
            - password
            - refresh_token
        - scopes
        - secret / name
        - accessTokenValiditySeconds
        - refreshTokenValiditySeconds
    - AuthorizationServerEndpointsConfigurer
        - tokenStore
        - authenticationMaanger
        - userDetailsService
        
 ### 스프링 시큐리티 OAuth 2 설정: 리소스 서버 설정
 - ResourceServerConfig를 만들고 EventControllerTest 테스트에서 header정보를 추가
 - 테스트 간에 서로 영향을 주지 않도록 @Before에 DB를 비우기
 
 - 테스트 수정
    - GET을 제외하고 모두 엑세스 토큰을 가지고 요청 하도록 테스트 수정
   
 - ResourceServer 설정
    - @EnableResourceServer
    - extends ResourceServerConfigurerAdapter
    - configure(ResourceServerSecurityConfigurer resources)
        - 리소스 ID
    - configure(HttpSecurity http)
        - anonymous
        - GET /api/** : permit all
        - POST /api/**: authenticated
        - PUT /api/**: authenticated
        - 에러 처리
            - accessDeniedHandler(OAuth2AccessDeniedHandler())

```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**")
                .anonymous()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler((new OAuth2AccessDeniedHandler()));
    }
}


public class EventControllerTests extends BaseControllerTest {
    @Autowired
        EventRepository eventRepository;
    
        @Autowired
        AccountService accountService;
    
        @Autowired
        AccountRepository accountRepository;
    
        // 인메모리 DB이지만 테스트 간에는 공유하므로 서로 영향을 주지 않기 위해 repository를 비워 준다.
        @Before
        public void setUp() {
            this.eventRepository.deleteAll();
            this.accountRepository.deleteAll();
        }
    
    /* header 추가
        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
    */
    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    public String getAccessToken() throws Exception {
        // Given
        String username = "seungui@email.com";
        String password = "1q2w3e4r";
        Account seungui = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(seungui);

        String clientId = "myApp";
        String clientSecret = "pass";

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        );

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }
}
```

### 문자열을 외부 설정으로 빼내기
1. AppProperties.java 생성
2. @ConfigurationProperties(prefix = "my-app")
3. dependency 추가
4. String 프로퍼티들 추가
5. Intellij > Build > Build Project 이후에 application.properties에 자동완성가능
6. prefix 'my-app'으로 프로퍼티 설정
7. AppConfig와 테스트에 하드코딩된 부분 AppProperties를 주입받아 사용

- 기본 유저 만들기
    - ApplicationRunner
    - Admin
    - User

- 외부 설정으로 기본 유저와 클라이언트 정보 빼내기
    - @ConfigurationProperties
