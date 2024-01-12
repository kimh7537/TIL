package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //protected 생성자
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private  Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //JPQL select o From order o;  -> SQL select * from order  (n(member) + 1(order))


//    @BatchSize(size = 100) //디테일하게 적용하고 싶을 때
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //persist(orderItemA)persist(orderItemB) persist(order)
    private List<OrderItem> orderItems = new ArrayList<>();   //위에 거를 persist(order)로 해줌, Order를 persist하면 강제로 리스트도 persist

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) //order저장할때 delivery도 저장해줌
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    //order_date(이름 자동으로 바꿔줌)
    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status;  //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==// -> 핵심적으로 컨트롤하는 쪽에 만들면 좋음
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }


    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
    
    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}