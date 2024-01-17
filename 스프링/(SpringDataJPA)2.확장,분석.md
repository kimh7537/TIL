#(SpringDataJPA)2.확장,분석

---
---
## ✏️ `사용자 정의 리포지토리`
### ✔️ `구현`
- 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
- 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
- 직접 인터페이스의 메서드를 구현하고 싶을때
    - JPA 직접 사용( `EntityManager` )
    - 스프링 JDBC Template 사용
    - MyBatis 사용
    - 데이터베이스 커넥션 직접 사용
    - Querydsl 사용 등

**사용자 정의 인터페이스**
```java
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
```
**사용자 정의 인터페이스 구현 클래스**
```java
@RequiredArgsConstructor
public class MemerRepositoryImpl implements MemberRepositoryCustom{
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
```
```java
@Test
public void callCustom(){
    List<Member> result = memberRepository.findMemberCustom();
}
```
**사용자 정의 구현 클래스**
- 규칙: `리포지토리 인터페이스 이름` + `Impl`
- 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록

**사용자 정의 인터페이스 상속**
```java
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{...}
```
- 실무에서는 주로 `QueryDSL`이나 `SpringJdbcTemplate`을 함께 사용할 때 사용자 정의 리포지토리 기능 자주 사용


> 항상 사용자 정의 리포지토리가 필요한 것은 아님. 그냥 임의의 리포지토리를 만들어도 됨. 예를들어 `MemberQueryRepository`를 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서 그냥 직접 사용해도 됨. 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작함.⬇️
```java
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final EntityManager em;

    List<Member> findAllMembers(){
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
```

---
### ✔️ `최신 방식`
- 스프링 데이터 2.x 부터는 사용자 정의 구현 클래스에 리포지토리 인터페이스 이름 + `Impl` 을 적용하는 대신에 `사용자 정의 인터페이스 명` + `Impl` 방식도 지원한다.
- `MemberRepositoryImpl` 대신에 `MemberRepositoryCustomImpl` 같이 구현해도됨
```java
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {...}
```

---
---
## ✏️ `Auditing`
- 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶을때
### ✔️ `순수 JPA 사용`
```java
@MappedSuperclass
@Getter
public class JpaBaseEntity {
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
```
```java
public class Member extends JpaBaseEntity {}
```
```java
@Test
public void JpaEventBaseEntity() throws Exception{
    Member member = new Member("member1");
    memberRepository.save(member); //@PrePersit 발생

    Thread.sleep(100);
    member.setUsername("member2");

    em.flush(); //@PreUpdate
    em.clear();
        
    Member findMember = memberRepository.findById(member.getId()).get();

    System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
    System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
}
```
**JPA 주요 이벤트 어노테이션**
- `@PrePersist`, `@PostPersist`
- `@PreUpdate`, `@PostUpdate`


---
### ✔️ `스프링 데이터 JPA 사용`
**설정**
- `@EnableJpaAuditing` 스프링 부트 설정 클래스에 적용해야함
- `@EntityListeners(AuditingEntityListener.class)` 엔티티에 적용

```java
@EnableJpaAuditing
@SpringBootApplication
public class DataJpaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
```
- 등록자, 수정자를 처리해주는 `AuditorAware` 스프링 빈 등록
- `DataJpaApplication` 에 `@EnableJpaAuditing` 도 함께 등록해야 함 주의
```java
public class Member extends BaseEntity{...}
```
```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    private LocalDateTime lastModifiedDate;


    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
```

> 참고: 실무에서 대부분의 엔티티는 등록시간, 수정시간이 필요하지만, 등록자, 수정자는 없을 수도 있음. 그래서 다음과 같이 `Base 타입`을 분리하고, 원하는 타입을 선택해서 상속
```java
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

public class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
}
```

> - 저장시점에 등록일, 등록자는 물론이고, 수정일, 수정자도 같은 데이터가 저장됨. 이렇게 하지 않으면 변경 컬럼이 `null` 일때 등록 컬럼을 또 찾아야 함<br>
> - 저장시점에 저장데이터만 입력하고 싶으면 `@EnableJpaAuditing(modifyOnCreate = false)` 옵션을 사용하면 됨

---
---
## ✏️ `Web 확장`
### ✔️ `도메인 클래스 컨버터`
- HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
```java
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }
    //도메인 클래스 컨버터 사용 후
    @GetMapping("/members/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @PostConstruct
    public void init(){
        memberRepository.save(new Member("userA"));
    }
}
```
- HTTP 요청은 회원 `id` 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
- 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음
- **주의:** 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 함(트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않음)


### ✔️ `페이징과 정렬`
#### ✨ `예제`
```java
@GetMapping("/members")
public Page<Member> list(Pageable pageable){
    Page<Member> page = memberRepository.findAll(pageable);
    return page;
}

@PostConstruct
public void init(){
    for (int i = 0 ; i < 100 ; i++){
        memberRepository.save(new Member("user" + i, i));
    }
}
```
- 파라미터로 `Pageable` 을 받을 수 있음
- `Pageable` 은 인터페이스, 실제는 `PageRequest` 객체 생성

