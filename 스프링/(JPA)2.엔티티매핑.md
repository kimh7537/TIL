# (JPA)2.엔티티매핑

---
## ✏️ `객체와 테이블 매핑`
### ✔️ `@Entity`

- `@Entity`가 붙은 클래스는 JPA가 관리, 엔티티라 부름
- JPA를 사용해서 테이블과 매핑할 클래스는 `@Entity`필수

- `기본 생성자 필수`(파라미터가 없는 public 또는 protected 생성자)
- final 클래스, enum, interface, inner 클래스 사용X
- 저장할 필드에 final 사용 X

**속성: name**
- JPA에서 사용할 엔티티 이름을 지정
- 기본값: 클래스 이름을 그대로 사용(예: Member)
- 같은 클래스 이름이 없으면 가급적 기본값을 사용하기

### ✔️ `@Table`
- 엔티티와 매핑할 테이블 지정
![!\[Alt text\](image.png)](image/image-7.png)

```java
@Entity(name = "Member")
//@Table(name = "USER")
public class Member {
    //기본 생성자가 무조건 하나 있어야함
    public Member() {
    }
}
```

---
## ✏️ `데이터베이스 스키마 자동 생성`
### ✔️ 설명

- DDL을 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
- 개발 장비에서만 사용/운영서버에서 사용하지 않거나 다듬고 사용

- **운영 장비에는 절대 create, create-drop, update 사용하면 안됨**
- 개발 초기 단계는 create 또는 update
- 테스트 서버는 update 또는 validate
- 스테이징과 운영 서버는 validate 또는 none

![!\[Alt text\](image.png)](image/image-8.png)

- `update`는 지우는건 불가능(추가만 가능)

### ✔️ DDL 생성 기능
- 제약조건 추가: 회원 이름은 **필수**, 10자 초과x

`@Column(nullable = false, length = 10)`

- 유니크 제약조건 추가

`@Table(uniqueConstraints = {@UniqueConstraint( name = "NAME_AGE_UNIQUE",
columnNames = {"NAME", "AGE"} )})`

- **DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고
JPA의 실행 로직에는 영향을 주지 않음**

ex. `unique=true`는 DDL 생성할때만 영향/실행 영향x

---
## ✏️ `필드와 컬럼 매핑`
### ✔️ 기본기능

```java
   @Id
   private Long id;

   @Column(name = "name", nullable = false)
//    @Column(unique = true, length = 10)
   private String username;

   private Integer age;

   @Enumerated(EnumType.STRING)
   private RoleType roleType;

   @Temporal(TemporalType.TIMESTAMP)
   private Date createdDate;


//    private LocalDate testLocalDate;
//    private LocalDateTime testLocalDateTime;


   @Temporal(TemporalType.TIMESTAMP)
   private Date lastModifiedDate;

   @Lob
   private String description;
```
```java
Member member = new Member();
member.setId(10L);
member.setUsername("A");
member.setRoleType(RoleType.ADMIN);  //ordinal이면 숫자 순서대로(0, 1..)

em.persist(member);
```

### ✔️ `@Column`
- 칼럼 매핑

![!\[Alt text\](image.png)](image/image-11.png)

- `unique`는 잘 쓰지 않음, 주로 `@Table`에서 사용하는 것 선호
- `unique=true` : column에서 사용하면 이름이 랜덤처럼 나오게 됨
- `@Table(uniqueConstraints = name)`은 이름을 줄 수 있어서 여기서 사용


### ✔️ `@Temporal`
- 날짜 타입 매핑

![!\[Alt text\](image.png)](image/image-9.png)

### ✔️ `@Enumerated`
- `enum` 타입 매핑

**ORDINAL 사용하지 말기**
- enum값이 추가되어 순서가 변경되는 경우, 데이터베이스 기존에 들어가 있던 순서 값들은 수정되지 않음->오류발생

![!\[Alt text\](image.png)](image/image-10.png)
 
### ✔️ `@Lob`
- 매핑하는 필드 타입이 문자면 CLOB, 나머지 BLOB
- 속성을 따로 지정하지 않음
- `BLOB`: `byte[], java.sql.BLOB`
- `CLOB`: `String, char[], java.sql.CLOB`



