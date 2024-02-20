#(JPA)8.객체지향쿼리언어1

---
## ✏️ `객체지향쿼리언어`
### ✔️ `JPQL`
```java
List<MemberE> result = em.createQuery("select m From MemberE m where m.username like '%kim%", MemberE.class).getResultList();

for(MemberE memberE : result){
    System.out.println("memberE = " + memberE);
}
```
- 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않음


### ✔️ `Criteria`
- JPQL은 동적쿼리를 만들기 어려움->`criteria`사용하기
```java
//Criteria 사용 준비
CriteriaBuilder cb = em.getCriteriaBuilder();

CriteriaQuery<Member> query = cb.createQuery(Member.class);

//루트 클래스 (조회를 시작할 클래스)
Root<Member> m = query.from(Member.class);
//쿼리 생성 CriteriaQuery<Member> cq =
CriteriaQuery<Member> cq = query.select(m);

String username = "kiki";
if (username != null) {
        cq.where(cb.equal(m.get("username"), "kim"));
}
//이렇게 동적쿼리를 사용하기가 용이함

List<Member> resultList = em.createQuery(cq).getResultList();
```
- 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
- JPA의 공식 기능임
- 단점: 너무 복잡하고 실용성이 없음, QueryDSL 사용 권장


### ✔️ `QueryDSL`
```java
//select m from Member m where m.age > 18
JPAFactoryQuery query = new JPAQueryFactory(em);
QMember m = QMember.member;
List<Member> list = query.selectFrom(m)
    .where(m.age.gt(18))
    .orderBy(m.name.desc())
    .fetch();
```
- 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
- 컴파일 시점에 문법 오류를 찾을 수 있음
- 동적쿼리 작성 편리함



### ✔️ `네이티브 SQL`
- JPA가 제공하는 SQL을 직접 사용하는 기능
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능 사용가능
- ex.) 오라클 CONNECT BY 등

```java
String sql = “SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = ‘kim’";

List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();
```

### ✔️ `JDBC 직접 사용, SpringJdbcTemplate`
- JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스등을 함께 사용 가능
- 단 영속성 컨텍스트를 적절한 시점에 강제로 플러시 필요
- ex.) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시



> query날아갈때 자동으로 flush해줌(JPQL, Criteria 등등)
---
---
## ✏️ `JPQL - 기본문법`
### ✔️ `기본 문법`

![Alt text](image/image-54.png)

- `select m from Member as m where m.age > 18`
- 엔티티와 속성은 대소문자 구분함 (Member, age)
- JPQL 키워드는 대소문자 구분X (SELECT, FROM, where)
- **엔티티 이름 사용, 테이블 이름이 아님(Member)**
- **별칭은 필수(m) (as는 생략가능)**

```java
select
    COUNT(m), //회원수
    SUM(m.age), //나이 합
    AVG(m.age), //평균 나이
    MAX(m.age), //최대 나이
    MIN(m.age) //최소 나이
from Member m
```
---

### ✔️ `TypeQuery, Query`
- `TypeQuery`: 반환 타입이 명확할 때 사용
- `Query`: 반환 타입이 명확하지 않을 때 사용
```java
Member member = new Member();
member.setUsername("member1");
member.setAge(10);
em.persist(member);

TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class); //뒤에 Member.class는 반환타입
TypedQuery<String> query1 = em.createQuery("select m.username from Member m", String.class);
Query query2 = em.createQuery("select m.username, m.age from Member m"); //타입정보를 받을 수 없을때
```
---
### ✔️ 결과조회 API

- `query.getResultList()`: 결과가 하나 이상일 때, 리스트 반환
    - 결과가 없으면 빈 리스트 반환
- `query.getSingleResult()`: 결과가 정확히 하나, 단일 객체 반환
    - 결과가 없으면: `javax.persistence.NoResultException`
    - 둘 이상이면: `javax.persistence.NonUniqueResultException`

