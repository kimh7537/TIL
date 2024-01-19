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
> - 저장시점에 저장데이터(update=null, create=값)만 입력하고 싶으면 `@EnableJpaAuditing(modifyOnCreate = false)` 옵션을 사용하면 됨

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
1. 글로벌 설정(application.yml)
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
    
1번 방법. `Pageable, Page`를 파리미터와 응답 값으로 사용히지 않고, 직접 클래스를 만들어서 처리. 그리고 직접 PageRequest(Pageable 구현체)를 생성해서 리포지토리에 넘김. 물론 응답값도 Page 대신에 직접 만들어서 제공해야함.
```java
@GetMapping("/members")
public MyPage<MemberDto> list(@PageableDefault(size = 5) Pageable pageable){
    PageRequest request = PageRequest.of(1, 2);

    Page<MemberDto> map = memberRepository.findAll(request)
        .map(MemberDto::new);
    MyPage<MemberDto>... //임의로 만들어서 반환
}
```

2번 방법. `spring.data.web.pageable.one-indexed-parameters` 를 `true` 로 설정. 그런데 이 방법은 web에서 `page` 파라미터를 `-1` 처리 할 뿐이다. 따라서 응답값인 `Page` 에 모두 0 페이지 인덱스를 사용하는 한계가 있음.

- Page 1요청 (`http://localhost:8080/members?page=1`)
- pageNumber: 0이 나옴
- Page 2요청: 실제 원래 Page 1이 나옴, pageNumber=1, number=1->한계



---
---
## ✏️ `스프링 데이터 JPA 분석`
### ✔️ `구현체 분석`
- 스프링 데이터 JPA가 제공하는 공통 인터페이스의 구현체

**SimpleJpaRepository**
```java
@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID> ...{
    @Transactional
    public <S extends T> S save(S entity) {
        if (entityInformation.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }
    ...
}
```

- `@Repository` 적용: JPA 예외를 스프링이 추상화한 예외로 변환
- `@Transactional` 트랜잭션 적용
   - JPA의 모든 변경은 트랜잭션 안에서 동작
   - 스프링 데이터 JPA는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 처리
   - 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
   - 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
   - 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능했음(사실은 트랜잭션이 리포지토리 계층에 걸려있는 것임)
- `@Transactional(readOnly = true)`
   - 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 `readOnly = true` 옵션을 사용하면 플러시를 생략해서 약간의 성능 향상을 얻을 수 있음



### ✔️ `새로운 엔티티 구별 방법`

**매우 중요!!!**
- `save()` 메서드
   - 새로운 엔티티면 저장( `persist` )
   - 새로운 엔티티가 아니면 병합( `merge` )
- 새로운 엔티티를 판단하는 기본 전략
   - 식별자가 객체일 때 `null` 로 판단
   - 식별자가 자바 기본 타입일 때 `0` 으로 판단
   - `Persistable` 인터페이스를 구현해서 판단 로직 변경 가능

