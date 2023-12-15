package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain_basic {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            //등록
//            Member member = new Member();
//            member.setId(1L);
//            member.setName("HelloA");
//            em.persist(member);

            //조회
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());

            //삭제
//            Member findMember = em.find(Member.class, 1L);
//            em.remove(findMember);

            //수정
//            Member findMember = em.find(Member.class, 1L);
//            findMember.setName("HelloJPA");
            //em.persist(findMember)안써도 됨, 값만 바꿔도 수정됨

            
//            List<Member> result = em.createQuery("select m from Member as m", Member.class)
//                    .setFirstResult(1)
//                    .setMaxResults(8) //page할때 사용
//                    .getResultList();
//            for (Member member : result){
//                System.out.println("member.getName() = " + member.getName());
            
            
            
//            //비영속
//            Member member = new Member();
//            member.setId(101L);
//            member.setName("HelloJPA");
//            //영속
//            System.out.println("==Before==");
//            em.persist(member);
//            System.out.println("==After==");
//            Member findMember = em.find(Member.class, 101L); //commit전에 쿼리 안날라감, 영속성 컨텍스트로 들어감
//            System.out.println("findMember.getId() = " + findMember.getId());  //select 쿼리가 안날아감, 1차 캐시에 저장된 것 꺼냄
//            System.out.println("findMember.getName() = " + findMember.getName());



//            //영속
//            Member findMember1 = em.find(Member.class, 101L); //트랜잭션 처음 시작한다고 가정(이미 101L db 저장), db에서 영속성 컨텍스트에 올려둠, 쿼리 1개
//            Member findMember2 = em.find(Member.class, 101L); //영속성 컨텍스트에서 찾음
//
//            System.out.println("result = " + (findMember1 == findMember2));




//            //영속
//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "B");
//            em.persist(member1);
//            em.persist(member2);
//            System.out.println("==================");  //여기까지 쿼리 안날림, commit에서 쿼리 날림



//            //영속
//            Member member = em.find(Member.class, 150L);
//            member.setName("ZZZZZ");
//            //em.persist(member) 업데이트할 때 사용할 필요 없음
//            System.out.println("==================");



//            //플러시
//            Member member = new Member(200L, "member200");
//            em.persist(member);
//            em.flush();  //1차 캐시를 지우지 않음, 쓰기 지연 저장소가 반영됨
//            System.out.println("==================");




//            Member member = em.find(Member.class, 150L); //영속상태가 됨
//            member.setName("AAAAA");
//            em.detach(member);  //update쿼리가 commit에서 안나감, 영속성 컨텍스트에서 없어짐




            /**
             * 엔티티 매핑
             */
//            Member member = new Member();
//            member.setId(10L);
//            member.setUsername("A");
//            member.setRoleType(RoleType.ADMIN);  //ordinal이면 숫자 순서대로(0, 1..)
//
//            em.persist(member);


            /**
             * ID
             */
            Member_basic memberBasic = new Member_basic();
//            member.setId("ID_A");  //Identity는 내가 넣을 필요가 없음(자동으로 생성)
            memberBasic.setUsername("C");

            em.persist(memberBasic);



            tx.commit(); //영속상태라고 바로 db에 쿼리 주지 않음, 여기서 db에 쿼리 날아감

        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