```java
TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
List<Member> resultList = query.getResultList(); //값이 여러개

TypedQuery<Member> query = em.createQuery("select m from Member m where m.id =10 ", Member.class);
Member result = query.getSingleResult(); //값이 1개
```
---
### ✔️ 파리미터 바인딩
- 1. 이름기준
```java
Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
    .setParameter("username", "member1")
    .getSingleResult();
```
- 2. 위치기준
   - 위치기준은 사용지양하기
```java
SELECT m FROM Member m where m.username=?1
query.setParameter(1, usernameParam);
```


---
### ✔️ `프로젝션(SELECT)`
- `SELECT` 절에 조회할 대상을 지정하는 것
- 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터 타
입)

   - `SELECT m FROM Member m` -> 엔티티 프로젝션
   - `SELECT m.team FROM Member m` -> 엔티티 프로젝션
   - `SELECT m.address FROM Member m` -> 임베디드 타입 프로젝션
   - `SELECT m.username, m.age FROM Member m` -> 스칼라 타입 프로젝션
- `DISTINCT`로 중복 제거가능
```java
//1~4 중복사용
Member member = new Member();
member.setUsername("member1");
member.setAge(10);
em.persist(member);

em.flush();
em.clear();

//1(JPQL에서 flush발생)
List<Member> result = em.createQuery("select m from Member m", Member.class)
        .getResultList(); 
//엔티티들(Member 리스트)이 반환됨, 모두 영속성 컨텍스트에서 관리됨
Member findMember = result.get(0);
findMember.setAge(20); 
//영속성컨텍스트에 존재하는 Member age가 20살로 변경됨
```
```java
//2-1
List<Team> result = em.createQuery("select m.team from Member m ", Team.class)
        .getResultList(); 
//실행하면 이너조인으로 팀 테이블에서 함께 찾아옴

//2-2
List<Team> result1 = em.createQuery("select m.team from Member m join m.team t ", Team.class)
        .getResultList(); 
//위 코드랑 나가는 sql은 같음, 그러나 jpql은 나가는 sql이랑 비슷하게 작성하는 것이 좋아서 다시 작성함
```
```java
//3
em.createQuery("select o.address from Order o", Address.class)
    .getResultList(); 
//from Address이런식으로 하는것은 불가능, 값타입의 한계, 소속된 엔티티에서 찾아야함
```
```java
//4
em.createQuery("select distinct m.username, m.age from Member m")
    .getResultList();
//타입을 빼야함
```

**프로젝션 여러값 조회**
- `SELECT m.username, m.age FROM Member m`
- 1. `Query 타입`으로 조회
- 2. `Object[] 타입`으로 조회
- 3. `new 명령어`로 조회
    - 단순 값을 DTO로 바로 조회
    - 패키지 명을 포함한 전체 클래스 명 입력
    - 순서와 타입이 일치하는 `생성자` 필요
```java
//1
List resultList = em.createQuery("select distinct m.username, m.age from Member m")
    .getResultList();
Object o = resultList.get(0);
Object[] result = (Object[]) o; //배열 첫 번째 username, 두 번째 age
System.out.println("result[0] = " + result[0]);
System.out.println("result[0] = " + result[1]);

//2
List<Object[]> resultList = em.createQuery("select distinct m.username, m.age from Member m")
    .getResultList();
Object[] result = resultList.get(0);System.out.println("result[0] = " + result[0]);
System.out.println("result[0] = " + result[1]);

//3
List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
    .getResultList(); 
//MemberDTO클래스의 생성자를 통해 만들어짐
MemberDTO memberDTO = result.get(0);
System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
```
```java
public class MemberDTO {

    private String username;
    private int age;

    public MemberDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }
    ...
}
```

---
### ✔️ `페이징`
- `setFirstResult(int startPosition)` : 조회 시작 위치
(0부터 시작)
- `setMaxResults(int maxResult)` : 조회할 데이터 수

