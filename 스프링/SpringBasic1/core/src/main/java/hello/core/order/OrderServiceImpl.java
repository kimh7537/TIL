package hello.core.order;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
//@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

//    private final MemberRepository memberRepository = new MemoryMemberRepository();  //1번 방법
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

//    private final MemberRepository memberRepository = new MemoryMemberRepository();    //2번 방법
//    private DiscountPolicy discountPolicy;   //오류 발생, 인터페이스가 생성되지도 않음





//    private MemberRepository memberRepository;     //수정자 주입
//    private DiscountPolicy discountPolicy;
//    @Autowired   //필수적으로 넣어줘야함
//    public void setMemberRepository(MemberRepository memberRepository){
//        System.out.println("memberRepository = " + memberRepository);
//        this.memberRepository = memberRepository;
//    }
//    @Autowired  //수정자는 스프링 빈 등록할때 2번째 단계에서 등록(스프링 빈 등록 -> 의존관계 등록), 생성자는 필요없음/수정자는 DiscountPolicy가 스프링 빈 등록이 안되어있어도 사용가능(required = false)
//    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
//        System.out.println("discountPolicy = " + discountPolicy);
//        this.discountPolicy = discountPolicy;
//    }


    //생성자 주입
    private final MemberRepository memberRepository;   //3번 방법
    private final DiscountPolicy discountPolicy;

    @Autowired             //생성자는 스프링 빈 등록할때 자동으로 등록
    public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) {//생성자에 있는건 값을 넣어주는게 관례임
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }


//    필드 주입, 생성자 필요없음
//    @Autowired private MemberRepository memberRepository;
//    @Autowired private DiscountPolicy discountPolicy;




//    //일반 메서드 주입
//    private MemberRepository memberRepository;
//    private DiscountPolicy discountPolicy;
//    @Autowired
//    public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
//        this.memberRepository = memberRepository;
//        this.discountPolicy = discountPolicy;
//    }
    
    

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }


    //Test
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
