#(QueryDsl)1.문법

---
---
## ✏️ `기본문법`
### ✔️ `JPQL vs Querydsl`
```java
//QuerydslBasicTest
@Autowired
EntityManager em;

@BeforeEach
public void before() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);
}

@Test
public void startJPQL(){
    //member1을 찾아라
    String qlString =
            "select m from Member m " +
            "where m.username = :username";

    Member findMember = em.createQuery(qlString, Member.class)
            .setParameter("username", "member1")
            .getSingleResult();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}

@Test
public void startQuerydsl(){
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    QMember m = new QMember("m");

    Member findMember = queryFactory
            .select(m)
            .from(m)
            .where(m.username.eq("member1")) //파리미터 바인딩 자동 처리
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```
- `EntityManager` 로 `JPAQueryFactory` 생성
- Querydsl은 JPQL 빌더
- JPQL: 문자(실행 시점 오류), Querydsl: 코드(컴파일 시점 오류)
- JPQL: 파라미터 바인딩 직접, Querydsl: 파라미터 바인딩 자동 처리

```java
@PersistenceContext
EntityManger em;

JPAQueryFactory queryFactory; //필드로 빼서 사용 권장

@BeforeEach
public void before(){
    queryFactory = new JPAQueryFactory(em);
    ...
}

@Test
public void startQuerydsl(){
    QMember m = new QMember("m");
    ...
}
```
- 동시성 문제는 `JPAQueryFactory`를 생성할 때 제공하는 EntityManager(em)에 달려있음
- 스프링 프레임워크는 여러 쓰레드에서 동시에 같은
`EntityManager`에 접근해도, 트랜잭션 마다 별도의 영속성 컨텍스트를 제공하기 때문에, 동시성 문제는 걱정하
지 않아도 됨



---
### ✔️ `기본 Q-Type활용`
**Q클래스 인스턴스 사용 방법**
```java
QMember qMember = new QMember("m"); //별칭 직접 지정
QMember qMember = QMember.member; //기본 인스턴스 사용
```
```java
//QMember 클래스내부에 존재
 public static final QMember member = new QMember("member1");
```
- 별칭은 SQL에서 확인 가능
- 테이블을 조인해야 하는 경우가 아니면 기본 인스턴스를 사용하기

```java
@Test
public void startQuerydsl2(){
    Member findMember = queryFactory
            .select(member)
            .from(member)
            .where(member.username.eq("member1")) //파리미터 바인딩 자동 처리
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```
- 원래는 `select(QMember.member)`이런식으로 사용해야하지만 `static import`사용함


----
### ✔️ `검색 조건 쿼리`
```java
@Test
public void search(){
    Member findMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1")
                    .and(member.age.eq(10)))
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```
- `select` , `from` 을 `selectFrom` 으로 합칠 수 있음
- 검색 조건은 `.and()` , . `or()` 를 메서드 체인으로 연결할 수 있음

```java
@Test
public void searchAndParam(){
    Member findMember = queryFactory
            .selectFrom(member)
            .where(
                    member.username.eq("member1"),
                    member.age.eq(10)
            )
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```
- `where()`에 파라미터로 검색조건을 추가하면 `AND` 조건이 추가됨
- 이 경우 `null` 값은 무시 -> 메서드 추출을 활용해서 동적 쿼리를 깔끔하게 만들 수 있음


**JPQL이 제공하는 모든 검색 조건**
```java
member.username.eq("member1") // username = 'member1'
member.username.ne("member1") //username != 'member1'
member.username.eq("member1").not() // username != 'member1'

member.username.isNotNull() //이름이 is not null

member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
member.age.between(10,30) //between 10, 30

member.age.goe(30) // age >= 30
member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30

member.username.like("member%") //like 검색
member.username.contains("member") // like ‘%member%’ 검색
member.username.startsWith("member") //like ‘member%’ 검색
```

---
### ✔️ `결과 조회`

```java
@Test
public void resultFetch(){
    List<Member> fetch = queryFactory
            .selectFrom(member)
            .fetch();

    Member fetchOne = queryFactory
            .selectFrom(member)
            .fetchOne();

    Member fetchFirst = queryFactory
            .selectFrom(member)
            .fetchFirst();

    QueryResults<Member> results = queryFactory
            .selectFrom(member)
            .fetchResults(); //쿼리 2번 실행(count, content)
    results.getTotal();
    List<Member> content = results.getResults(); //데이터 꺼내기


    long count = queryFactory
            .selectFrom(member)
            .fetchCount();
}
```
- `fetch()` : 리스트 조회, 데이터 없으면 빈 리스트 반환
- `fetchOne()` : 단 건 조회
    - 결과가 없으면 : `null` 
    - 결과가 둘 이상이면 error
- `fetchFirst()` : `limit(1).fetchOne()`
- `fetchResults()` : 페이징 정보 포함, total count 쿼리 추가 실행
- `fetchCount()` : count 쿼리로 변경해서 count 수 조회