```java
for(int i=0 ; i < 100 ; i++){
    Member member = new Member();
    member.setUsername("member" + i);
    member.setAge(i);
    em.persist(member);
}

em.flush();
em.clear();

List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
    .setFirstResult(1) 
//0이 아니라 1이라서 두 번째 값 들고 나옴, age=99아니고, 98이 나옴
    .setMaxResults(10)
    .getResultList();
System.out.println("result.size() = " + result.size()); //10개

for(Member member1 : result){
    System.out.println("member1 = " + member1); // member=98~89
}
```

- `MySQL`, `ORACLE`등 언어마다 방언이 다름

---
### ✔️ `조인`
- 내부 조인:<br>
`SELECT m FROM Member m [INNER] JOIN m.team t`
- 외부 조인:<br>
`SELECT m FROM Member m LEFT [OUTER] JOIN m.team t`
- 세타 조인:<br>
`select count(m) from Member m, Team t where m.username
= t.name`

```java
Team team = new Team();
team.setName("teamA");
em.persist(team);

Member member = new Member();
member.setUsername("member1");
member.setAge(10);
member.setTeam(team);
em.persist(member);

em.flush();
em.clear();

//내부 조인
String query = "select m from Member m inner join m.team t";
List<Member> result = em.createQuery(query, Member.class)
        .getResultList();
// Lazy로 설정안하면 SQL쿼리 2개 나감 
// jpql하나랑 team찾는 select쿼리, LAZY로 변경해서 사용하기


//세타 조인
String query = "select m from Member m, Team t where m.username = t.name";
List<Member> result = em.createQuery(query, Member.class)
        .getResultList();
```

**조인 - ON절**
1. 조인 대상 필터링
- 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
```java
String query = "select m from Member m left join m.team t on t.name = 'teamA'";
List<Member> result = em.createQuery(query, Member.class)
        .getResultList();
```

2. 연관관계 없는 엔티티 외부 조인
- 회원의 이름과 팀의 이름이 같은 대상 외부 조인
- `m.team`이런식으로 사용x
```java
String query = "select m from Member m left join Team t on m.username = t.name";
List<Member> result = em.createQuery(query, Member.class)
        .getResultList();
```

---
### ✔️ `서브쿼리`

- `[NOT]EXISTS` (subquery): 서브쿼리에 결과가 존재하면 참
- `{ALL | ANY | SOME}` (subquery)
    - `ALL` 모두 만족하면 참
    - `ANY, SOME`: 같은 의미, 조건을 하나라도 만족하면 참
