#(QueryDsl)3.JPA와querydsl

---
---
## ✏️ `순수 JPA와 Querydsl`
```java
@Repository
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
    ...save, findById등등
}
```
**JPAQueryFactory 스프링 빈 등록**
- `JPAQueryFactory` 를 스프링 빈으로 등록해서 주입받아 사용해도됨
```java
@Bean
JPAQueryFactory jpaQueryFactory(EntityManager em) {
    return new JPAQueryFactory(em);
}

@Repository
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }
}
```
- 동시성 문제 발생하지 않음
- 스프링이 주입해주는 엔티티 매니저는 실제 동작 시점에 진짜 엔티티 매니저를 찾아주는 프록시용 가짜 엔티티 매니저
- 가짜 엔티티 매니저는 실제 사용 시점에 트랜잭션 단위로 실제 엔티티 매니저(영속성 컨텍스트)를 할당해줌


### ✔️ `동적 쿼리와 성능 최적화 조회`

```java
@Data
public class MemberTeamDto {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
```
```java
@Data
public class MemberSearchCondition {
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
```

#### ✨ `Builder`
```java
//MemberJpaRepository
public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
    BooleanBuilder builder = new BooleanBuilder();

    if (StringUtils.hasText(condition.getUsername())) {
        //들어오는 값이 null이나 ""빈 문자열인지 점검
        builder.and(member.username.eq(condition.getUsername()));
    }
    if (StringUtils.hasText(condition.getTeamName())) {
        builder.and(team.name.eq(condition.getTeamName()));
    }
    if (condition.getAgeGoe() != null) {
        builder.and(member.age.goe(condition.getAgeGoe()));
    }
    if (condition.getAgeLoe() != null) {
        builder.and(member.age.loe(condition.getAgeLoe()));
    }

    return queryFactory
        .select(new QMemberTeamDto(
                member.id,
                member.username,
                member.age,
                team.id,
                team.name))
        .from(member)
        .leftJoin(member.team, team)
        .where(builder)
        .fetch();
}
```
```java
@Test
public void searchTest(){
        ...Team, Member생성후 persist

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");
        //모든 조건이 없다면 -> 쿼리가 모든 데이터를 가지고 옴

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);
        assertThat(result).extracting("username").containsExactly("member4");
}
```
- 만약 `condition.setXxx()`과 같은 조건을 아무것도 설정하지 않으면 SQL쿼리가 모든 데이터를 들고옴 -> 주의하기
- `QMemberTeamDto` 는 생성자를 사용하기 때문에 필드 이름을 맞추지 않아도 돼서 `member.id`만 적음


#### ✨ `Where절 파라미터`


---
### ✔️ `조회 API 컨트롤러`

