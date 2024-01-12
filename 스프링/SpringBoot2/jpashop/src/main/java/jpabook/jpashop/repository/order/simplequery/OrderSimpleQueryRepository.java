package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +  //JPA는 식별자로 왔다갔다하기때문에 엔티티 o를 넣지 못함
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
    }
}
