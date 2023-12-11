package hello.core.order;

import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    
    //AppConfig의 OrderService return을 null로 바꿔줌
    //수정자는 누락이 될 수 있음, 수정자는 null point execption 오류 발생
    //생성자는 컴파일 오류가 나게 됨
    @Test
    void createOrder(){
        MemoryMemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "name", Grade.VIP));


//        OrderServiceImpl orderService = new OrderServiceImpl();    //이러면 오류 발생(생성자, 수정자 둘다 오류는 발생함)
        OrderServiceImpl orderService = new OrderServiceImpl(memberRepository, new FixDiscountPolicy());   //생성자 주입으로 오류 안나게
        Order order = orderService.createOrder(1L, "itemA", 10000);
        assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }

}