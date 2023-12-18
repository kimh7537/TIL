#(JPA)6.프록시와연관관계관리

---
## ✏️ `프록시`
### ✔️ 조인전략(각각 테이블 변환)



```java
Member member = em.find(Member.class, 1L);
printMember(member);
printMemberAndTeam(member);  //상황에 따라 member만 쿼리로 들고 나오고 싶거나/member,team한번에 둘다 쿼리로 들고나오고 싶을때 상황이 다름
```

```java
//            Member member= new Member();
//            member.setUsername("hello");
//            em.persist(member);
//
//            em.flush();
//            em.clear();

            //1. member와 team을 JPA가 join해서 다 들고옴
//            Member findMember = em.find(Member.class, member.getId());
            
            //2. select쿼리가 안날아감
//            Member findMember = em.getReference(Member.class, member.getId());

            //3. select쿼리가 날아감(실제 사용되는 시점, username시점)(id는 이미 존재하기때문에 username시점에 쿼리 나감)
//            Member findMember = em.getReference(Member.class, member.getId());
//            System.out.println("findMember.getClass() = " + findMember.getClass());   //Member$HibernateProxy... (가짜)
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.username = " + findMember.getUsername()); //db에 있는것 들고오기 위해 쿼리 날림
```

**특징**
```java
            //특징(1~까지 공통사용)
            Member member1 = new Member();
            member1.setUsername("member1");
            em.persist(member1);
            Member member2 = new Member();
            member2.setUsername("member2");
            em.persist(member2);

            em.flush();
            em.clear();

            //1
//            Member m1 = em.find(Member.class, member1.getId());
//            Member m2 = em.find(Member.class, member2.getId());
//            System.out.println("(m1.getClass() == m2.getClass()) = " + (m1.getClass() == m2.getClass())); // true
            //2
//            Member m1 = em.find(Member.class, member1.getId());
//            Member m2 = em.getReference(Member.class, member2.getId());
//            System.out.println("(m1.getClass() == m2.getClass()) = " + (m1.getClass() == m2.getClass())); //false
//            System.out.println("(m1.getClass() == m2.getClass()) = " + (m1 instanceof Member)); //true
//            System.out.println("(m1.getClass() == m2.getClass()) = " + (m2 instanceof Member)); //true
            
            //3
//            Member m1 = em.find(Member.class, member1.getId());
//            System.out.println("m1.getClass() = " + m1.getClass()); //member객체, 영속성 컨텍스트에 올라가게 됨
//
//            Member reference = em.getReference(Member.class, member1.getId());
//            System.out.println("reference.getClass() = " + reference.getClass()); //실제 엔티티 객체, 영속성 컨텍스트에 올라와 있기 때문
//
//            System.out.println("(m1 == reference) = " + (m1 == reference)); //true, 같은 영속성컨텍스트 안에서 가져오고,pk같으면 jpa는 true반환

            //4
//            Member m1 = em.getReference(Member.class, member1.getId()); //proxy
//            Member reference = em.getReference(Member.class, member1.getId()); //proxy
//            System.out.println("(m1 == reference) = " + (m1 == reference)); //true

            //4-2
//            Member refMember = em.getReference(Member.class, member1.getId());//proxy초기화
//            Member findMember = em.find(Member.class, member1.getId()); //실제 DB에 select쿼리 날아감, 그러나 proxy가 반환됨
//            System.out.println("(m1 == reference) = " + (refMember == findMember)); //true(JPA가 true보장해주기 위해 값들 조정함)


            //5
//            Member refMember = em.getReference(Member.class, member1.getId());
//            System.out.println("refMember.getClass() = " + refMember.getClass()); //proxy
//            em.detach(refMember); //영속성컨텍스트에서 관리안함
//            em.clear();
//            em.close();
//            System.out.println("refMember.getUsername() = " + refMember.getUsername()); //exception이라서 exception보기
            
            //프록시 확인. 6
//            Member refMember = em.getReference(Member.class, member1.getId());
//            System.out.println("emf.getPersistenceUnitUtil().isLoaded(refMember) = " + emf.getPersistenceUnitUtil().isLoaded(refMember)); //false

            Member refMember = em.getReference(Member.class, member1.getId());
            refMember.getUsername(); //강제초기화한것임
            Hibernate.initialize(refMember); //좀 세려된 강제초기화 방법
            System.out.println("emf.getPersistenceUnitUtil().isLoaded(refMember) = " + emf.getPersistenceUnitUtil().isLoaded(refMember)); //true, username탐색하면서 초기화함

```

---
## ✏️ `즉시로딩과 지연로딩`
### ✔️ 조인전략(각각 테이블 변환)