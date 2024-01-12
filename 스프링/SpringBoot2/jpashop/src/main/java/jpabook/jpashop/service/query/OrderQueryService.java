//package jpabook.jpashop.service.query;
//
//import jpabook.jpashop.api.OrderApiController;
//import jpabook.jpashop.domain.Address;
//import jpabook.jpashop.domain.Order;
//import jpabook.jpashop.domain.OrderItem;
//import jpabook.jpashop.domain.OrderStatus;
//import jpabook.jpashop.repository.OrderRepository;
//import lombok.Data;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static java.util.stream.Collectors.toList;
//
//@Service
//@Transactional(readOnly = true)  //읽기 전용
//@RequiredArgsConstructor
//public class OrderQueryService {
//
//    private final OrderRepository orderRepository;
//
//    public List<OrderDto> ordersV3(){
//        List<Order> orders = orderRepository.findAllWithItem();
//
//        List<OrderDto> result = orders.stream()
//                .map(o -> new OrderDto(o))
//                .collect(toList());
//        return result;
//    }
//
//
//
//    @Data
//    static class OrderDto{
//
//        private Long orderId;
//        private String name;
//        private LocalDateTime orderDate;
//        private OrderStatus orderStatus;
//        private Address address;
//        //        private List<OrderItem> orderItems; //Dto안에 엔티티가 있으면 안됨, 엔티티가 내부로 노출되면 안됨, 엔티티에 대한 의존을 끊어야함, OrderItem도 Dto로 바꾸기
//        private List<OrderItemDto> orderItems;
//
//        public OrderDto(Order order) {
//            orderId = order.getId();
//            name = order.getMember().getName();
//            orderDate = order.getOrderDate();
//            orderStatus = order.getStatus();
//            address = order.getDelivery().getAddress();
//
////            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); //초기화, 이거 안하면 orderItem null로 나옴
////            orderItems = order.getOrderItems();
//
//            orderItems = order.getOrderItems().stream()
//                    .map(orderItem -> new OrderItemDto(orderItem))
//                    .collect(toList());
//        }
//    }
//
//    @Getter
//    static class OrderItemDto{
//
//        private  String itemName; //상품명
//        private int orderPrice;   //주문가격
//        private int count;        //주문수량
//
//        public OrderItemDto(OrderItem orderItem) {
//            itemName = orderItem.getItem().getName();
//            orderPrice = orderItem.getItem().getPrice();
//            count = orderItem.getCount();
//        }
//    }
//
//}
