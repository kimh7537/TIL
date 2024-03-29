package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){
            em.persist(item);
        }else{
            em.merge(item);  //Item merge = em.merge(item) item이 영속상태로 변경x, return값(merge)이 영속상태
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);  //단건 작성은 이렇게, 여러개 찾는건 jpql
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }


}