- JPA 식별자 생성 전략이 `@GenerateValue` 면 `save()` 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작함(`persist`) 
- JPA 식별자 생성 전략이 `@Id` 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 `save()` 를 호출    
  - `merge()` 가 호출됨 
  - `merge()` 는 우선 DB를 호출해서 값을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율적
  - `Persistable` 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적
  - 등록시간( `@CreatedDate` )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있음 (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
```
```java
public interface ItemRepository extends JpaRepository<Item, Long> {
}
```
```java
@SpringBootTest
public class ItemRepositoryTest {
    @Autowired ItemRepository itemRepository;

    @Test
    public void save(){
        Item item = new Item("A");
        itemRepository.save(item);
    }
}
```
**save 함수에서**
- `em.persist(entity)` : id="A", createdDate=null
- `return entity` : id="A", createdDate=값들어감



---
---
## ✏️ `기타 기능`
### ✔️ `Specifications (명세)`
**술어(predicate)**
- 참 또는 거짓으로 평가
- AND OR 같은 연산자로 조합해서 다양한 검색조건을 쉽게 생성(컴포지트 패턴)
- 스프링 데이터 JPA는 `org.springframework.data.jpa.domain.Specification` 클래스로 정의

```java
public interface MemberRepository extends JpaRepository<Member, Long>,
JpaSpecificationExecutor<Member>{...}
```
- 테스트 코드와 작성 코드(`MemberSpec`)는 자료 참고

---
### ✔️ `Query By Example`
```java
@SpringBootTest
@Transactional
public class QueryByExampleTest {
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void basic() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        em.persist(new Member("m1", 0, teamA));
        em.persist(new Member("m2", 0, teamA));
        em.flush();

        //when
        //Probe 생성
        Member member = new Member("m1");
        Team team = new Team("teamA"); //내부조인으로 teamA 가능
        member.setTeam(team);

        //ExampleMatcher 생성, age 프로퍼티는 무시
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        Example<Member> example = Example.of(member, matcher);
        List<Member> result = memberRepository.findAll(example);

        //then
        assertThat(result.size()).isEqualTo(1);
    }
}
```
- Probe: 필드에 데이터가 있는 실제 도메인 객체
- ExampleMatcher: 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
- Example: Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용

**장점**
- 동적 쿼리를 편리하게 처리
- 도메인 객체를 그대로 사용
- 데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있음
- 스프링 데이터 JPA `JpaRepository` 인터페이스에 이미 포함

**단점**
- 조인은 가능하지만 내부 조인(INNER JOIN)만 가능함 외부 조인(LEFT JOIN) 안됨
- 다음과 같은 중첩 제약조건 안됨 `firstname = ?0 or (firstname = ?1 and lastname = ?2)`
- 매칭 조건이 매우 단순함
   - 문자는 `starts/contains/ends/regex`
   - 다른 속성은 정확한 매칭( `=` )만 지원
- **실무에서는 QueryDSL을 사용하자**


---
### ✔️ `Projections`

- 엔티티 대신에 DTO를 편리하게 조회할 때 사용
- 전체 엔티티가 아니라 만약 회원 이름만 조회하고 싶다면

#### ✨ 인터페이스 기반 Closed Projections
- 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공

```java
public interface UsernameOnly {
    String getUsername();
}
```
- 조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)

```java
public interface MemberRepository ... {
    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
}
```
- 메서드 이름은 자유, 반환 타입으로 인지

```java
@Test
public void projections() throws Exception {
    //given
    Team teamA = new Team("teamA");
    em.persist(teamA);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    //when
    List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

    //then
    for(UsernameOnly usernameOnly : result){
        System.out.println(usernameOnly)//프록시
        System.out.println(usernameOnly.getUsername()) //m1
    }
}
```
```sql
select m.username from member m
where m.username=‘m1’;
```
- SQL에서도 select절에서 username만 조회(Projection)하는 것을 확인
- 인터페이스를 프록시로 들고옴, 구현체는 스프링 데이터 JPA가 만듦


#### ✨ 인터페이스 기반 Open Proejctions

```java
public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
```
- SpEL문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산함! 따라서 JPQL SELECT 절 최적화가 안됨


#### ✨ 클래스 기반
- 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능
- 생성자의 파라미터 이름으로 매칭
```java
public class UsernameOnlyDto {
    private final String username;

    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
```
```java
//MemberRepository
List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

//Test
List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");
for(UsernameOnlyDto usernameOnlyDto : result){
    System.out.println(usernameOnlyDto.getUsername()); //프록시x, 진짜 클래스
}
```

#### ✨ 동적 Projections
- 다음과 같이 Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능
```java
<T> List<T> findProjectionsByUsername(String username, Class<T> type);
```
```java
List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1",
UsernameOnly.class);
```

#### ✨ 중첩
```java
public interface NestedClosedProjection {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
```
```java
//MemberRepository
<T> List<T> findProjectionsByUsername(String username, Class<T> type);

//Test
List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

for(NestedClosedProjections nestedClosedProjections : result){
    String username = nestedClosedProjections.getUsername();
    String teamName = nestedClosedProjections.getTeam().getName();

    //SQL: Member는 이름만 가져오고, Team은 전체 다 가져옴, SQL 1개
}
```

**주의**
- 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
- 프로젝션 대상이 ROOT가 아니면 `LEFT OUTER JOIN` 처리
   - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산

**정리**
- 프로젝션 대상이 root 엔티티면 유용
- 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안됨
- 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용


---
### ✔️ `네이티브 쿼리`

- 가급적 네이티브 쿼리는 사용하지 않는게 좋음, 정말 어쩔 수 없을 때 사용
- 스프링 데이터 Projections 활용도 가능

#### ✨ 스프링 데이터 JPA 기반 네이티브 쿼리

- 페이징 지원
- 반환 타입
  - Object[]
  - Tuple
  - DTO(스프링 데이터 인터페이스 Projections 지원)
- 제약
  - Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
  - JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
  - 동적 쿼리 불가

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);
}
```
- JPQL은 위치 기반 파리미터를 1부터 시작하지만 네이티브 SQL은 0부터 시작
- 네이티브 SQL을 엔티티가 아닌 DTO로 변환은 하려면 DTO 대신 JPA TUPLE 조회
- 네이티브 SQL을 DTO로 조회할 때는 `JdbcTemplate` or `myBatis` 권장

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

#### ✨ Projections 활용
- 스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projections 활용

```java
public interface MemberProjection{
    Long getId();
    String getUsername();
    String getTeamName();
}
```
```java
//MemberRepository
@Query(value = "SELECT m.member_id as id, m.username, t.name as teamName " +
    "FROM member m left join team t ON m.team_id = t.team_id", 
    countQuery = "SELECT count(*) from member", 
    nativeQuery = true)
Page<MemberProjection> findByNativeProjection(Pageable pageable);
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


#### ✨ 동적 네이티브 쿼리

- 하이버네이트를 직접 활용
- 스프링 JdbcTemplate, myBatis, jooq같은 외부 라이브러리 사용

```java
//하이버네이트
String sql = "select m.username as username from member m";

List<MemberDto> result = em.createNativeQuery(sql)
    .setFirstResult(0)
    .setMaxResults(10)
    .unwrap(NativeQuery.class)
    .addScalar("username")
    .setResultTransformer(Transformers.aliasToBean(MemberDto.class))
    .getResultList();
```