### ✔️ `@Transient`
- 특정 필드를 컬럼에 매핑하지 않음(매핑 무시)
- DB에 저장x, 조회x
- 메모리에서만 임시로 값을 보관하고 싶을 때 사용
```java
@Transient
private Integer temp;
```

---
---
## ✏️ `기본 키 매핑`
### ✔️ `@Id`
- 직접 할당: `@Id`만 사용
- 자동 생성(`@GeneratedValue`)

1. `IDENTITY`: 데이터베이스에 위임, MYSQL
2. `SEQUENCE`: 데이터베이스 시퀀스 오브젝트 사용, ORACLE
    - `@SequenceGenerator` 필요
3. `TABLE`: 키 생성용 테이블 사용, 모든 DB에서 사용
    - `@TableGenerator` 필요
    - `AUTO`: 방언에 따라 자동 지정, 기본값

---
### ✔️ `@GeneratedValue`
#### ✨ `IDENTITY`
- 기본 키 생성을 데이터베이스에 위임
- 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
(예: MySQL의 AUTO_ INCREMENT)
- JPA는 보통 트랜잭션 커밋 시점에 `INSERT SQL` 실행
- `AUTO_ INCREMENT`는 데이터베이스에 `INSERT SQL`을 실행한 이후에 ID 값을 알 수 있음
- `IDENTITY` 전략은 `em.persist()` 시점에 즉시 `INSERT SQL` 실행하고 DB에서 식별자를 조회
<br><br>
- 단점: SQL쿼리를 모아서 한번에 DB에 보내는 것이 불가능
- `em.persist(member)`: 여기서 INSERT SQL 실행
    - 바로 member.getId()하면 select쿼리 안날아가고 바로 조회가능


```java
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // private String id; identity는 String도 가능
```

```java
Member member = new Member();
//member.setId("ID_A");  //Identity는 내가 넣을 필요가 없음(자동으로 생성)
member.setUsername("C");
em.persist(member); //INSERT SQL 실행
```
---
#### ✨ `SEQUENCE`

- 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
- 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용

```java
@Table
@SequenceGenerator(
    name = "member_seq_generator", 
    sequenceName = "member_seq", //매핑할 데이터베이스 시퀀스 이름
    initialValue = 1, 
    allocationSize = 1)
...
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
private Long id;  //sequence

@Column(name = "name", nullable = false)
private String username;
```

![!\[Alt text\](image.png)](image/image-12.png)

- `em.persist(member)`하면 영속성컨텍스트에 값을 넣음<br>
   -> 이때 id값을 알아야 영속성 컨텍스트에 값을 넣을 수 있음<br>
   -> id값은 sequence에서 찾음
```java
System.out.println("===");
em.persist(member);
System.out.println("===");

// 결과
// ===
// call next value for MEMBER_SEQ
// 여기서 영속성 컨텍스트에 넣음
// ===
```

- SQL 쿼리는 `tx.commit`시점에 DB로 들어감
   - 쿼리를 모아서 한 번에 처리하는 것이 가능

> **allocationsize 50인이유**<br>
> - `em.persist()`한 번 호출할때 `allocationSize` 50씩 늘어남<br><br>
> 초기2번호출 DBSEQ -49 -> DB_SEQ = 1  <br>
> em.persist(member1); 현재값 1 | DB_SEQ = 51<br>
> em.persist(member2); 현재값 2 | DB_SEQ = 51 (db호출하지 않고 메모리 호출, 자리가 남음)<br>
> em.persist(member3); 현재값 3 | DB_SEQ = 51<br>

---
#### ✨ `TABLE`
- 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
- 장점: 모든 데이터베이스에 적용 가능
- 단점: 성능

```java
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1)

@Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
private Long id;

@Column(name = "name", nullable = false)
private String username;
```
![!\[Alt text\](image.png)](image/image-13.png)

- `sequence_name`의 value는 `MEMBER_SEQ`이 됨

![!\[Alt text\](image.png)](image/image-14.png)
![!\[Alt text\](image-1.png)](image/image-15.png)

> **권장하는 식별자 전략**
>- 기본 키 제약 조건: null 아님, 유일, 변하면 안됨
>- 미래까지 이 조건을 만족하는 자연키는 찾기 어려움, 대체키사용하기
>- 권장: Long형 + 대체키 + 키 생성전략 사용