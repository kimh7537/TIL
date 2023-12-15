package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
//            Team team = new Team();
//            team.setName("TeamA");
//            em.persist(team);
//
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setTeamId(team.getId());
//            em.persist(member);
//
//            Member findMember = em.find(Member.class, member.getId());
//            Long findTeamId = findMember.getTeamId();
//            Team findTeam = em.find(Team.class, findTeamId);


//            //팀 저장
//            Team team = new Team();
//            team.setName("TeamA");
//            em.persist(team);
//            //회원 저장
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setTeam(team); //단방향 연관관계 설정, 참조 저장
//            em.persist(member);
//
//            em.flush(); //db로 정보 바로 보내기
//            em.clear(); //영속성 컨텍스트 내용 지우기
//
//            //조회
//            Member findMember = em.find(Member.class, member.getId());
////            //참조를 사용해서 연관관계 조회
////            Team findTeam = findMember.getTeam();
////            System.out.println("findTeam.getName() = " + findTeam.getName());
//
//            //foreign key update
////            Team newTeam = em.find(Team.class, 100L);
////            findMember.setTeam(newTeam);
//
//
//            List<Member> members = findMember.getTeam().getMembers();
//            for(Member m : members){
//                System.out.println("m.getUsername() = " + m.getUsername());
//            }


//            /**
//             * 양방향 연관관계 주의점
//             */
//            Team team = new Team();
//            team.setName("TeamA");
////            team.getMembers().add(member);   //읽기 전용
//            em.persist(team);
//
//            Member member = new Member();
//            member.setUsername("member1");
//            member.changeTeam(team);
//            em.persist(member);
//
////            team.getMembers().add(member);   //사용안하고,플러시도 안했다면| 영속성 컨텍스트에서 바로 가져오기 때문에 값이 제대로 안나옴|연관관계 편의 메서드
//
////            team.addMember(member);   //이것도 가능, team이나 member 클래스 둘 중 하나만 만들기
//
////            em.flush();
////            em.clear();
//
//            Team findTeam = em.find(Team.class, team.getId());
//            List<Member> members = findTeam.getMembers();
//            for(Member m : members){
//                System.out.println("m.getUsername() = " + m.getUsername());
//            }



            Member member = new Member();
            member.setUsername("member1");

            em.persist(member);

            Team team = new Team();
            team.setName("teamA");

            team.getMembers().add(member);
            em.persist();


            tx.commit();

        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
