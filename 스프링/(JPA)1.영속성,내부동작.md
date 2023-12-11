# (JPA)1.영속성,내부동작

---
## ✏️ `영속성 컨텍스트`
### ✔️ 정의

![!\[Alt text\](image.png)](image/image.png)
- 엔티티를 영구 저장하는 환경
- 영속성 컨텍스트는 논리적인 개념, 눈에 보이지 않음
- 엔티티메니저로 접근 가능
- 엔티티메니저와 영속성 컨텍스트는 N:1(스프링)


### ✔️ 생명주기

![Alt text](image/image-1.png)

1. `비영속(new/transient)`
영속성 컨텍스트와 관계가 없는 **새로운** 상태

2. `영속 (managed)`
영속성 컨텍스트에 관리되는 상태

3. `준영속 (detached)`
영속성 컨텍스트에 저장되었다가 분리된 상태

4. `삭제 (removed)`
삭제된 상태

---
#### ✨ 영속, 비영속
```java
public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
           //1. 비영속
           Member member = new Member();
           member.setId(101L);
           member.setName("HelloJPA");
           //2. 영속
           System.out.println("==Before==");
           em.persist(member);
           System.out.println("==After==");
           
           tx.commit(); //영속상태라고 바로 db에 쿼리 주지 않음, 여기서 db에 쿼리 날아감

        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
```
**결과**<br>
```
==Before==
==After==
insert ...
```
- `em.persist(member)`는 영속성 컨텍스트에 넣어주는 역할
- `tx.commit()`에서 DB에 쿼리를 날려줌

#### ✨ 준영속, 삭제
```java
//회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태
em.detach(member);

//객체를 삭제한 상태(삭제)
em.remove(member);
```
---
---
## ✏️ 영속성 컨텍스트의 이점
### ✔️ 1차캐시
![!\[Alt text\](image-1.png)](image/image-3.png)

**회원 조회**

1. 영속성 컨텍스트에 이미 존재한다면
```java
 Member findMember = em.find(Member.class, 101L); 
//commit전에 쿼리 안날라감, 영속성 컨텍스트로 들어감
System.out.println("findMember.getId() = " + findMember.getId());  //select 쿼리가 안날아감, 1차 캐시에 저장된 것 꺼냄
```

2. 영속성 컨텍스트에 없다면
```java
//영속
Member findMember1 = em.find(Member.class, 101L); //트랜잭션 처음 시작한다고 가정(이미 101L db 저장), db에서 영속성 컨텍스트에 올려둠(쿼리 날아감)
Member findMember2 = em.find(Member.class, 101L); //영속성 컨텍스트에서 찾음(쿼리 안날아감)
```

### ✔️ 동일성(identity) 보장
- 같은 transaction안에서 발생
- 위 코드랑 이어짐
- 1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공
```java
System.out.println("result = " + (findMember1 == findMember2));  //true
```


### ✔️ 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
**회원 등록**

![!\[Alt text\](image.png)](image/image-2.png)
- `em.persist(member1)`
- `em.persist(member2)`
- 바로 데이터베이스에 SQL문을 날리는 것이 아닌, 영속성 컨텍스트에 저장하고 쓰기 지연 저장소에 SQL문 저장(2개 저장됨)

![!\[Alt text\](image.png)](image/image-4.png)
- `tx.commit()`

```java
 //영속
Member member1 = new Member(150L, "A");
Member member2 = new Member(160L, "B");
em.persist(member1);
em.persist(member2);
System.out.println("==================");  //여기까지 Insert 쿼리 안날림

tx.commit(); //커밋하는 순간 DB에 INSERT SQL 보냄
```



### ✔️ 변경 감지(Dirty Checking)

**회원 수정**

![!\[Alt text\](image.png)](image/image-5.png)

- 트랜잭션 처음 시작한다고 가정했을 때, find에서 SELECT쿼리가 DB에 들어감, 영속성 컨텍스트에 저장함
- `tx.commit()`에서 Update쿼리가 날아감

```java
//영속
Member member = em.find(Member.class, 150L);
member.setName("ZZZZZ");
//em.persist(member) 업데이트할 때 사용할 필요 없음
System.out.println("==================");

tx.commit(); //영속상태라고 바로 db에 쿼리 주지 않음, 여기서 db에 쿼리 날아감
```

**영속성 컨텍스트는 지연 로딩(Lazy Loading)의 기능도 가지고 있음**











