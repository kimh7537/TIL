package jpabook.jpashop;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {


    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            Order order = new Order();
//            order.addOrderItem(new OrderItem());

//            OrderItem orderItem = new OrderItem();   //양방향이 아닌 단방향이라도 개발에 큰 문제는 없음
//            orderItem.setOrder(order);               //양방향 하는 이유는 개발상의 편의
//            em.persist(orderItem);

            tx.commit();

        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();

    }
}
