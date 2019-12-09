ch5. REST API 보안 적용
=====================

### Account 도메인 추가
- id
- email
- password
- roels

###AccountRoles
- ADMIN, USER

### JPA 맵핑
- @Table(“Users”)

### JPA enumeration collection mapping
```
@ElementCollection(fetch = FetchType.EAGER)
@Enumerated(EnumType.STRING)
private Set<AccountRole> roles;
```

Event에 owner 추가
```
@ManyToOne
Account manager;
```


