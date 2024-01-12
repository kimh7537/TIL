package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //getMember()까지는 프록시객체, getName()으로 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
//        return orderRepository.findAllByString(new OrderSearch()).stream()
//                .map(SimpleOrderDto::new)
//                .collect(toList());


        //N + 1 문제 -> 1 + N(주문 2개) -> 1쿼리의 결과로 N번 쿼리가 추가 실행됨, 여기서는 1 + N(회원) + N(배송)
        List<Order> orders = orderRepository.findAllByString(new OrderSearch()); //ORDER 조회 -> SQL 1번 -> 결과 주문 수 2개

        // 루프 2번 -> 첫 번째 DTO 생성(쿼리 Member, Delivery 2개) -> 두 번째 DTO 생성(쿼리 2개)
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        // 모든 쿼리가 총 5번 나감
        // 만약 Member의 id가 같은 것이라면 쿼리가 1번만 날아감(영속성 컨텍스트 1차 조회하고 남아있음)
        return result;
    }



    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        //쿼리 총 통틀어서 1번 나감(fetch, select로 한방에 들고옴)
        List<Order> orders = orderRepository.findAllWithMemberDelivery1();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }


    @GetMapping("/api/v4/simple-orders")  //select dptj 내가 원하는 것만 들고옴, v3랑 차이점, sql은 1개라는 공통점
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }
    //v3,v4는 우위를 가리기 어려움 -> v3는 로직을 재활용 가능, v4가 성능 최적화에서 조금 더 좋음



    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }


}
