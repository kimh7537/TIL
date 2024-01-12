package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)  //스프링이랑 같이 테스트할 때 사용
@SpringBootTest   //스프링 부트를 띄운 상태로 실행
@Transactional    //test끝나면 commit안하고 rollback시킴(test에서만)
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
//    @Autowired EntityManager em;

    @Test
//    @Rollback(false)   //false면 rollback안하고 insert문 내보는 것 확인
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        //em.flush (영속성 컨텍스트 반영함/insert문이 나가면서 볼 수 있음)
        assertEquals(member, memberRepository.findOne(savedId));
    }
  

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예제() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim1");
        Member member2 = new Member();
        member2.setName("kim1");


        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다.

        //then
        fail("예외가 발생해야 한다."); //위에서 오류가 나면 여기로 내려오면 안됨
    }


}