**요청 파라미터**
- ex.`/members?page=0&size=3&sort=id,desc&sort=username,desc`
- page: 현재 페이지, **0부터 시작**
- size: 한 페이지에 노출할 데이터 건수
- sort: 정렬 조건을 정의, 정렬 방향을 변경하고 싶으면 `sort` 파라미터 추가 (`asc` 생략 가능)

**기본값**
1. 글로벌 설정
```yaml
spring.data.web.pageable.default-page-size=20 /# 기본 페이지 사이즈/
spring.data.web.pageable.max-page-size=2000 /# 최대 페이지 사이즈/
```

2. 개별 설정
- `@PageableDefault` 어노테이션을 사용
```java
@RequestMapping(value = "/members_page", method = RequestMethod.GET)
public Page<Member> list(@PageableDefault(size = 12, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
    ...
}
```

3. 접두사
- 페이징 정보가 둘 이상이면 접두사로 구분
- `@Qualifier` 에 접두사명 추가 "{접두사명}_xxx"
- `/members?member_page=0&order_page=1`
```java
public Page<Member> list(
@Qualifier("member") Pageable memberPageable,
@Qualifier("order") Pageable orderPageable, ...
```

#### ✨ `Page내용을 DTO로 변환`

- 엔티티를 API로 노출하면 다양한 문제가 발생함. 그래서 엔티티를 꼭 DTO로 변환해서 반환
- Page는 `map()` 을 지원해서 내부 데이터를 다른 것으로 변경할 수 있음
```java
@Data
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member){
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
```
```java
@GetMapping("/members")
public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable){
    Page<Member> page = memberRepository.findAll(pageable);
    Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null))
    //Page<MemberDto> map = page.map(MemberDto::new);도 가능
    return map;
}
```



**Page를 1부터 시작하기**
- 스프링 데이터는 Page를 0부터 시작함
- 1부터 시작하도록 변경
    1. `Pageable, Page`를 파리미터와 응답 값으로 사용히지 않고, 직접 클래스를 만들어서 처리. 그리고 직접 PageRequest(Pageable 구현체)를 생성해서 리포지토리에 넘김. 물론 응답값도 Page 대신에 직접 만들어서 제공해야함.
    2. `spring.data.web.pageable.one-indexed-parameters` 를 `true` 로 설정. 그런데 이 방법은 web에서 `page` 파라미터를 `-1` 처리 할 뿐이다. 따라서 응답값인 `Page` 에 모두 0 페이지 인덱스를 사용하는 한계가 있음.

```java
@GetMapping("/members")
public MyPage<MemberDto> list(@PageableDefault(size = 5)){
    PageRequest request = PageRequest.of(1, 2);

    Page<MemberDto> map = memberRepository.findAll(request)
        .map(MemberDto::new);
    MyPage<MemberDto>... //임의로 만들어서 반환
}
```
- Page 1요청 (`http://localhost:8080/members?page=1`)
- pageNumber: 0이 나옴
- Page 2요청: 실제 원래 Page 1이 나옴, pageNumber=1, number=1->한계



---
---
## ✏️ `스프링 데이터 JPA 분석`
### ✔️ `구현체 분석`



### ✔️ `새로운 엔티티 구별 방법`

- em.persist(entity) : id="A", createdDate=null
- return entity: id="A", createdDate=...



---
---
## ✏️ `기타 기능`
### ✔️ `Specifications (명세)`



### ✔️ `Query By Example`


### ✔️ `Projections`

```java
List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

for(UsernameOnly usernameOnly : result){
    System.out.println(usernameOnly)//프록시
    System.out.println(usernameOnly.getUsername()) //m1
}
```
- p6: 인터페이스를 프록시로 들고옴, 구현체는 스프링 데이터 JPA가 만듦





- 클래스 기반
```java
//MemberRepository
List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

//Test
List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");
for(UsernameOnlyDto usernameOnlyDto : result){
    System.out.println(usernameOnlyDto.getUsername()); //프록시x, 진짜 클래스
}
```



-중첩
```java
//MemberRepository
<T> List<T> findProjectionsByUsername(String username, Class<T> type);

//Test
List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

for(NestedClosedProjections nestedClosedProjections : result){
    String username = nestedClosedProjections.getUsername();
    String teamName = nestedClosedProjections.getTeam().getName();

    //SQL: Member는 이름만 가져오고, Team은 다 가져옴
}
```


---
### ✔️ `네이티브 쿼리`
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(value = "select * from member where username = ?", nativeQuery =
    true)
    Member findByNativeQuery(String username);
}
```

```java
@Test
public void nativeQuery(){
    Team teamA = new Team("teamA");
    em.persist(teamA);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    Member result = memberRepository.findByNativeQuery("m1");
}
```

---
```java
public interface MemberProjection{
    Long getId();
    String getUsername();
    String getTeamName();
}
```
```java
//MemberRepository


```

```java
@Test
public void nativeQuery(){
    Team teamA = new Team("teamA");
    em.persist(teamA);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
    List<MemberProjection> content = result.getContent();
    for(MemberProjection memberProjection : content){
        System.out.println(memberProjection.getUsername()); //m1, m2
        System.out.println(memberProjection.getTeamName()); //teamA, teamA
    }
}
```