- `[NOT] IN` (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참


**서브쿼리 예제**
```java
//나이가 평균보다 많은 회원
select m from Member m
where m.age > (select avg(m2.age) from Member m2)
//한 건이라도 주문한 고객
select m from Member m
where (select count(o) from Order o where m = o.member) > 0
//팀A 소속인 회원
select m from Member m
where exists (select t from m.team t where t.name = '팀A')
//전체 상품 각각의 재고보다 주문량이 많은 주문들
select o from Order o
where o.orderAmount > ALL (select p.stockAmount from Product p)
//어떤 팀이든 팀에 소속된 회원
select m from Member m
where m.team = ANY (select t from Team t)
```

**서브 쿼리 한계**
- **JPA는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능**
- `SELECT` 절도 가능(하이버네이트에서 지원)
- `FROM` 절의 서브 쿼리는 현재 JPQL에서 불가능
     - 조인으로 풀 수 있으면 풀어서 해결
```java
String query = "select (select avg(m1.age) From Member m1) as avgAge from Member m join Team t on m.username = t.name";
//하이버네이트에서 가능
```

---
### ✔️ `타입 표현`
- 문자: `‘HELLO’, ‘She’’s’`
- 숫자: `10L(Long), 10D(Double), 10F(Float)`
- Boolean: `TRUE, FALSE`
- ENUM: `jpabook.MemberType.Admin` (패키지명 포함)
- 엔티티 타입: `TYPE(m) = Member` (상속 관계에서 사용)

```java
//1
String query = "select m.username, 'HELLO', TRUE From Member m" +
        "where m.type = jpql.MemberType.ADMIN";
List<Object[]> result = em.createQuery(query)
        .getResultList();

for(Object[] objects : result){
    System.out.println("objects[0] = " + objects[0]);
    System.out.println("objects[1] = " + objects[1]); //HELLO
    System.out.println("objects[2] = " + objects[2]); //true
}
//2
String query = "select m.username, 'HELLO', TRUE From Member m" +
        "where m.type = :userType";
List<Object[]> result = em.createQuery(query)
        .setParameter("userType", MemberType.ADMIN)
        .getResultList();

//3
//상속관계 Item -> Book, Movie, Album 
//DiscriminatorColumn 값 이용
em.createQuery("select i from Item i where type(i) = Book ", AbstractReadWriteAccess.Item.class);
```

**SQL과 문법이 같은 식**
- EXISTS, IN
- AND, OR, NOT
- =, >, >=, <, <=, <>
- BETWEEN, LIKE, IS NULL
```java
String query = "select m.username, 'HELLO', TRUE From Member m" +
        "where m.age between 0 and 10";
```

---
### ✔️ `조건식`
- 기본 CASE식
```java
String query =
        "select" +
            "case when m.age <= 10 then '학생요금' "+
            "     when m.age >= 60 then '경로요금' "+
            "     else '일반요금' " +
            " end " +
        "from Member m";
List<String> result = em.createQuery(query, String.class)
        .getResultList();
```
- 단순 CASE 식
```java
select
    case t.name
        when '팀A' then '인센티브110%'
        when '팀B' then '인센티브120%'
        else '인센티브105%'
    end
from Team t
```

- `COALESCE`: 하나씩 조회해서 null이 아니면 반환
- `NULLIF`: 두 값이 같으면 null 반환, 다르면 첫번째 값 반환

```java
String query = "select coalesce(m.username, '이름 없는 회원') from Member m "; 
//username이 null이면 이름 없는 회원 반환
//있으면 그대로 나옴

String query = "select nullif(m.username, '관리자') from Member m "; 
//사용자 이름이 관리자면 null반환, 관리자의 이름을 숨겨야할 때 사용
//나머지는 본인의 이름을 반환
```



---
### ✔️ `JPQL 함수`
1. JPQL기본 함수
- CONCAT
- SUBSTRING
- TRIM
- LOWER, UPPER
- LENGTH
- LOCATE
- ABS, SQRT, MOD
- SIZE, INDEX(JPA 용도)
```java
String query = "select concat('a', 'b') From Member m"; 
//select 'a' || 'b'로 사용가능(하이버네이트)

String query = "select substring(m.username, 2, 3) From Member m";

String query = "select locate('de', 'abcdefg') From Member m"; //숫자 4(위치)(Integer)
List<Integer> result = em.createQuery(query, Integer.class).getResultList();

String query = "select size(t.members) From Team t"; 
//컬렉션의 크기를 알려줌
//index는 값타입 컬렉션 위치값구할때 사용, 안쓰는게 좋음
```

2. 사용자 정의 함수
- 하이버네이트는 사용전 방언에 추가해야 함
- 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록함
```java
String query = "select function('group_concat', m.username) From Member m"; //persistence에서 MyH2Dialect로 설정해주기
List<String> result = em.createQuery(query, String.class).getResultList();
//데이터를 한줄로 뽑아서 알려줌
//관리자1,관리자2 -> 이런느낌
//select group_concat(m.username) From Member m -> 하이버네이트
```
```java
public class MyH2Dialect extends H2Dialect {

    public MyH2Dialect(){
        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
}
```
