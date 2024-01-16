#(SpringDataJPA)1.공통인터페이스,쿼리메소드

---
---
## ✏️ `도메인 설정`
```java
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) //연관관계 필드는 가급적 toString하지 말기
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username")
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

}
```
- `@NoArgsConstructor AccessLevel.PROTECTED`: 기본 생성자, PROTECTED
- `@ToString`은 가급적 내부 필드만(연관관계 없는 필드만)

```java
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
```
- Member와 Team은 양방향 연관관계, `Member.team` 이 연관관계의 주인, `Team.members` 는 연관관계의 주인이 아님
- 따라서 `Member.team` 이 데이터베이스 외래키 값을 변경, 반대편은 읽기만 가능
```java
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity(){
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

        //초기화
        em.flush();
        em.clear();

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) { //쿼리 3개, Member 1번 Team 2번
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        }
    }
}
```
- 모든 `Member` 들고오는 SQL 쿼리 1개
- `member.getTeam()`으로 프록시 상태 초기화 하면서 SQL쿼리 2개 날림(`@ToString`이 존재하므로, 호출하면서 바로 초기화)

---
---
## ✏️ `공통 인터페이스 기능`
### ✔️ `순수 JPA 기반 리포지토리 만들기`
> JPA에서 수정은 변경감지 기능을 사용하면 됨.
트랜잭션 안에서 엔티티를 조회한 다음에 데이터를 변경하면, 트랜잭션 종료 시점에 변경감지 기능이 작동해서 변경된 엔티티를 감지하고 UPDATE SQL을 실행
```java

```
```java
@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    //JPA는 변경감지로 업데이트가 발생하므로, 업데이트를 따로 만들 필요는 없음
    
    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count(){
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
```
- `TeamJpaRepository`도 위 코드와 거의 동일함
```java
@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //findMember1.setUsername("member1111"); //Update, 변경감지 기능을 이용
        
        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }
}
```

---
### ✔️ `공통 인터페이스`
#### ✨ 설정
![Alt text](image/image-70.png)

`org.springframework.data.repository.Repository` 를 구현한 클래스는 스캔 대상
MemberRepository 인터페이스가 동작한 이유
실제 출력해보기(Proxy)
memberRepository.getClass() class com.sun.proxy.$ProxyXXX
`@Repository` 애노테이션 생략 가능
컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리

#### ✨ 적용

#### ✨ 분석



---
---
## ✏️ `쿼리 메서드 기능`
### ✔️ `V1: 엔티티 직접 노출`

- `By`하고 아무것도 없으면 전체 조회
- `By`뒤에 `where`에 넣을 컨디션을 넣어주기

- `findTop3HelloBy`



- `NamedQuery`의 장점: 애플리케이션 로딩 시점에 오류가 있으면 오류 발생시킴



//7
- `Optional` 값이 있는지 없는지 모를때 사용 권장




//11
`List<Member> findTop3ByAge(age)`; 페이징 안넘길때, 단순 3건만 조회할때


